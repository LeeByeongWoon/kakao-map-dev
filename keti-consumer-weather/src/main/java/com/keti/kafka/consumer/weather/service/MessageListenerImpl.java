package com.keti.kafka.consumer.weather.service;

import java.util.Date;
import java.util.TimeZone;
import java.time.Instant;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.keti.consumer.kafka.service.InfluxServiceImpl;
import com.keti.consumer.kafka.vo.PartitionsMeasurementVo;

@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    // @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    InfluxServiceImpl influxService;

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            String message = consumerRecord.value();
        
            JsonNode rootNode = objectMapper.readTree(message);
            System.out.println(rootNode);
            JsonNode kafkaPartition = rootNode.path("kafka").path("partition");

            PartitionsMeasurementVo vo = new PartitionsMeasurementVo();
            vo.setTime(changeTimeStampMin(rootNode.path("@timestamp").toString()));
            vo.setId(Integer.parseInt(kafkaPartition.path("id").toString()));
            vo.setBrokerAddress(kafkaPartition.path("broker").path("address").toString());

            influxService.write(vo);
        } catch (Exception e) {
            //TODO: handle exception
        }   
    }

    Instant changeTimeStampMin(String timeStamp) {
		timeStamp = timeStamp.replaceAll("\"", "");
		timeStamp = timeStamp.substring(0, (timeStamp.length() - 7))+"00";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		Date date = null;
		try {
			date = sdf.parse(timeStamp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date.toInstant();
	}

}