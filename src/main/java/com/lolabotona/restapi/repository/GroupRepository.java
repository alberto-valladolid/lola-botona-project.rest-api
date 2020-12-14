package com.lolabotona.restapi.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;


public interface GroupRepository extends JpaRepository<Group, Long> {

	List<Group> findByDayofweekAndShoworder( int dayofweek, int showorder  );
	
	List<Group> findByDayofweekAndActiveOrderByShoworderAsc( int dayOfWeek, boolean active);
	
	List<Group> findByActiveOrderByShoworderAsc( boolean active);
	
	List<Group> findByActiveAndTeacherInOrderByShoworderAsc( boolean active, List<User> teachers);

}
