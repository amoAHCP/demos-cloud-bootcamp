package ch.trivadis.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by Andy Moncsek on 01.04.16.
 */
public class InitMongoDB {

    public static MongoClient initMongoData(Vertx vertx,JsonObject config) {
        MongoClient mongo;
        // Create a mongo client using all defaults (connect to localhost and default port) using the database name "demo".
        String connectionUrl = connectionURL();
        if (connectionUrl != null) {
            String dbName = config.getString("dbname", "vxmsdemo");
            mongo = MongoClient.createShared(vertx, new JsonObject().put("connection_string", connectionUrl).put("db_name", dbName));
        } else {
            mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));
        }
        // the load function just populates some data on the storage
        return mongo;
    }


    private static String connectionURL() {
        return "mongodb://mongo:27017";
    }


}
