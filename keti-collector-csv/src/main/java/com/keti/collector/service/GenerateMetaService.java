package com.keti.collector.service;

import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.InsertOneResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keti.collector.config.MongoDBConfiguration;


@Service
public class GenerateMetaService {

    private final MongoDBConfiguration mongoDBConfiguration;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GenerateMetaService(MongoDBConfiguration _mongoDBConfiguration, ObjectMapper _objectMapper) {
        this.mongoDBConfiguration = _mongoDBConfiguration;
        this.objectMapper = _objectMapper;
    }


    public JSONObject generatedByMeta(JSONObject _database, JSONObject _measurements) throws IOException, MongoException {
        String database = _database.get("commit").toString();
        String mainDomain = database.split("__")[0];
        String subDomain = database.split("__")[1];
        List<String> measurements = new ArrayList<>(
            objectMapper.readValue(
                _measurements.get("commits").toString(), new TypeReference<Map<String, Long>>(){}
            ).keySet());

        Map<String, Object> serviceResultMeta = new HashMap<>();

        MongoClient mongoClient = mongoDBConfiguration.getMongoConn();
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mainDomain);
        MongoIterable<String> mongoCollections = mongoDatabase.listCollectionNames();

        boolean collectionsInValidation = false;
        for (String mongoCollection : mongoCollections) {
            if(subDomain.equals(mongoCollection)) {
                collectionsInValidation = true;
            }
        }

        if(!collectionsInValidation) {
            mongoDatabase.createCollection(subDomain);
        }

        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(subDomain);
        MongoCursor<Document> mongoCursor = mongoCollection.find().iterator();

        int rows = -1;
        int commits = -1;

        for (String measurement : measurements) {
            boolean validation = false;

            while(mongoCursor.hasNext()) {
                BsonDocument bsonDocument = mongoCursor.next().toBsonDocument();
                BsonString bsonString = bsonDocument.get("table_name").asString();
                String tableName = bsonString.getValue();

                if(measurement.equals(tableName)) {
                    logger.info("measurement: " + measurement);
                    logger.info("tableName: " + tableName);
                    validation = true; 
                    break;
                }
            }

            if(!validation) {
                InsertOneResult insertOneResult = mongoCollection.insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("table_name", measurement)
                        .append("location", new JSONObject())
                        .append("description", "")
                        .append("source_agency", "")
                        .append("source", measurement)
                        .append("source_type", "")
                        .append("tag", new ArrayList<String>()));

                BsonValue bsonValue = insertOneResult.getInsertedId();
                logger.info("bsonValue: " + bsonValue);

                commits++;
            }

            rows++;
        }

        serviceResultMeta.put("rows", rows+1);
        serviceResultMeta.put("commits", commits+1);

        return new JSONObject(serviceResultMeta);
    }

}
