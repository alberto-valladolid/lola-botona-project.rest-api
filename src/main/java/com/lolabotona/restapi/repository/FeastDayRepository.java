package com.lolabotona.restapi.repository;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
//import java.util.Optional;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.FeastDay;


public interface FeastDayRepository extends JpaRepository<FeastDay, Long> {

//	List<FeastDay>  findByDayofweekAndTimeofday( int dayofweek, String timeofday  );
//	Optional<FeastDay> findByTimeofdayAndDayofweekAndActive(String timeOfDay, int dayOfWeek, boolean active);

	Boolean existsByDate(Date date);
	
}
