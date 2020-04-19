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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.CalendarDay;
import com.lolabotona.restapi.model.CalendarEvent;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.request.ChgPwRequest;
import com.lolabotona.restapi.payload.request.NewCalendarRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;
import com.lolabotona.restapi.service.UserGroupService;

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
    	
	@Autowired 
	private UserGroupService userGroupService; 
	

	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN'))")
	@GetMapping("calendar/calendardata")
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
//	      	    	    
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
		      	    	    morningEvent.setUserAssits(userGroupService.userAssists(morningGroup.get(), morningTimestamp ));
		      	    	    if(!morningEvent.isUserAssits()) {
		      	    	    	
		      	    	    	morningEvent.setFull(userGroupService.eventIsFull(morningGroup.get(), morningTimestamp));
		      	    	    	
		      	    	    	
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
		      	    	    afternoonEvent.setUserAssits(userGroupService.userAssists(afternoonGroup.get(), afternoonTimestamp ));
		      	    	    if(!afternoonEvent.isUserAssits()) {
		      	    	    	
		      	    	    	afternoonEvent.setFull(userGroupService.eventIsFull(afternoonGroup.get(), afternoonTimestamp));
		      	    	    	
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
	
	
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PostMapping("calendar/createRetrieve")
	public ResponseEntity<?> createRetrieve(@Valid @RequestBody NewCalendarRequest rewRetrieveRequest, Authentication authentication) {
		
		Date today = new Date();
		
		if(rewRetrieveRequest.getDate().after(today)) {
			if(userGroupService.getPendingRecieveCount() > 0) {
				
				Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());				
				
				if (group.isPresent()) {
					
					if (userGroupService.userAssists(group.get(), rewRetrieveRequest.getDate())) {
						return ResponseEntity
								.badRequest()
								.body(new MessageResponse("Error: El usuario ya está asiste a ese evento "));
					}else {
						
						if (userGroupService.eventIsFull(group.get(), rewRetrieveRequest.getDate())) {
							return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: El grupo está completo "));
						}else {
							
							//Group group = new Group (newGroupRequest.getCapacity(), newGroupRequest.getDescription(), newGroupRequest.getTimeofday(), newGroupRequest.getDayofweek(), newGroupRequest.isActived());
							
							UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
							Optional<User> user = userRepository.findById( userDetailsImpl.getId());
							
							UserGroup newUserGroup = new UserGroup(user.get(), group.get(), "retrieve", true, rewRetrieveRequest.getDate()); 
					
							userGroupRepository.save(newUserGroup);
			
							userGroupService.decreasePendingRecieveCount(user.get());							
							
							return ResponseEntity.ok(new MessageResponse("Grupo creado con éxito!"));
							
						}
						
					}
					
				} else {
					return ResponseEntity
							.badRequest()
							.body(new MessageResponse("Error: Grupo no encontrado"));
				}
								
			}else {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("No dispone de clases pendientes"));
			}
			
			
		}else {
			  return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: No se pueden crear asistencias en fechas pasadas"));
		}
		
		 
		
	}
	
	
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PostMapping("calendar/createAbsence")
	public ResponseEntity<?> createAbsence(@Valid @RequestBody NewCalendarRequest rewRetrieveRequest, Authentication authentication) {
		
		Date today = new Date();
		
		if(rewRetrieveRequest.getDate().after(today)) {
				
			Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());				
			
			if (group.isPresent()) {
				
				if (userGroupService.userAssists(group.get(), rewRetrieveRequest.getDate())) {
					
					//REVISAR SI EL USUARIO ASISTE AL EVENTO POR UN RETRIEVE O POR UN RECURRENT
					
					UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
					Optional<User> user = userRepository.findById( userDetailsImpl.getId());
					
					UserGroup newUserGroup = new UserGroup(user.get(), group.get(), "absence", false, rewRetrieveRequest.getDate()); 
			
					userGroupRepository.save(newUserGroup);
						
					return ResponseEntity.ok(new MessageResponse("Grupo creado con éxito!"));
						
				
				}else {
					return ResponseEntity
							.badRequest()
							.body(new MessageResponse("Error: No se puede crear la ausencia, el usuario no asiste a clase ese día "  ));
				}
				
			} else {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Grupo no encontrado"));
			}
							
	
			
			
		}else {
			  return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: No se pueden crear asistencias en fechas pasadas"));
		}

		
		 
		
	}
	
		
	
}
