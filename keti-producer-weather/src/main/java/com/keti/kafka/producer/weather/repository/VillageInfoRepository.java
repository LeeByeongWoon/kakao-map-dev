package com.keti.kafka.producer.weather.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.keti.kafka.producer.weather.entity.VillageInfoEntity;


@Repository
public interface VillageInfoRepository extends JpaRepository<VillageInfoEntity, String> {

    @Query(value = "SELECT * FROM Village_Info WHERE vi_collect_active=?1", nativeQuery = true)
    public List<VillageInfoEntity> findByViCollectActive(Boolean viCollectActive);

}