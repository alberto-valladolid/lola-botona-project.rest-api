package com.lolabotona.restapi.payload.response;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CalendarEventResponse {

	private Long groupId; 
	private boolean userAssits; 
	private boolean isFull; 
    private Timestamp timeOfDay; //timestamp para diferenciar las clases en un mismo día. 
    private String description; 
    private String users; 
    private Timestamp startAt;  //para desactivar el grupo cuando esta fecha sea próxima. 
    
    
    
    public CalendarEventResponse(boolean userAssits, boolean isFull, Timestamp timeOfDay,String description, Long groupId,String users, Timestamp startAt) {
   
    	this.groupId = groupId; 
    	this.userAssits = userAssits; 
    	this.isFull = isFull; 
    	this.timeOfDay = timeOfDay; 
    	this.description = description;
    	this.users = users;
    	this.startAt = startAt;
		
    }
    
    public CalendarEventResponse() {
    }
    
	@Override
	public String toString() {
		return "CalendarEvent [groupId=" + groupId + ", userAssits=" + userAssits + ", isFull=" + isFull + " , timeOfDay=" + timeOfDay +" , description=" + description +" , users=" + users + "]";
	}


}
