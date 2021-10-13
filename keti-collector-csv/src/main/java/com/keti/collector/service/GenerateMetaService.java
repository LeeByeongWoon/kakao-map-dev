package com.keti.collector.service;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
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
        Map<String, Long> commits = objectMapper.readValue(_measurements.get("commits").toString(), new TypeReference<Map<String, Long>>(){});

        logger.info("commits: " + commits);

        Map<String, Object> resultMap = new HashMap<>();

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

        MongoCollection<Document> collection = mongoDatabase.getCollection(subDomain);

        logger.info("mongo: " + collection);
        // database.createCollection(subDomain);


        return new JSONObject(resultMap);
    }

}
