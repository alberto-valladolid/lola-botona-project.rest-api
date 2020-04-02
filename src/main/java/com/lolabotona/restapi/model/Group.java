package com.lolabotona.restapi.model;


import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;


//import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(	name = "groups", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"day_of_week","time_of_day"})
})

public class Group {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
    
	@JsonManagedReference(value="group")
    @OneToMany(mappedBy = "group")
    private Set<UserGroup> userSet;
	
    @NotBlank 
    private int capacity;
	
    @NotBlank    
    private String description; 
    
//    @NotBlank 
//    private int orderShown;
	
    @NotBlank    
    private String time_of_day; //morning or afternoon. Es posible que no sea necesario 

    @NotBlank 
    private int day_of_week;
    
    @NotBlank 
    private boolean active;
    
	
	public Group() {
	}

	public Group(int capacity, String description,/* int orderShown,*/ String time_of_day, int day_of_week,boolean active) {
		this.capacity = capacity;
		this.description = description;
		//this.orderShown = orderShown;
		this.time_of_day = time_of_day;
		this.day_of_week = day_of_week;
		this.active = active; 	

	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public String getTimeOfDay() {
		return time_of_day;
	}
	
	public void setTimeOfDay(String time_of_day) {
		this.time_of_day = time_of_day;
	}
	
	public int getDayOfWeek() {
		return day_of_week;
	}

	public void setDayOfWeek(int day_of_week) {
		this.day_of_week = day_of_week;
	}
	
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}	
	

	@Override
	public String toString() {
		return "Group [id=" + id + ", capacity=" + capacity + ", description=" + description + /*", orderShown=" + orderShown +*/ " , time_of_day=" + time_of_day +" , day_of_week=" + day_of_week +" , active=" + active + "]";
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        Group that = (Group) o;
        return Objects.equals(id, that.id)  ;
    }
}
