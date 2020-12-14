package com.lolabotona.restapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*; 


@Entity
@Getter
@Setter
@Table(	name = "users", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = "username")
})

public class User {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@JsonManagedReference(value="user")
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserGroup> groupSet;
	
	
	@JsonManagedReference(value="user2")
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeacher> userTeacherSet; //profesores asignados a este usuario
	
	
	@JsonManagedReference(value="user3")
    @OneToMany(mappedBy = "teacher",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeacher> userSet; //usuarios que tiene como profesor este usuario
	
	@JsonManagedReference(value="user4")
    @OneToMany(mappedBy = "teacher",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Group> studentsGroupSet; //grupos que tiene asignados este usuario siendo profesor 
	
	
//	FUNCIONA
//    @OneToMany(mappedBy = "teacher",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private Set<Group> groupsWitchIsTeacher; 
	
	
	
	
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
	
    @NotBlank
    @Size(min = 1, max = 100)
    private String type ; //alumn or teacher
	
	public User() {
	}

	public User(String username, String role, String password, String name, String type) {
		this.username = username;
		this.role = role;
		this.password = password;
		this.name = name; 
		this.type = type; 
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Set<UserGroup> getGroupSet() {
        return groupSet;
    }
	
	public void setGroupSet(Set<UserGroup> groupSet) {
        this.groupSet = groupSet;
    } 
	
	public void setUserTeacherSet(Set<UserTeacher> userTeacherSet) {
        this.userTeacherSet = userTeacherSet;
    } 
		
	public Set<UserTeacher> getUserTeacherSet() {
	   return userTeacherSet;
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
		return "User [id=" + id + ", username=" + username + ", role=" + role + ", name=" + name + " , password=" + password + " , type=" + type +  "]";
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        User that = (User) o;
        return Objects.equals(username, that.username)  ;
    }
}
