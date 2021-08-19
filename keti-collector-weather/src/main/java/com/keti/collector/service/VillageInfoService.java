package com.keti.collector.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.keti.collector.entity.VillageInfoEntity;
import com.keti.collector.repository.VillageInfoRepository;


@Service
public class VillageInfoService {

    private final VillageInfoRepository villageInfoRepository;

    public VillageInfoService(VillageInfoRepository villageInfoRepository) {
        this.villageInfoRepository = villageInfoRepository;
    }


    public List<VillageInfoEntity> getAllVi() {
        List<VillageInfoEntity> viAll = villageInfoRepository.findByAll();

        return viAll;
    }


    public List<int[]> getViPointGrpCnts() {
        List<int[]> ViPointGrpCnts = new ArrayList<>();
        ViPointGrpCnts = villageInfoRepository.findByViPointGrpCnt();

        return ViPointGrpCnts;
    }


    public List<int[]> getPoints(String targetIds) {
        List<int[]> points = new ArrayList<>();

        int[] convertTargetIds = Arrays.stream(targetIds.split(","))
                                       .mapToInt(Integer::parseInt)
                                       .toArray();

        points = villageInfoRepository.findByViTarget(convertTargetIds);
        
        return points;
    }


    public Map<String, List<VillageInfoEntity>> getGroupPointMap() {
        Map<String, List<VillageInfoEntity>> groupPointMap = new HashMap<String, List<VillageInfoEntity>>();

        List<VillageInfoEntity> viAll = getAllVi();
		List<int[]> viPointGrpCnts = getViPointGrpCnts();

		int viAllSize = viAll.size();
		int viPointGrpCntsSize = viPointGrpCnts.size();

		if(viAllSize <= 0 && viPointGrpCntsSize <= 0) {
			viAll = null;
			viPointGrpCnts = null;
		} else {
			int start = 0;
			int end = 0;
			for(int i=0; i<viPointGrpCntsSize; i++) {
				String nx = Integer.toString(viPointGrpCnts.get(i)[0]);
				String ny = Integer.toString(viPointGrpCnts.get(i)[1]);
				int pointCnt = viPointGrpCnts.get(i)[2];

				String key = nx + "." + ny;
				
				end += pointCnt;

				List<VillageInfoEntity> pointList = new ArrayList<>();

				for(int j=start; j<end; j++) {
					pointList.add(viAll.get(j));
				}
				groupPointMap.put(key, pointList);

				start += pointCnt;
			}
		}

        return groupPointMap;
    }
    
}
