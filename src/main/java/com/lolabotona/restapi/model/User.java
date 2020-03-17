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
	
	private Long id; 
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

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setEmail(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return role;
	}

	public void setRoles(String role) {
		this.role = role;
	}
	
}
