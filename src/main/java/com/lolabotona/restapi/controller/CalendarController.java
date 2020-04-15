package com.lolabotona.restapi.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.CalendarDay;
import com.lolabotona.restapi.model.CalendarEvent;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CalendarController {
	
	
	  
    @Autowired 
	private UserGroupRepository userGroupRepository; 
    
    @Autowired 
	private UserRepository userRepository; 
    
    @Autowired 
	private GroupRepository groupRepository; 
    

	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN'))")
	@GetMapping("/calendardata")
    public ResponseEntity<List<CalendarDay>> getCalendarData() {
	  try {
		  
		  int postMonthExtraDays, preMonthExtraDays;		  
	      List<CalendarDay> calendarDays = new ArrayList<CalendarDay>();      
	      Calendar c = Calendar.getInstance();	      
	      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      String startString,endString; 
	      LocalDate start,end; 
	      
	     
	      //iterate previus month extra days	      
	      preMonthExtraDays = CalendarDay.calcPreMonthExtraDays(c);
	      
    	  if(preMonthExtraDays!=0) {
    		  String previusMonthString; 

     		  if( c.get(Calendar.MONTH) == 0) {
    
        		   
        			  startString = c.get(Calendar.YEAR)-1 + "-12-"+ (YearMonth.of(c.get(Calendar.YEAR)-1, 11).lengthOfMonth()+1-preMonthExtraDays);
                	  endString =   c.get(Calendar.YEAR)-1 + "-12-"+  YearMonth.of(c.get(Calendar.YEAR)-1, 11).lengthOfMonth();
                	  
        		   
    		  }else {
    			   previusMonthString =   String.valueOf(c.get(Calendar.MONTH));
        		   
         		  if (c.get(Calendar.MONTH) <10 && c.get(Calendar.MONTH) != 0 ) {
        			  previusMonthString = "0"+previusMonthString; 
        		  }
        		   
    			  startString = String.valueOf(c.get(Calendar.YEAR)) + "-" + previusMonthString +"-"+ (YearMonth.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)).lengthOfMonth()+1-preMonthExtraDays);
            	  endString =   String.valueOf(c.get(Calendar.YEAR)) + "-" + previusMonthString +"-"+  YearMonth.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)).lengthOfMonth();
            	  
    		  }
     		  
    		  System.out.println(startString + " " + endString); 
    		  
        	  start = LocalDate.parse(startString);
        	  end = LocalDate.parse(endString);   
        	  
        	  while (!start.isAfter(end)) {
        		  
        		  System.out.println(start.getDayOfMonth());
	        	      
	      	        try {
	      	    	    
	      	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), false, new CalendarEvent(),  new CalendarEvent(), start.getDayOfWeek().getValue())); 
	      	    	    
	      	    	} catch(Exception e) { //this generic but you can control another types of exception
	      	    	    // look the origin of excption 
	      	    	}
        		  
        	      start = start.plusDays(1);
        	  }       	  

    	  }
    	  
  
		  

   	  
