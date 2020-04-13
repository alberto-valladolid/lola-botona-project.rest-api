package com.lolabotona.restapi.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CalendarEvent {

	private boolean groupExist; 
	private boolean userAssits; 
	private boolean isFull; 
    private Timestamp timeOfDay; // 12:00  morning - 18:00 afternoon
    private String description; 
    
    
    public CalendarEvent(boolean groupExist,boolean userAssits, boolean isFull, Timestamp timeOfDay,String description) {
    	this.groupExist = groupExist; 
    	this.userAssits = userAssits; 
    	this.isFull = isFull; 
    	this.timeOfDay = timeOfDay; 
    	this.description = description; 
    }
    
    public CalendarEvent() {
    }


}
