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


    public List<VillageInfoEntity> getViAll() {
        List<VillageInfoEntity> viAll = villageInfoRepository.findByAll();

        return viAll;
    }


    public List<int[]> getViPointGrpCnt() {
        List<int[]> ViPointGrpCnt = new ArrayList<>();
        ViPointGrpCnt = villageInfoRepository.findByViPointGrpCnt();

        return ViPointGrpCnt;
        
    }


    public List<int[]> getEnablePoint(Boolean active) {
        List<int[]> enablePointList = new ArrayList<>();
        enablePointList = villageInfoRepository.findByViActivePoint(active);

        return enablePointList;
        
    }
    
}
