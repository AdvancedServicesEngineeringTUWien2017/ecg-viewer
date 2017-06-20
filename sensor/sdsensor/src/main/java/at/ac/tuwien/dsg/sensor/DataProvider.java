package at.ac.tuwien.dsg.sensor;

import at.ac.tuwien.dsg.sensor.GenericDataInstance.IRecord;
import at.ac.tuwien.dsg.sensor.GenericDataInstance.ListRecord;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class DataProvider implements Runnable {

    public static DataProvider provider;
    private static final Object lock = new Object();
    private static Logger LOGGER = Logger.getLogger(DataProvider.class);
    private static String dataFile;
    private int serialNumberOfBatch = 0;
    private static final GenericDataInstance poisonPill = new GenericDataInstance("REPLACE_ME", new ArrayList<>(), -1);

    static {
        provider = new DataProvider();
    }

    public static DataProvider getProvider() {
////        synchronized (lock) {
//            if (provider == null) {
//                provider = new DataProvider();
//            }
        return provider;
//        }
    }

    public static DataProvider getProvider(String file) {
        dataFile = file;
        return provider;
    }

    private Queue<GenericDataInstance> dataInstances = new ArrayDeque<>();

    public void run() {

        //NOTE only to be used with single column data
        if (dataInstances.size() == 0) {
            LOGGER.info("Reading csv file to load data");
            //InputStreamReader reader = new InputStreamReader(new File(dataFile));//new InputStreamReader(DataProvider.class.getClassLoader().getResourceAsStream("/home/truong/myprojects/gittuwiendsg/iCOMOTSensors/java/genericsdsensor/src/sdsensor/target/data.csv"));
            try {
                BufferedReader br = new BufferedReader(new FileReader(dataFile));
                //need to use better library for CSV
                String firstLine = br.readLine();
                String headers[] = firstLine.split(",");
                String line;
                poisonPill.setID(headers[1]);

                while ((line = br.readLine()) != null) {
                    List<String> lines = new ArrayList<>();

                    for (int i = 0; i < 500 && line != null; i++) {
                        lines.add(line);
                        line = br.readLine();
                    }
                    List<IRecord> records = new ArrayList<>();
                    records.add(new ListRecord(headers[0], lines));

                    if (serialNumberOfBatch != 10) {
                        GenericDataInstance ginst = new GenericDataInstance(headers[1], records, serialNumberOfBatch++);

                        dataInstances.add(ginst);

                    } else {
                        serialNumberOfBatch++;
                    }
                }
                dataInstances.add(poisonPill);
                br.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                e.printStackTrace();
            }
        } else {
            LOGGER.info(String.format("Data stack not empty yet"));
        }
    }

    private DataProvider() {
    }

    public GenericDataInstance getNextInstance() {
        if (this.dataInstances.isEmpty()) {
            return null;
        } else {
            return this.dataInstances.remove();
        }
    }
}
