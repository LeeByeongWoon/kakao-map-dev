package com.keti.kafka.producer.weather.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keti.kafka.producer.weather.entity.VillageInfoEntity;
import com.keti.kafka.producer.weather.repository.VillageInfoRepository;


@Service
public class VillageInfoService {

    @Autowired
    private VillageInfoRepository villageInfoRepository;


    public List<VillageInfoEntity> getEnabledVillage() {
        List<VillageInfoEntity> enableVillageList = new ArrayList<>();
        enableVillageList = villageInfoRepository.findByViCollectActive(true);

        return enableVillageList;
    }
    
}
