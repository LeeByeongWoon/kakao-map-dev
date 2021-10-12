package com.keti.collector.service;

import java.net.UnknownHostException;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.keti.collector.config.MongoDBConfiguration;
import com.keti.collector.vo.GenerateVo;


@Service
public class GenerateMetaService {

    private final MongoDBConfiguration mongoDBConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GenerateMetaService(MongoDBConfiguration _mongoDBConfiguration) {
        this.mongoDBConfiguration = _mongoDBConfiguration;
    }


    public void generateByDatabaseAndCollections(GenerateVo generateVo) throws MongoException, UnknownHostException {
        String mainDomain = generateVo.getMetaVo().getMainDomain();
        String subDomain = generateVo.getMetaVo().getSubDomain();

        MongoClient mongoClient = mongoDBConfiguration.getMongoConn();
        MongoDatabase database = mongoClient.getDatabase(mainDomain);
        database.createCollection(subDomain);

        MongoCollection<Document> collection = database.getCollection(subDomain);

    }
    

    public void generateByMeta(GenerateVo generateVo) throws MongoException, UnknownHostException {
        MongoClient mongoClient = mongoDBConfiguration.getMongoConn();
        
        MongoDatabase database = mongoClient.getDatabase("admin");

        Bson command = new BsonDocument("ping", new BsonInt64(1));
        Document commandResult = database.runCommand(command);

        logger.info("commandResult: " + commandResult);

    }

}
