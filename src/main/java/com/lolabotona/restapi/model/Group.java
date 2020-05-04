package com.lolabotona.restapi.model;


import java.sql.Time;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.sql.Timestamp;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;


//import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(	name = "groups", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"dayofweek","showorder"})
})

public class Group {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
    
	@JsonManagedReference(value="group")
    @OneToMany(mappedBy = "group")
    private Set<UserGroup> userSet;
	

    private int capacity;
	
    @NotBlank    
    private String description; 
    
//    @NotBlank 
//    private int orderShown;
	
    @NotNull 
    private int showorder; 

    private int dayofweek;

    @Column(nullable = false)
    private boolean active;
    
    private Time startTime; 
    
	public Group() {
	}

	public Group(int capacity, String description, int showorder, int dayofweek,boolean active, Time startTime) {
		this.capacity = capacity;
		this.description = description;
		//this.orderShown = orderShown;
		this.showorder = showorder;
		this.dayofweek = dayofweek;
		this.active = active; 	
		this.startTime = startTime; 	

	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	

	public Set<UserGroup> getUserSet() {
        return userSet;
    }
	
	public void setUserSet(Set<UserGroup> userSet) {
        this.userSet = userSet;
    } 	
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
		
//	public int getOrderShown() {
//		return orderShown;
//	}
//
//	public void setOrderShown(int orderShown) {
//		this.orderShown = orderShown;
//	}
	
	public int getshoworder() {
		return showorder;
	}
	
	public void setshoworder(int showorder) {
		this.showorder = showorder;
	}
	
	public int getdayofweek() {
		return dayofweek;
	}

	public void setdayofweek(int dayofweek) {
		this.dayofweek = dayofweek;
	}
	
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}	
	
	@Override
	public String toString() {
		return "Group [id=" + id + ", capacity=" + capacity + ", description=" + description + /*", orderShown=" + orderShown +*/ " , showorder=" + showorder +" , dayofweek=" + dayofweek +" , active=" + active + "]";
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        Group that = (Group) o;
        return Objects.equals(id, that.id)  ;
    }
}
