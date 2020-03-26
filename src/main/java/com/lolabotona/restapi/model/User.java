package com.lolabotona.restapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*; 


@Entity
@Table(	name = "users", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = "username")
})

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
    @NotBlank
    @Size(min = 3, max = 20)
    private String username; //telefono
 
    @NotBlank
    @Size(max = 50)
    private String role;
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String name; //nombre
    
    @NotBlank 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
	
	
	
	public User() {
	}

	public User(String username, String role,  String name,String password) {
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

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRoles(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", role=" + role + ", name=" + name + " , password=" + password + "]";
	}

	
}
