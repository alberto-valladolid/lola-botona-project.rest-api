package com.lolabotona.restapi.service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.response.MessageResponse;
//import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.repository.UserRepository;

@Service
public class UserGroupService  {
	
//	@Autowired 
//	private   GroupRepository groupRepository;
	
	@Autowired 
	private   UserRepository userRepository; 
	
	@Autowired 
	private   UserGroupRepository userGroupRepository; 

	public   int getPendingRecieveCount(User user) {	
	
	
		
		//Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.add(Calendar.MONTH, +2);
		Timestamp currentTimestamp = new Timestamp(currentCalendar.getTimeInMillis());	
		
		Calendar aMonthAgoCalendar = Calendar.getInstance();
		aMonthAgoCalendar.add(Calendar.MONTH, -1);
		Timestamp aMonthAgoTimestamp = new Timestamp(aMonthAgoCalendar.getTimeInMillis());	
	

		return userGroupRepository.countByTypeAndUserAndRetrievedAndDateatBetween("absence", user, false, aMonthAgoTimestamp , currentTimestamp); 
		
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
    
    
    
    public long decreasePendingRecieveCount(User user) {
    	
		//Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.add(Calendar.MONTH, +2);
		Timestamp currentTimestamp = new Timestamp(currentCalendar.getTimeInMillis());	
		
		
		Calendar aMonthAgoCalendar = Calendar.getInstance();
		aMonthAgoCalendar.add(Calendar.MONTH, -1);
		Timestamp aMonthAgoTimestamp = new Timestamp(aMonthAgoCalendar.getTimeInMillis());			
    	
		Optional<UserGroup> userGroup = userGroupRepository.findTop1ByTypeAndUserAndRetrievedAndDateatBetweenOrderByDateat("absence", user, false, aMonthAgoTimestamp, currentTimestamp);

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
