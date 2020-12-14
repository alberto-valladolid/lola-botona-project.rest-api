package com.lolabotona.restapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);	
	Boolean existsByUsername(String username);	
	Boolean existsByName(String name);	
	List<User>  findByType(  String type  );	
	Optional<User>  findByIdAndType(long id, String type);
	
	

}
