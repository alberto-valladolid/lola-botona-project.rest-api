package com.lolabotona.restapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*; 

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(	name = "users", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = "username"),
	@UniqueConstraint(columnNames = "email") 
})

public class User {
	@Id
	
	private int user_id; 
	private String username; //telefono
	private String password; 
	private String role; 
	private String name;  //nombre
	
	
	public User() {
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.role = role;
		this.password = password;
		this.name = name; 
	}

}
