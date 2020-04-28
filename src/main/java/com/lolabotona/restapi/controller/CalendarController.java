package com.lolabotona.restapi.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.request.ChgPwRequest;
import com.lolabotona.restapi.payload.request.NewCalendarRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.repository.FeastDayRepository;
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
	private FeastDayRepository feastDayRepository; 
    
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
	      	        	
	      	    	    
	      	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), false, start.getDayOfWeek().getValue(),false, null) ); 
	      	    	    
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
  		  Date dayDate; 
  		  boolean isFeastDay;  	
  		  
		  UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
  		  User user = userRepository.findById( userDetailsImpl.getId()).get();

  		  
  		  if (c.get(Calendar.MONTH) <10) {
  			currMonthString = "0"+currMonthString; 
  		  }
  		  
  		  startString = c.get(Calendar.YEAR) + "-" + currMonthString +"-"+ "01";
      	  endString =   c.get(Calendar.YEAR) + "-" + currMonthString +"-"+  currMonthDays;
      	  
      	  
      	  System.out.println(startString + " " + endString);
      	  
      	  
      	  start = LocalDate.parse(startString);
      	  end = LocalDate.parse(endString); 
      	  
      	  while (!start.isAfter(end)) {      		
    
	  	        try {
	  	        	
	  	        	
	  	    	    dayDate	= new GregorianCalendar(c.get(Calendar.YEAR), Calendar.MONTH+1, start.getDayOfMonth()).getTime();
	  	    	    
	  	    	    System.out.println(start.getDayOfMonth());
	  	    	    
	      		    List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	      		    feastDayRepository.findAll().forEach(feastDays::add);	
	  	    	    
	  	    	    if(userGroupService.containsDate(feastDays, dayDate)) {	      	    	 
	  	    	    	isFeastDay = true; 
	  	    	    }else {
	  	    	    	isFeastDay = false; 
	  	    	    }
	  	    	    
	  	    	    
	  	        	int dayOfWeek =  (start.getDayOfWeek().getValue() % 7) +1 ; 
	  	        	
	  
	  	        	
	  	        	
	  	        	//find  current day Group
	      	  		List<Group> groups = groupRepository.findByDayofweekAndActiveOrderByShoworderAsc( dayOfWeek, true);
	      	  			
//	      	  		Set<CalendarEvent> events = new HashSet<CalendarEvent>();   es importante el orden y por eso se modifica a arraylist
	      	  	    ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
      	  	    	
      	  	    	
	      	  	     
	      			//if (!groups.isEmpty()) {	      	  		
	
      				for (Group group : groups) { 
      					System.out.println(group);
      					CalendarEvent calendarEvent = new CalendarEvent();      					
      					String hourEvent; 
      					
      					if(group.getshoworder() < 10) {
      						hourEvent = "12:0"+group.getshoworder();
      					}else {
      						hourEvent = "12:"+group.getshoworder();
      					}
        				
          	    	    Date parsedDate1 = dateFormat.parse(c.get(Calendar.YEAR) + "-" + currMonthString +  "-" +start.getDayOfMonth() + " "  + hourEvent);
          	    	    
          	    	    Timestamp timestampEvent = new java.sql.Timestamp(parsedDate1.getTime());  
          	    	    
          	    	    
	          	  		Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user, group, "recurrent");
	          			Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group, "absence", timestampEvent);
	          			Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group, "retrieve", timestampEvent);
	          	    	    
	          			
	          			List<UserGroup> recurrentsGroup = userGroupRepository.findByGroupAndType( group, "recurrent");
	          			List<UserGroup> abcensesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "absence", timestampEvent);
	          			List<UserGroup> retrievesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "retrieve", timestampEvent);
	          			
          	    	    
          	    	    calendarEvent.setTimeOfDay(timestampEvent);
