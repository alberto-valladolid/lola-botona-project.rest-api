package com.lolabotona.restapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
	@Id
	
	private int user_id; 
	private String username; 
	private String password; 
	private String profile; 
	private String phone_number; 

}
