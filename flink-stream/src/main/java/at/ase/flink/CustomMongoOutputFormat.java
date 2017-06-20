package at.ase.flink;

import com.mongodb.hadoop.mapred.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;

/**
 * Created by gena on 16/06/17.
 */
public class CustomMongoOutputFormat<K,V> extends MongoOutputFormat<K,V> {

    @Override
    public RecordWriter<K, V> getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress) {
        return new CustomMongoRecordWriter<>(MongoConfigUtil.getOutputCollection(job), job);
    }
}
