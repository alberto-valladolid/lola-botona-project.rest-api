package com.lolabotona.restapi.repository;

import java.util.List;
//import java.util.Optional;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.Group;


public interface GroupRepository extends JpaRepository<Group, Long> {

	//List<Group>  findByDayofweekAndTimeofday( int dayofweek, String timeofday  );
	
	List<Group> findByDayofweekAndActiveOrderByShoworderAsc( int dayOfWeek, boolean active);
	
}
