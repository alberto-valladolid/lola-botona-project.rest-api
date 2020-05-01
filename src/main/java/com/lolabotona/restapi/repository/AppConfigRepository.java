package com.lolabotona.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.AppConfig;


public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
	


}