//    	    System.out.println(userGroupRepository.asdf(group.get(), user.get(), "recurrent"));


    	     
    	  //iterate current month days
    	  
  		
    	  int currMonthDays;     	  
		  YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);	  
		  currMonthDays = yearMonthObject.lengthOfMonth();		  
  		  String currMonthString =   String.valueOf(c.get(Calendar.MONTH)+1);
  		  
  		  if (c.get(Calendar.MONTH) <10) {
  			currMonthString = "0"+currMonthString; 
  		  }
  		  
  		  startString = c.get(Calendar.YEAR) + "-" + currMonthString +"-"+ "01";
      	  endString =   c.get(Calendar.YEAR) + "-" + currMonthString +"-"+  currMonthDays;
      	  
      	  System.out.println(startString + " " + endString);
      	  
      	  start = LocalDate.parse(startString);
      	  end = LocalDate.parse(endString); 
      	  
      	  while (!start.isAfter(end)) {
      		  
      		  System.out.println(start.getDayOfMonth());
	        	      
	      	        try {
	      	    	    
	      	        	int dayOfWeek =  (start.getDayOfWeek().getValue() % 7) +1 ; 
	      	        	
	      	        	CalendarEvent morningEvent = new CalendarEvent();
	      	        	CalendarEvent afternoonEvent = new CalendarEvent();;
	      	        	
	      	        	//find  morning groups 
		      	  		Optional<Group> morningGroup = groupRepository.findByTimeofdayAndDayofweekAndActive("Mañana", dayOfWeek, true);
		      	  		
		      	  	    morningEvent.setGroupExist(morningGroup.isPresent());
		      			if (morningEvent.isGroupExist()) {
				      				
		      	    	    Date parsedDate1 = dateFormat.parse(c.get(Calendar.YEAR) + "-" + currMonthString +  "-" +start.getDayOfMonth()  +" 12:00");
		      	    	    Timestamp morningTimestamp = new java.sql.Timestamp(parsedDate1.getTime()); 
		      	    	    morningEvent.setTimeOfDay(morningTimestamp);
		      	    	    morningEvent.setGroupId(morningGroup.get().getId());
		      	    	    
		      	    	    morningEvent.setDescription(morningGroup.get().getDescription());
		      	    	    morningEvent.setUserAssits(this.userAssists(morningGroup.get(), morningTimestamp ));
		      	    	    if(!morningEvent.isUserAssits()) {
		      	    	    	
		      	    	    	morningEvent.setFull(this.eventIsFull(morningGroup.get(), morningTimestamp));
		      	    	    	
		      	    	    	
		      	    	    }
		      				
		      			} 
		      			
		      			
		      			
		      		    //find  afternoon groups 
		      	  		Optional<Group> afternoonGroup = groupRepository.findByTimeofdayAndDayofweekAndActive("Tarde", dayOfWeek, true);
		      	  		
		      	  	    afternoonEvent.setGroupExist(afternoonGroup.isPresent());
		      			if (afternoonEvent.isGroupExist()) {
				      				
		      				Date parsedDate2 = dateFormat.parse(c.get(Calendar.YEAR) + "-" + currMonthString +  "-" +start.getDayOfMonth()  +" 18:00");
		      	    	    Timestamp afternoonTimestamp = new java.sql.Timestamp(parsedDate2.getTime()); 
		      	    	    afternoonEvent.setTimeOfDay(afternoonTimestamp);
		      	    	    afternoonEvent.setGroupId(afternoonGroup.get().getId());
		      	    	    afternoonEvent.setDescription(afternoonGroup.get().getDescription());
		      	    	    afternoonEvent.setUserAssits(this.userAssists(afternoonGroup.get(), afternoonTimestamp ));
		      	    	    if(!afternoonEvent.isUserAssits()) {
		      	    	    	
		      	    	    	afternoonEvent.setFull(this.eventIsFull(afternoonGroup.get(), afternoonTimestamp));
		      	    	    	
		      	    	    }
		      				
		      			} 
		      
	      	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), true, morningEvent, afternoonEvent, start.getDayOfWeek().getValue())); 
	      	    	    
	      	    	} catch(Exception e) { //this generic but you can control another types of exception
	      	    	    // look the origin of excption 
	      	    	}
      		  
      	      start = start.plusDays(1);
      	  }       	  

      	  
      	  
      	  
	  	  //iterate next month extra days
      	 postMonthExtraDays = CalendarDay.calcPostMonthExtraDays(c);
	      
    	  if(postMonthExtraDays!=0) {
    		  
    		  if(c.get(Calendar.MONTH) == 11) {   	
    			  startString = c.get(Calendar.YEAR)+1 + "-01-"+ "01";
            	  endString =   c.get(Calendar.YEAR)+1 + "-01-0"+ postMonthExtraDays  ;
    			  
    		  }else {
    			  String nextMonthString =   String.valueOf(c.get(Calendar.MONTH)+2);
    			  if (c.get(Calendar.MONTH) <10) {
        			  nextMonthString = "0"+nextMonthString; 
        		  }
    			  
    			  
    			  startString = c.get(Calendar.YEAR)+1 + "-"+nextMonthString+"-"+ "01";
            	  endString =   c.get(Calendar.YEAR)+1 + "-"+nextMonthString+"-0"+ postMonthExtraDays  ;
    			  
    		  }
    		  
        	  System.out.println(startString + " " + endString);    
        	  
        	  start = LocalDate.parse(startString);
        	  end = LocalDate.parse(endString);   
        	  
        	  while (!start.isAfter(end)) {
        		  
        		  System.out.println(start.getDayOfMonth());
	        	      
	      	        try {
	      	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), false, new CalendarEvent(),  new CalendarEvent(), start.getDayOfWeek().getValue())); 
	      	    	    
	      	    	} catch(Exception e) { //this generic but you can control another types of exception
	      	    	    // look the origin of excption 
	      	    	}
        		  
        	      start = start.plusDays(1);
        	  }       	  

    	  }
    	  
	      return new ResponseEntity<>(calendarDays, HttpStatus.OK);	
	      
       } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
     }
	
	
    public boolean userAssists(  Group group,  Timestamp dateAt ) {    	
    	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
		Optional<User> user = userRepository.findById( userDetailsImpl.getId());

		Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user.get(), group, "recurrent");
		Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group, "absencse", dateAt);
		Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group, "retrieve", dateAt);
		
		if (recurrentUserGroup.isPresent() && !abcenseUserGroup.isPresent() || retrieveUserGroup.isPresent() ) {
 			return true; 
		}else {
			return false; 
		}
	
    }
    
    
    public boolean eventIsFull(  Group group,  Timestamp dateAt ) {    	
    	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
		Optional<User> user = userRepository.findById( userDetailsImpl.getId());
		
		List<UserGroup> recurrentUserGroup = userGroupRepository.findByGroupAndType( group, "recurrent");
		List<UserGroup> abcenseUserGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "absencse", dateAt);
		List<UserGroup> retrieveUserGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "retrieve", dateAt);
		

		if ((recurrentUserGroup.size() -    abcenseUserGroup.size() +  retrieveUserGroup.size() ) < group.getCapacity() ) {
 			return false; 
		}else {
			return true; 
		}
	
    }
    
	
	
	
	
}