//          	    	   
          	    	    
	          	    	calendarEvent.setGroupId(group.getId());		      	    	    
	          	    	calendarEvent.setDescription(group.getDescription());
	          	    	
	          	    	
	       
	          	    	
	          	    	 //System.out.println("adsf");
	          	    	
	          	    	 events.add(calendarEvent);
	          	    	
	          	    	 
          	           
	          	    	
	          	    	calendarEvent.setUserAssits(userGroupService.userAssists(group, timestampEvent ,user, recurrentUserGroup, abcenseUserGroup, retrieveUserGroup));		      	    	    
	          	    	calendarEvent.setUsers(userGroupService.getUserAndCapacity(group, timestampEvent, recurrentsGroup, abcensesGroup, retrievesGroup));
	          	    	    
          	    	    if(!calendarEvent.isUserAssits()) {
          	    	    	
          	    	    	calendarEvent.setFull(userGroupService.eventIsFull(group, timestampEvent, recurrentsGroup, abcensesGroup, retrievesGroup));
          	    	    	
          	    	    	
          	    	    }
      					
      				
      				}
	      				
  				   
      				
		  
	      				
	      			//} 
	      			
	      			
	
	      
	  	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), true,  start.getDayOfWeek().getValue(),isFeastDay,events)); 
	  	    	    
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
	      	    	    calendarDays.add(new CalendarDay(start.getDayOfMonth(), false, start.getDayOfWeek().getValue(),false, null)); 	      	    	  
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
	
	    List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	    feastDayRepository.findAll().forEach(feastDays::add);	
	    
	    LocalDate date = Instant.ofEpochMilli(rewRetrieveRequest.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();  	    
	    Date dayDate	= new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime();
	    	    
		if(!userGroupService.containsDate(feastDays,dayDate)) {
			
			Date today = new Date();
			
			if(rewRetrieveRequest.getDate().after(today)) {
				
				UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = userRepository.findById( userDetailsImpl.getId()).get();
				
				if(userGroupService.getPendingRecieveCount(user) > 0) {
										
					
					
					Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());		
					
					if (group.isPresent()) {
						
	          	  		Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user, group.get(), "recurrent");
	          			Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group.get(), "absence", rewRetrieveRequest.getDate());
	          			Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group.get(), "retrieve", rewRetrieveRequest.getDate());
						
	         			List<UserGroup> recurrentsGroup = userGroupRepository.findByGroupAndType( group.get(), "recurrent");
	          			List<UserGroup> abcensesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group.get(), "absence", rewRetrieveRequest.getDate());
	          			List<UserGroup> retrievesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group.get(), "retrieve", rewRetrieveRequest.getDate());
						
						if (userGroupService.userAssists(group.get(), rewRetrieveRequest.getDate(), user, recurrentUserGroup, abcenseUserGroup, retrieveUserGroup)) {
							return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: El usuario ya está asiste a ese evento "));
						}else {
							
							if (userGroupService.eventIsFull(group.get(), rewRetrieveRequest.getDate(), recurrentsGroup, abcensesGroup, retrievesGroup)) {
								return ResponseEntity
										.badRequest()
										.body(new MessageResponse("Error: El grupo está completo "));
							}else {
								
								//Group group = new Group (newGroupRequest.getCapacity(), newGroupRequest.getDescription(), newGroupRequest.getTimeofday(), newGroupRequest.getDayofweek(), newGroupRequest.isActived());
								
							    long absenceId = userGroupService.decreasePendingRecieveCount(user);							
																
								UserGroup newUserGroup = new UserGroup(user, group.get(), "retrieve", true, rewRetrieveRequest.getDate(), absenceId); 
								
								userGroupRepository.save(newUserGroup);
								
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
		
  	    }else {
			  return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: No se pueden recuperar clases los días festivos"));
  	    }
		
	}
	
	
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PostMapping("calendar/createAbsence")
	public ResponseEntity<?> createAbsence(@Valid @RequestBody NewCalendarRequest rewRetrieveRequest, Authentication authentication) {
		
	    List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	    feastDayRepository.findAll().forEach(feastDays::add);	
	    
	    LocalDate date = Instant.ofEpochMilli(rewRetrieveRequest.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();  	    
	    Date dayDate	= new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime();
	    
		if(!userGroupService.containsDate(feastDays,dayDate)) {
			
			Date today = new Date();
			
			if(rewRetrieveRequest.getDate().after(today)) {
					
				Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());				
				
				if (group.isPresent()) {
					
					UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			  		User user = userRepository.findById( userDetailsImpl.getId()).get();
					
          	  		Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user, group.get(), "recurrent");
          			Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group.get(), "absence", rewRetrieveRequest.getDate());
          			Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group.get(), "retrieve", rewRetrieveRequest.getDate());
					
					if (userGroupService.userAssists(group.get(), rewRetrieveRequest.getDate(), user, recurrentUserGroup, abcenseUserGroup, retrieveUserGroup)) {						
			
						
						Optional<UserGroup> existRetrieve = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group.get(), "retrieve", rewRetrieveRequest.getDate());
	
						
						if(existRetrieve.isPresent()) {
							
					        try {
					        
					        	userGroupRepository.deleteById(existRetrieve.get().getId());
					        	
					        	Optional<UserGroup> absenceGroup = userGroupRepository.findById(existRetrieve.get().getAbsenceid());
					        	
					    		if (absenceGroup.isPresent()) {
					    			
					    			//System.out.println(absenceGroup.get());
					    			UserGroup currUserGroup  = absenceGroup.get(); 
					    			currUserGroup.setRetrieved(false);
					    			userGroupRepository.save(currUserGroup); 			
					    			
					    		} 		
					        	
					        } catch (Exception e) {
					        	return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
					        }
					  		
						}else {
							
							UserGroup newUserGroup = new UserGroup(user, group.get(), "absence", false, rewRetrieveRequest.getDate(),null); 			
							System.out.println(newUserGroup);
							userGroupRepository.save(newUserGroup);							
						}						
						
						return ResponseEntity.ok(new MessageResponse("Ausencia creada con éxito!"));
						
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

	    }else {
			  return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: No se pueden crear ausencias los días festivos"));
  	    }
		 
		
	}
	
}
