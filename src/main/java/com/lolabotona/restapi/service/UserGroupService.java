package com.lolabotona.restapi.service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lolabotona.restapi.model.AppConfig;
import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.repository.AppConfigRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;


@Service
public class UserGroupService  {
	

	
	@Autowired 
	private   AppConfigRepository appConfigRepository; 
	

	
	@Autowired 
	private   UserGroupRepository userGroupRepository; 

	public   int getPendingRecieveCount(User user, AppConfig appConfig) {	
		
		
		//Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar toCalendar = Calendar.getInstance();
		toCalendar.add(Calendar.YEAR, 1);
		Timestamp toTimestamp = new Timestamp(toCalendar.getTimeInMillis());			
		
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.add(Calendar.DAY_OF_YEAR, -appConfig.getAbsenceDays());
		Timestamp fromTimestamp = new Timestamp(fromCalendar.getTimeInMillis());	
		

		return userGroupRepository.countByTypeAndUserAndRetrievedAndDateatBetween("absence", user, false, fromTimestamp , toTimestamp); 
		
	}
	
	
    public boolean userAssists(  Group group,  Timestamp dateAt , User user,Optional<UserGroup> recurrentUserGroup, Optional<UserGroup> abcenseUserGroup, Optional<UserGroup> retrieveUserGroup) { 

		
		if (recurrentUserGroup.isPresent() && !abcenseUserGroup.isPresent() || retrieveUserGroup.isPresent() ) {
 			return true; 
		}else {
			return false; 
		}
	
    }
    
    
    public boolean eventIsFull(  Group group,  Timestamp dateAt, List<UserGroup> recurrentsGroup, List<UserGroup> abcensesGroup, List<UserGroup> retrievesGroup ) {   
	

		if ((recurrentsGroup.size() -    abcensesGroup.size() +  retrievesGroup.size() ) < group.getCapacity() ) {
 			return false; 
		}else {
			return true; 
		}
	
    }
    
    
    public String getUserAndCapacity(  Group group,  Timestamp dateAt, List<UserGroup> recurrentsGroup, List<UserGroup> abcensesGroup, List<UserGroup> retrievesGroup ) {  	

		return recurrentsGroup.size() -    abcensesGroup.size() +  retrievesGroup.size() +"/" + group.getCapacity(); 
			
    }
    
    
    
    public long decreasePendingRecieveCount(User user, AppConfig appConfig) {
    	

		
		
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.add(Calendar.DAY_OF_YEAR, -appConfig.getAbsenceDays());
		fromCalendar.add(Calendar.MONTH, -1);
		Timestamp fromTimestamp = new Timestamp(fromCalendar.getTimeInMillis());		
		
		
		//Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar toCalendar = Calendar.getInstance();
		toCalendar.add(Calendar.YEAR, +1);
		Timestamp toTimestamp = new Timestamp(toCalendar.getTimeInMillis());	
		
    	
		Optional<UserGroup> userGroup = userGroupRepository.findTop1ByTypeAndUserAndRetrievedAndDateatBetweenOrderByDateat("absence", user, false, fromTimestamp, toTimestamp);
		
	
		
		if (userGroup.isPresent()) {
			
			System.out.println(userGroup.get().getId());
			UserGroup currUserGroup  = userGroup.get(); 
			currUserGroup.setRetrieved(true);
			userGroupRepository.save(currUserGroup); 			
			
		} 
		
		return userGroup.get().getId(); 
    	
    }
    
    
    public boolean containsDate(final List<FeastDay> list, final Date date){
        return list.stream().map(FeastDay::getDate).filter(date::equals).findFirst().isPresent();
    }



    

	
	
}
