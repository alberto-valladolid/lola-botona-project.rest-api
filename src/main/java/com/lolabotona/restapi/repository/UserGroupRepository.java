package com.lolabotona.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {	

//  ejemplo funcionando con nativeQuery	
//	@Query(value = "SELECT * FROM user_group  WHERE groupid = ?1 and userid = ?2 and type = ?3", nativeQuery = true)
//	 UserGroup asdf(  String group, String user ,String type );
	
//ejemplo funcionando con JPQL
	@Query("SELECT u FROM UserGroup u WHERE u.group = ?1 and u.user = ?2 and u.type = ?3")
	List<UserGroup> asdf(  Group group, User user ,String type );

}