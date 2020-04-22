package com.lolabotona.restapi.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CalendarEvent {

	private boolean groupExist; 
	private Long groupId; 
	private boolean userAssits; 
	private boolean isFull; 
    private Timestamp timeOfDay; // 12:00  morning - 18:00 afternoon
    private String description; 
    private String users; 
    
    
    
    public CalendarEvent(boolean groupExist,boolean userAssits, boolean isFull, Timestamp timeOfDay,String description, Long groupId,String users) {
    	this.groupExist = groupExist; 
    	this.groupId = groupId; 
    	this.userAssits = userAssits; 
    	this.isFull = isFull; 
    	this.timeOfDay = timeOfDay; 
    	this.description = description;
    	this.users = users;
		
    }
    
    public CalendarEvent() {
    }


}
