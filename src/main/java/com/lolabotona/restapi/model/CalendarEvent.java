package com.lolabotona.restapi.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarEvent {
	
	private boolean userAssits; 
	private boolean isFull; 
    private Timestamp timeOfDay; // 12:00  morning - 18:00 afternoon
    
    
    public CalendarEvent(boolean userAssits, boolean isFull, Timestamp timeOfDay) {
    	this.userAssits = userAssits; 
    	this.isFull = isFull; 
    	this.timeOfDay = timeOfDay; 
    }
    
    public CalendarEvent() {
    }

}
