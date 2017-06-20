package at.ase.flink;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.hadoop.MongoOutput;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.io.MongoUpdateWritable;
import com.mongodb.hadoop.mapred.output.MongoRecordWriter;
import org.apache.hadoop.mapred.JobConf;
import org.bson.BSONObject;

import java.io.IOException;

/**
 * Created by gena on 16/06/17.
 */
public class CustomMongoRecordWriter<K, V> extends MongoRecordWriter<K, V> {

    private int roundRobinCounter = 0;
    private final DBCollection collections;

    public CustomMongoRecordWriter(DBCollection c, JobConf conf) {
        super(conf);
        this.collections = c;
    }

    @Override
    public void write(K key, V value) throws IOException {
        DBObject o = new BasicDBObject();
        if (value instanceof MongoUpdateWritable) {
            MongoUpdateWritable muw = (MongoUpdateWritable) value;

            try {
                DBCollection dbCollection = this.getDbCollectionByRoundRobin();
                dbCollection.update(new BasicDBObject(muw.getQuery()), new BasicDBObject(muw.getModifiers()), muw.isUpsert(), muw.isMultiUpdate());
            } catch (MongoException var6) {
                throw new IOException("can't write to mongo", var6);
            }
        } else {
            if (key instanceof BSONWritable) {
                o.put("_id", ((BSONWritable) key).getDoc());
            } else if (key instanceof BSONObject) {
                o.put("_id", key);
            } else {
                o.put("_id", BSONWritable.toBSON(key));
            }
            DBObject u = new BasicDBObject();
            if (value instanceof BSONWritable) {
                u.putAll(((BSONWritable) value).getDoc());
            } else if (value instanceof MongoOutput) {
                ((MongoOutput) value).appendAsValue(u);
            } else if (value instanceof BSONObject) {
                u.putAll((BSONObject) value);
            } else {
                u.put("$push", new BasicDBObject("value", new BasicDBObject("$each", BSONWritable.toBSON(value))));
            }

            try {
                DBCollection dbCollection = this.getDbCollectionByRoundRobin();
                synchronized (dbCollection) {
                    dbCollection.update(o, u, true, false);
                }
            } catch (MongoException var7) {
                throw new IOException("can't write to mongo", var7);
            }
        }
    }

    private synchronized DBCollection getDbCollectionByRoundRobin() {
        return this.collections;
    }
}
