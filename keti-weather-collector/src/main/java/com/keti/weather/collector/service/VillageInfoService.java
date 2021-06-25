package com.keti.weather.collector.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keti.weather.collector.entity.VillageInfoEntity;
import com.keti.weather.collector.repository.VillageInfoRepository;


@Service
public class VillageInfoService {

    @Autowired
    private VillageInfoRepository villageInfoRepository;


    public List<VillageInfoEntity> getAllVi() {
        List<VillageInfoEntity> viAll = villageInfoRepository.findByAll();

        return viAll;
    }


    public List<int[]> getViPointGrpCnts() {
        List<int[]> ViPointGrpCnts = new ArrayList<>();
        ViPointGrpCnts = villageInfoRepository.findByViPointGrpCnt();

        return ViPointGrpCnts;
    }


    public List<int[]> getViTarget(String targetIds) {
        List<int[]> points = new ArrayList<>();

        int[] convertTargetIds = Arrays
                                    .stream(targetIds.split(","))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();

        points = villageInfoRepository.findByViTarget(convertTargetIds);
        
        return points;
    }
    
}
