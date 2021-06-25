package com.keti.kafka.producer.weather.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.keti.kafka.producer.weather.entity.VillageInfoEntity;


@Repository
public interface VillageInfoRepository extends JpaRepository<VillageInfoEntity, String> {

    @Query(value = "SELECT * FROM Village_Info ORDER BY vi_nx, vi_ny", nativeQuery = true)
    public List<VillageInfoEntity> findByAll();

    @Query(value = "SELECT vi_nx, vi_ny, COUNT(vi_code) AS vi_nxy_cnt FROM Village_Info GROUP BY vi_nx, vi_ny ORDER BY vi_nx, vi_ny", nativeQuery = true)
    public List<int[]> findByViPointGrpCnt();

    @Query(value = "SELECT DISTINCT vi_nx, vi_ny FROM Village_Info WHERE vi_target_id=?1", nativeQuery = true)
    public List<int[]> findByViTarget(int targetId);

    @Query(value = "SELECT DISTINCT vi_nx, vi_ny FROM Village_Info", nativeQuery = true)
    public List<int[]> findByAllViTarget();

}