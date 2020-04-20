package com.lolabotona.restapi.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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

	public   int getPendingRecieveCount() {	
	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findById( userDetailsImpl.getId());
		
		//Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.add(Calendar.MONTH, +2);
		Timestamp currentTimestamp = new Timestamp(currentCalendar.getTimeInMillis());	
		
		Calendar aMonthAgoCalendar = Calendar.getInstance();
		aMonthAgoCalendar.add(Calendar.MONTH, -1);
		Timestamp aMonthAgoTimestamp = new Timestamp(aMonthAgoCalendar.getTimeInMillis());	
	

		return userGroupRepository.countByTypeAndUserAndRetrievedAndDateatBetween("absence", user.get(), false, aMonthAgoTimestamp , currentTimestamp); 
		
	}
	
	
    public boolean userAssists(  Group group,  Timestamp dateAt ) {    	
    	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
		Optional<User> user = userRepository.findById( userDetailsImpl.getId());

		Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user.get(), group, "recurrent");
		Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group, "absence", dateAt);
		Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group, "retrieve", dateAt);
		
		if (recurrentUserGroup.isPresent() && !abcenseUserGroup.isPresent() || retrieveUserGroup.isPresent() ) {
 			return true; 
		}else {
			return false; 
		}
	
    }
    
    
    public boolean eventIsFull(  Group group,  Timestamp dateAt ) {   
	
		
		List<UserGroup> recurrentUserGroup = userGroupRepository.findByGroupAndType( group, "recurrent");
		List<UserGroup> abcenseUserGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "absence", dateAt);
		List<UserGroup> retrieveUserGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "retrieve", dateAt);
	

		if ((recurrentUserGroup.size() -    abcenseUserGroup.size() +  retrieveUserGroup.size() ) < group.getCapacity() ) {
 			return false; 
		}else {
			return true; 
		}
	
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
    
    

	
	
}
