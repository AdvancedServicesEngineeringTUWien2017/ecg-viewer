package at.ase.flink;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.hadoop.mapred.HadoopOutputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;
import org.apache.hadoop.mapred.JobConf;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlinkTest {

    public static void main(String[] args) throws Exception {

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        HadoopOutputFormat<String, DBObject> hdof = new HadoopOutputFormat<>(
                new CustomMongoOutputFormat<>(), new JobConf());
        MongoConfigUtil.setOutputURI(hdof.getJobConf(), new MongoClientURI(parameterTool.getRequired("mongouri")));
        //Read from a socket stream at map it to StockPrice objects

        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(parameterTool.getRequired("host"))
                .setUserName(parameterTool.getRequired("user"))
                .setPassword(parameterTool.getRequired("password"))
                .setPort(parameterTool.getInt("port"))
                .setVirtualHost(parameterTool.getRequired("virtualhost"))
                .build();

        DataStream<DataEntriesWrapper> socketWrapperStream = env.addSource(new RMQSource<DataEntriesWrapper>(
                connectionConfig,
                parameterTool.getRequired("queuename"),
                true,
                new CustomDeserializationSchema()));

        socketWrapperStream = socketWrapperStream.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<DataEntriesWrapper>() {
            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(DataEntriesWrapper dataEntry, long l) {
                return new Watermark(dataEntry.getSerial());
            }

            @Override
            public long extractTimestamp(DataEntriesWrapper dataEntry, long l) {
                return dataEntry.getSerial();
            }
        });
        DataStream<QualityEntity> qualityStream = socketWrapperStream.map(new MapFunction<DataEntriesWrapper, QualityEntity>() {
            @Override
            public QualityEntity map(DataEntriesWrapper dataEntriesWrapper) throws Exception {
                QualityEntity entity = new QualityEntity();
                entity.setPrevious(dataEntriesWrapper.getSerial());
                entity.setId(dataEntriesWrapper.getId());
                return entity;
            }
        }).keyBy(new KeySelector<QualityEntity, String>() {
            @Override
            public String getKey(QualityEntity qualityEntity) throws Exception {
                return qualityEntity.getId();
            }
        }).reduce(new ReduceFunction<QualityEntity>() {
            @Override
            public QualityEntity reduce(QualityEntity qualityEntity, QualityEntity t1) throws Exception {
                //new entity stream
                if (qualityEntity.getAll() == 0L) {
                    qualityEntity.setMissed(qualityEntity.getPrevious());
                }
                if (t1.getPrevious() != -1L) {
                    qualityEntity.setAll(t1.getPrevious() + 1L);
                    qualityEntity.setMissed(qualityEntity.getMissed() + (t1.getPrevious() - qualityEntity.getPrevious() - 1L));
                } else {
                    qualityEntity.setPercentage(((double) qualityEntity.getAll() - qualityEntity.getMissed()) / (double) qualityEntity.getAll());
                }
                qualityEntity.setPrevious(t1.getPrevious());
                return qualityEntity;
            }
        });

        Pattern<QualityEntity, ?> triggerPattern = Pattern.<QualityEntity>begin("Trigger Event")
                .where(evt -> evt.getPrevious() == -1L);
        CEP.pattern(qualityStream, triggerPattern).select(new PatternSelectFunction<QualityEntity, QualityEntity>() {
            @Override
            public QualityEntity select(Map<String, QualityEntity> map) throws Exception {
                return map.get("Trigger Event");
            }
        }).addSink(new SinkFunction<QualityEntity>() {
            @Override
            public void invoke(QualityEntity entity) throws Exception {
                hdof.open(1, 1);
                BigDecimal bigDecimal = new BigDecimal(entity.getPercentage());
                bigDecimal = bigDecimal.setScale(4, RoundingMode.DOWN);
                DBObject data = new BasicDBObject("quality", bigDecimal.doubleValue());
                data.put("date", System.currentTimeMillis());
                hdof.writeRecord(new Tuple2<>(entity.getId(), new BasicDBObject("$set", data)));
            }
        });

        socketWrapperStream.addSink(new SinkFunction<DataEntriesWrapper>() {
            @Override
            public void invoke(DataEntriesWrapper dataEntry) throws Exception {
                if (dataEntry.getSerial() != -1L) {
                    hdof.open(1, 1);
                    DBObject data = new BasicDBObject("serial", dataEntry.getSerial().toString());
                    data.put("values", BSONWritable.toBSON(dataEntry.getEntryList().stream().map(DataEntry::getValue).collect(Collectors.toList())));
                    BasicDBList list = new BasicDBList();
                    list.add(data);
                    DBObject u = new BasicDBObject(
                            "$push", new BasicDBObject("values", new BasicDBObject("$each", list)));

                    hdof.writeRecord(new Tuple2<>(dataEntry.getId(), u));
                }
            }
        });

        DataStream<DataEntry> socketStockStream = socketWrapperStream
                .flatMap(new FlatMapFunction<DataEntriesWrapper, DataEntry>() {
                    @Override
                    public void flatMap(DataEntriesWrapper wrapper, Collector<DataEntry> collector) throws Exception {
                        for (DataEntry entry : wrapper.getEntryList()) {
                            collector.collect(entry);
                        }
                    }
                }).assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<DataEntry>() {
                    @Nullable
                    @Override
                    public Watermark checkAndGetNextWatermark(DataEntry dataEntry, long l) {
                        return new Watermark(dataEntry.getSerialNumber());
                    }

                    @Override
                    public long extractTimestamp(DataEntry dataEntry, long l) {
                        return dataEntry.getSerialNumber();
                    }
                });

        KeyedStream<DataEntry, String> sensorStream = socketStockStream.keyBy(DataEntry::getSensorId);

        DataStream<Tuple2<DataEntry, Double>> lowpass = sensorStream.countWindow(7, 1).apply(new WindowFunction<DataEntry, Tuple2<DataEntry, Double>, String, GlobalWindow>() {
            @Override
            public void apply(String s, GlobalWindow globalWindow, Iterable<DataEntry> iterable, Collector<Tuple2<DataEntry, Double>> collector) throws Exception {
                final double[] sum = {0.0};
                List<DataEntry> entryList = Lists.newArrayList(iterable);
                iterable.forEach(a -> sum[0] += a.getValue());
                double y_1 = sum[0] / entryList.size();
                if (entryList.size() == 7) {
                    double y_2 = entryList.get((entryList.size() + 1) / 2).getValue();
                    collector.collect(new Tuple2<>(entryList.get(entryList.size() - 1), y_2 - y_1));

                }

            }
        });

        DataStream<Tuple2<DataEntry, Boolean>> decision = lowpass.keyBy(new DataEntryKeySelector()).countWindow(25).apply(new RichWindowFunction<Tuple2<DataEntry, Double>, Tuple2<DataEntry, Boolean>, String, GlobalWindow>() {
            private transient ValueState<Double> threshold;
            private double alpha = 0.05;
            private double gamma = 0.15;

            @Override
            public void apply(String s, GlobalWindow globalWindow, Iterable<Tuple2<DataEntry, Double>> iterable, Collector<Tuple2<DataEntry, Boolean>> collector) throws Exception {
                final double[] sum = {0.0};
                Double currentThreshold = threshold.value();
                List<Tuple2<DataEntry, Double>> entryList = Lists.newArrayList(iterable);
                iterable.forEach(a -> sum[0] += Math.pow(a.f1, 2));

                if (currentThreshold == null) {
                    currentThreshold = 0.5 * sum[0];
                }

                if (sum[0] > currentThreshold) {
                    currentThreshold = (alpha * gamma * sum[0]) + ((1 - alpha) * currentThreshold);
                    threshold.update(currentThreshold);
                    collector.collect(new Tuple2<>(entryList.get(entryList.size() / 2).f0, true));
                } else {
                    collector.collect(new Tuple2<>(entryList.get(entryList.size() / 2).f0, false));

                }
            }

            @Override
            public void open(Configuration parameters) throws Exception {
                ValueStateDescriptor<Double> descriptor = new ValueStateDescriptor<>(
                        "threshold", // the state name
                        TypeInformation.of(new TypeHint<Double>() {
                        }));

                threshold = getRuntimeContext().getState(descriptor);
            }
        });

        decision.keyBy(new BooleanDataEntryKeySelector()).countWindow(100).apply(new WindowFunction<Tuple2<DataEntry, Boolean>, HeartRateEntry, String, GlobalWindow>() {
            @Override
            public void apply(String s, GlobalWindow globalWindow, Iterable<Tuple2<DataEntry, Boolean>> iterable, Collector<HeartRateEntry> collector) throws Exception {
                int between = 0;
                int detected = 0;
                for (Tuple2<DataEntry, Boolean> tuple2 : iterable) {
                    between++;
                    if (tuple2.f1) {
                        detected++;
                    }
                }

                //25*100 ==> 2500 entries ==> 5 seconds
                collector.collect(new HeartRateEntry(60.0 * (((double) detected) / ((double) between * 0.05)), s));

            }
        }).addSink(new RMQSink<>(
                connectionConfig,
                parameterTool.getRequired("heartqueuename"),
                new CustomHeartRateSerializationSchema()));
        env.execute("ECG-Stream");
    }

    private static class DataEntryKeySelector implements KeySelector<Tuple2<DataEntry, Double>, String> {

        @Override
        public String getKey(Tuple2<DataEntry, Double> dataEntry) throws Exception {
            return dataEntry.f0.getSensorId();
        }
    }

    private static class BooleanDataEntryKeySelector implements KeySelector<Tuple2<DataEntry, Boolean>, String> {

        @Override
        public String getKey(Tuple2<DataEntry, Boolean> dataEntry) throws Exception {
            return dataEntry.f0.getSensorId();
        }
    }

}

