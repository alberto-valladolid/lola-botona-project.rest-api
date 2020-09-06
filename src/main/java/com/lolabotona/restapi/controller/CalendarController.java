package com.lolabotona.restapi.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.AppConfig;
import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.request.NewCalendarRequest;
import com.lolabotona.restapi.payload.response.CalendarDayResponse;
import com.lolabotona.restapi.payload.response.CalendarEventResponse;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.repository.AppConfigRepository;
import com.lolabotona.restapi.repository.FeastDayRepository;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;
import com.lolabotona.restapi.service.UserGroupService;

import ch.qos.logback.core.net.SyslogOutputStream;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CalendarController {
	
	@Autowired 
	private AppConfigRepository appConfigRepository; 
	  
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
    public ResponseEntity<?> getCalendarData(@RequestParam long requestMonth) {
	  try {
		  System.out.println(" ");
		  System.out.println("START");
		  System.out.println(" ");
		  
		  UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
  		  User user = userRepository.findById( userDetailsImpl.getId()).get();
  		  
	      List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	      feastDayRepository.findAll().forEach(feastDays::add);
	      
	      
	      List<UserGroup> allUserGroups =  new ArrayList<UserGroup>(); 	
	      userGroupRepository.findAll().forEach(allUserGroups::add);
	      
	      List<Group> allGroups = groupRepository.findByActiveOrderByShoworderAsc(true);
	      
	      System.out.println(allUserGroups);
		  
		  int postMonthExtraDays, preMonthExtraDays;		  
	      List<CalendarDayResponse> calendarDays = new ArrayList<CalendarDayResponse>();      
	      Calendar c = Calendar.getInstance();	
	      c.add(Calendar.MONTH, (int) requestMonth);
	      
	      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      String startString,endString; 
	      LocalDate start,end; 
	      	      

	      //iterate previus month extra days	      
	      preMonthExtraDays = CalendarDayResponse.calcPreMonthExtraDays(c);
	      
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
	      	        	
	      	    	    
	      	    	    calendarDays.add(new CalendarDayResponse(start.getDayOfMonth(), false, start.getDayOfWeek().getValue(),false, null) ); 
	      	    	    
	      	    	} catch(Exception e) { //this generic but you can control another types of exception
	      	    	    // look the origin of excption 
	      	    	}
        		  
        	      start = start.plusDays(1);
        	  }       	  

    	  }
    	  
      	  //iterate current month days
    	  int currMonthDays;     	  
		  YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);	  
		  currMonthDays = yearMonthObject.lengthOfMonth();		  
  		  String currMonthString =   String.valueOf(c.get(Calendar.MONTH)+1);
  		  Date dayDate; 
  		  boolean isFeastDay;  
  		   
  		  if (c.get(Calendar.MONTH) <9) {
  			currMonthString = "0"+currMonthString; 
  		  }
  		  
  		  startString = c.get(Calendar.YEAR) + "-" + currMonthString +"-"+ "01";
      	  endString =   c.get(Calendar.YEAR) + "-" + currMonthString +"-"+  currMonthDays;
      	        	  
      	  System.out.println(startString + " " + endString);
      	  
      	  start = LocalDate.parse(startString);
      	  end = LocalDate.parse(endString); 
      	  
      	  while (!start.isAfter(end)) {      		
    
	  	        try {
	  	    	    //dayDate	= new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, start.getDayOfMonth()).getTime();
	  	        	dayDate	= new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), start.getDayOfMonth()).getTime();
	  	    	    
	  	    	    System.out.println(start.getDayOfMonth());
	  	    	    
	
	  	    	    
	  	    	    if(userGroupService.containsDate(feastDays, dayDate)) {	      	    	 
	  	    	    	isFeastDay = true; 
	  	    	    }else {
	  	    	    	isFeastDay = false; 
	  	    	    }	 
	  	    	    
	  	        	int dayOfWeek =  (start.getDayOfWeek().getValue() % 7) +1 ; 
	  	        	
	  	        	//find current day's events
	      	  		//List<Group> groups = groupRepository.findByDayofweekAndActiveOrderByShoworderAsc( dayOfWeek, true);

	      	  	    ArrayList<CalendarEventResponse> events = new ArrayList<CalendarEventResponse>();
	      	  	    
	      	  	    
		      	  	List<Group> todayGroups =  allGroups.stream()
		      	  	    .filter(g -> g.getdayofweek() == dayOfWeek).collect(Collectors.toList());
	      	  	    
//      	  	    System.out.println(" ");
//  	  	        System.out.println("Eventos del dia");  	  	        	
//	  	        	System.out.println(todayGroups);
//	  	        	System.out.println(" ");
      	  	    
	
      				//for (Group group : groups) {
      				for (Group group : todayGroups) { 
      		
      					CalendarEventResponse calendarEvent = new CalendarEventResponse();      					
      					String hourEvent; 
      					
      					if(group.getshoworder() < 10) {
      						hourEvent = "12:0"+group.getshoworder();
      					}else {
      						hourEvent = "12:"+group.getshoworder();
      					}
        				
          	    	    Date parsedDate1 = dateFormat.parse(c.get(Calendar.YEAR) + "-" + currMonthString +  "-" +start.getDayOfMonth() + " "  + hourEvent);          	    	    
          	    	    Timestamp timestampEvent = new java.sql.Timestamp(parsedDate1.getTime());  
          	    	    	
          	    	    String startEventString =  group.getStartTime().toString();
          	    	    startEventString = startEventString.substring(0, startEventString.length() - 3);          	    	  
          	    	    Date parsedStartDate = dateFormat.parse(c.get(Calendar.YEAR) + "-" + currMonthString +  "-" +start.getDayOfMonth() + " "  + startEventString);          	    	    
        	    	    Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());           	    	    
          	    	    
        	    	    
        	    	    List<UserGroup> recurrentsGroup =  allUserGroups.stream()  		      	  	  
       		      	  	    .filter(x -> (x.getGroup().equals(group) && x.getType().equals("recurrent") ))  
        		      	  	.collect(Collectors.toList());
        	    	    
        	    	    List<UserGroup> abcensesGroup =  allUserGroups.stream()  		      	  	  
           		      	  	    .filter(x -> (x.getGroup().equals(group) && x.getType().equals("absence") && x.getDateat().equals(timestampEvent) ))  
            		      	  	.collect(Collectors.toList());
        	    	   
        	     	    List<UserGroup> retrievesGroup =  allUserGroups.stream()  		      	  	  
           		      	  	    .filter(x -> (x.getGroup().equals(group) && x.getType().equals("retrieve") && x.getDateat().equals(timestampEvent) ))  
            		      	  	.collect(Collectors.toList());
        	     	    
        	     	    
        	     	    Optional<UserGroup> recurrentUserGroup = recurrentsGroup.stream()  		      	  	  
          		      	  	    .filter(x -> (x.getUser().equals(user)  ))
    	     	   				.findFirst();
        	     	    
        	     	    Optional<UserGroup> abcenseUserGroup = abcensesGroup.stream()  		      	  	  
         		      	  	    .filter(x -> (x.getUser().equals(user)  ))
         		      	  	    .findFirst();
        	     	   
        	     	    Optional<UserGroup> retrieveUserGroup = retrievesGroup.stream()  		      	  	  
        		      	  	    .filter(x -> (x.getUser().equals(user)  ))
        		      	  	    .findFirst();
        	     	    
	          			
	          			//List<UserGroup> recurrentsGroup = userGroupRepository.findByGroupAndType( group, "recurrent");
	          			//List<UserGroup> abcensesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "absence", timestampEvent);
	          			//List<UserGroup> retrievesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group, "retrieve", timestampEvent);	 
        	     	    
	          	  		//Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user, group, "recurrent");
//	          			Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group, "absence", timestampEvent);
//	          			Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user, group, "retrieve", timestampEvent);
      	     	    
	          			
	          			calendarEvent.setStartAt(startTimestamp);	          			
          	    	    calendarEvent.setTimeOfDay(timestampEvent);          	    	    
	          	    	calendarEvent.setGroupId(group.getId());
	          	    	calendarEvent.setDescription(group.getDescription());
	          	    	events.add(calendarEvent);   
	          	    	
	          	    	calendarEvent.setUserAssits(userGroupService.userAssists(group, timestampEvent ,user, recurrentUserGroup, abcenseUserGroup, retrieveUserGroup));		      	    	    
	          	    	calendarEvent.setUsers(userGroupService.getUserAndCapacity(group, timestampEvent, recurrentsGroup, abcensesGroup, retrievesGroup));
	          	    	    
          	    	    if(!calendarEvent.isUserAssits()) {
          	    	    	
          	    	    	calendarEvent.setFull(userGroupService.eventIsFull(group, timestampEvent, recurrentsGroup, abcensesGroup, retrievesGroup));
          	    	    	
          	    	    }
      					
      				}
	      				      			
	  	    	    calendarDays.add(new CalendarDayResponse(start.getDayOfMonth(), true,  start.getDayOfWeek().getValue(),isFeastDay,events)); 
	  	    	    
	  	    	} catch(Exception e) { //this generic but you can control another types of exception
	  	    	    // look the origin of excption 
	  	    	}
      		  
	  	        start = start.plusDays(1);
      	  }       	  

      	  
	  	  //iterate next month extra days
      	 postMonthExtraDays = CalendarDayResponse.calcPostMonthExtraDays(c);
	      
    	 if(postMonthExtraDays!=0) {
    		  			  
		 	  if(c.get(Calendar.MONTH) == 11) {  
			 
    			  startString = c.get(Calendar.YEAR)+1 + "-01-"+ "01";
            	  endString =   c.get(Calendar.YEAR)+1 + "-01-0"+ postMonthExtraDays  ;
    			  
    		  }else {
    			  String nextMonthString =   String.valueOf(c.get(Calendar.MONTH)+2);
    			  if (c.get(Calendar.MONTH) <8) {
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
	      	    	    calendarDays.add(new CalendarDayResponse(start.getDayOfMonth(), false, start.getDayOfWeek().getValue(),false, null)); 	      	    	  
	      	    	} catch(Exception e) { //this generic but you can control another types of exception
	      	    	    // look the origin of excption 
	      	    	}
        		  
        	      start = start.plusDays(1);
        	  }       	  

    	  }    	  
    	 
		 Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
		 	
		  if (!appConfig.isPresent()) {
		   
		 	 return ResponseEntity
		 			.badRequest()
		 			.body(new MessageResponse("Error: La aplicación necesita autogenerar la configuración. El administrador debe acceder a la configuración de la aplicación."));
		   
		  } 
    	  
    	  HashMap<String, Object> response = new HashMap<String , Object>();
    	  response.put("minsEditEvents", appConfig.get().getEventMinutes());
    	  response.put("days", calendarDays);
    	  response.put("dataPendingRecieveCount", userGroupService.getPendingRecieveCount(user,appConfig.get()));
  
    	  
	      return new ResponseEntity<>(response, HttpStatus.OK);	
	      
      } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }	
    }
	
	
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PostMapping("calendar/createRetrieve")
	public ResponseEntity<?> createRetrieve(@Valid @RequestBody NewCalendarRequest rewRetrieveRequest, Authentication authentication) throws ParseException {
	
	    List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	    feastDayRepository.findAll().forEach(feastDays::add);	
	    
	    List<UserGroup> allUserGroups =  new ArrayList<UserGroup>(); 	
	    userGroupRepository.findAll().forEach(allUserGroups::add);
	    
	    LocalDate date = Instant.ofEpochMilli(rewRetrieveRequest.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();  	    
	    Date dayDate	= new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime();
	    	   
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    
		if(!userGroupService.containsDate(feastDays,dayDate)) {
				
				UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = userRepository.findById( userDetailsImpl.getId()).get();
				
				Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
				
				if (!appConfig.isPresent()) {
				  
					return ResponseEntity
							.badRequest()
							.body(new MessageResponse("Error: La aplicación necesita autogenerar la configuración. El administrador debe acceder a la configuración de la aplicación."));
				  
				} 
				
				if(userGroupService.getPendingRecieveCount(user,appConfig.get()) > 0    ) {		
					
					Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());	
					
					if (group.isPresent()) {
						
						Date today = new Date();
						String dateMonthString;
						
						if(date.getMonthValue() < 10) {
							dateMonthString = "0"+date.getMonthValue(); 
						}else {
							dateMonthString = ""+date.getMonthValue(); 
						}
						
						//System.out.println("el mes es: " + date.getMonthValue());
						
						
						
				   	    String startEventString =  group.get().getStartTime().toString();
          	    	    startEventString = startEventString.substring(0, startEventString.length() - 3);          	    	  
          	    	    Date parsedStartDate = dateFormat.parse(date.getYear() + "-" + dateMonthString +  "-" +date.getDayOfMonth() + " "  + startEventString);          	    	    
//        	    	    Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());  
        	    	    
        	    	    
        	    	    
        	    	    
//        	    	    Timestamp testStartTimestamp  = new Timestamp(startTimestamp.getTime());
//        	    	    Timestamp testStartTimestamp2  = new Timestamp(startTimestamp.getTime());
//        	    	    
//        	    		Calendar fromCalendarTimestamp  = Calendar.getInstance();
//        	    		fromCalendarTimestamp.setTime(testStartTimestamp);
//        	    		fromCalendarTimestamp.add(Calendar.DAY_OF_YEAR, - appConfig.get().getAbsenceDays()); 
//          	    	    java.sql.Timestamp fromTimestamp = testStartTimestamp ;
//        	    	    fromTimestamp.setTime(fromCalendarTimestamp.getTime().getTime());
//        	    	    
//        	    	    
//
//        	    		Calendar toCalendarTimestamp  = Calendar.getInstance();
//        	    		toCalendarTimestamp.setTime(testStartTimestamp2);
//        	    		toCalendarTimestamp.add(Calendar.YEAR,  1); 
//          	    	    java.sql.Timestamp toTimestamp = testStartTimestamp2 ;
//          	    	    toTimestamp.setTime(toCalendarTimestamp.getTime().getTime());
          	    	    
        	    		
        	    		//fromSQLTimestamp.setTime(fromTimestamp.getTime().getTime());
        	    		
          	    	    
						//a partir de ahora puede recuperar en fechas pasadas
        	    	    //if(startTimestamp.after(today) ) {
        	    	    

    	   
        	    		//comprobar si el usuario tiene  faltas pendientes de dos meses antes de la fecha del evento.
        	    	    //if( userGroupRepository.countByTypeAndUserAndRetrievedAndDateatBetween("absence", user, false, fromTimestamp, toTimestamp) > 0 ) {	
        	    	    	
        
							
	        	    	    List<UserGroup> recurrentsGroup =  allUserGroups.stream()  		      	  	  
	           		      	  	    .filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("recurrent") ))  
	            		      	  	.collect(Collectors.toList());
	            	    	    
            	    	    List<UserGroup> abcensesGroup =  allUserGroups.stream()  		      	  	  
               		      	  	    .filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("absence") && x.getDateat().equals(rewRetrieveRequest.getDate()) ))  
                		      	  	.collect(Collectors.toList());
            	    	   
            	     	    List<UserGroup> retrievesGroup =  allUserGroups.stream()  		      	  	  
               		      	  	    .filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("retrieve") && x.getDateat().equals(rewRetrieveRequest.getDate()) ))  
                		      	  	.collect(Collectors.toList());            	     	    
            	     	    
            	     	    
            	     	    Optional<UserGroup> recurrentUserGroup = recurrentsGroup.stream()  		      	  	  
              		      	  	    .filter(x -> (x.getUser().equals(user)  ))
        	     	   				.findFirst();
            	     	    
            	     	    Optional<UserGroup> abcenseUserGroup = abcensesGroup.stream()  		      	  	  
             		      	  	    .filter(x -> (x.getUser().equals(user)  ))
             		      	  	    .findFirst();
            	     	   
            	     	    Optional<UserGroup> retrieveUserGroup = retrievesGroup.stream()  		      	  	  
            		      	  	    .filter(x -> (x.getUser().equals(user)  ))
            		      	  	    .findFirst();
         	     	

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
									
									
									Date  max = parsedStartDate;
							
									
									Calendar cal = Calendar.getInstance();
									cal.setTime(max);
									cal.add(Calendar.MINUTE, - appConfig.get().getEventMinutesToAllow());
									Date min = cal.getTime();
									
									if (today.after(min) && today.before(max) ) {
										
										long absenceId = userGroupService.decreasePendingRecieveCount(user,appConfig.get());	
										UserGroup newUserGroup = new UserGroup(user, group.get(), "retrieve", true, rewRetrieveRequest.getDate(), absenceId);
										userGroupRepository.save(newUserGroup);
										return ResponseEntity.ok(new MessageResponse("Recuperación creada con éxito!"));
										
									}else {
										
										
										return ResponseEntity
												.badRequest()
												.body(new MessageResponse("Error: Solo es posible recuperar clases si falta menos de "+ ((appConfig.get().getEventMinutesToAllow())/60)/24 +" dias para su inicio "));
										
										
									
									}
									

	
									
								}
								
							}
							
						
//						}else {
//							 return ResponseEntity
//									.badRequest()
//									.body(new MessageResponse("Error: Han pasado más de " + appConfig.get().getAbsenceDays() + " días desde su última ausencia, el plazo para recuperar la clase ha terminado."));
//						}
							
//						}else {
//							  return ResponseEntity
//										.badRequest()
//										.body(new MessageResponse("Error: No se pueden crear asistencias en fechas pasadas"));
//						}
						
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
						.body(new MessageResponse("Error: No se pueden recuperar clases los días festivos"));
  	    }
		
	}
		
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PostMapping("calendar/createAbsence")
	public ResponseEntity<?> createAbsence(@Valid @RequestBody NewCalendarRequest rewRetrieveRequest, Authentication authentication) throws ParseException {
		
	    List<FeastDay> feastDays = new ArrayList<FeastDay>();    
	    feastDayRepository.findAll().forEach(feastDays::add);	
	    
	    List<UserGroup> allUserGroups =  new ArrayList<UserGroup>(); 	
	    userGroupRepository.findAll().forEach(allUserGroups::add);
	    		
	    LocalDate date = Instant.ofEpochMilli(rewRetrieveRequest.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();  	    
	    Date dayDate	= new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime();
	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		if(!userGroupService.containsDate(feastDays,dayDate)) {
				        
			Date today = new Date();
				
			Optional<Group> group = groupRepository.findById(rewRetrieveRequest.getGroupid());				
			
			if (group.isPresent()) {	
				
				Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
			 	 	
			 	if (!appConfig.isPresent()) {
			 	 
			 		 return ResponseEntity
			 				.badRequest()
			 				.body(new MessageResponse("Error: La aplicación necesita autogenerar la configuración. El administrador debe acceder a la configuración de la aplicación."));
			 	 
			 	} 
								
				String currMonthString; 
				
				if(date.getMonthValue() < 10) {
					currMonthString = "0"+date.getMonthValue(); 
				}else {
					currMonthString = ""+date.getMonthValue(); 
				}
				
		   	    String startEventString =  group.get().getStartTime().toString();
  	    	    startEventString = startEventString.substring(0, startEventString.length() - 3);          	    	  
  	    	    Date parsedStartDate = dateFormat.parse(date.getYear() + "-" + currMonthString +  "-" +date.getDayOfMonth() + " "  + startEventString);          	    	    
	    	    Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());     
	    	    Timestamp startTimestamp2 = new java.sql.Timestamp(parsedStartDate.getTime());  
	    	    
	    	    System.out.println("HOy: "+startTimestamp);
	    	    
	 
	    	    
	    	    //SE RESTAN LOS MINUTOS DE LA CONFIGURACIÓN AL TIMESTAMP DEL EVENTO
	    	    
	    	    
	    	       
	    	    //limite para abrir (permitir) las ausencias.Hora de inicio menos los minutos de appConfig.getEventMinutesToAllow
	    	    java.sql.Timestamp openAbsenceLimit = startTimestamp ;
	    		Calendar calOpenAbsenceLimit  = Calendar.getInstance();
	    		calOpenAbsenceLimit.setTime(startTimestamp);
	    		calOpenAbsenceLimit.add(Calendar.MINUTE, - appConfig.get().getEventMinutesToAllow());
	    		openAbsenceLimit.setTime(calOpenAbsenceLimit.getTime().getTime());
	    		
	    		
	    		
	    	    //limite para cerrar las ausencias. Hora de inicio menos los minutos de appConfig.getEventMinutes
	    	    java.sql.Timestamp closeAbseceLimit = startTimestamp2 ;
	    		Calendar calCloseAbseceLimit  = Calendar.getInstance();
	    		calCloseAbseceLimit.setTime(startTimestamp2);
	    		calCloseAbseceLimit.add(Calendar.MINUTE, - appConfig.get().getEventMinutes());
	    		closeAbseceLimit.setTime(calCloseAbseceLimit.getTime().getTime());
	    		
	    		
	    		System.out.println("HOy: "+startTimestamp);
	    		
	    		System.out.println("open: limit "+ calOpenAbsenceLimit.getTime());
	    		System.out.println("close limit: "+ calCloseAbseceLimit.getTime());
	    	    
				if(  today.after(openAbsenceLimit)  && today.before(closeAbseceLimit)  ) {
				
				
					UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			  		User user = userRepository.findById( userDetailsImpl.getId()).get();
			  		
			  		
			  		//se filtran los grupos que coincidan con el grupo, el usuario y el tipo
    	     	    Optional<UserGroup> recurrentUserGroup =  allUserGroups.stream()  		      	  	  
    	     	    		.filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("recurrent") && x.getUser().equals(user)))  
	     	   				.findFirst();
    	     	    
    	     	    Optional<UserGroup> abcenseUserGroup = allUserGroups.stream()  		      	  	  
    	     	    		 .filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("absence") && x.getDateat().equals(rewRetrieveRequest.getDate()) && x.getUser().equals(user) ))  
     		      	  	    .findFirst();
    	     	   
    	     	    Optional<UserGroup> retrieveUserGroup = allUserGroups.stream()  		      	  	  
    	     	    		.filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("retrieve") && x.getDateat().equals(rewRetrieveRequest.getDate()) && x.getUser().equals(user) ))  
    		      	  	    .findFirst();
					
    	     	                    			
          			//si el usuario asiste ese día
					if (userGroupService.userAssists(group.get(), rewRetrieveRequest.getDate(), user, recurrentUserGroup, abcenseUserGroup, retrieveUserGroup)) {						
	
						
					    Optional<UserGroup> existRetrieve = allUserGroups.stream()  		      	  	  
        	     	    		.filter(x -> (x.getGroup().equals(group.get()) && x.getType().equals("retrieve") && x.getDateat().equals(rewRetrieveRequest.getDate()) && x.getUser().equals(user) && x.getDateat().equals(rewRetrieveRequest.getDate()) ))  
        		      	  	    .findFirst();						
						
	
						//si ya existe una recuperación ese día
						if(existRetrieve.isPresent()) {
							
							
							return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: Esta asistencia es una recuperación. No es posible anular recuperaciones. "  ));
							
							
					  		
						}else {
							
							//crea la ausencia
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
					
				}else {
					
					if( !today.after(openAbsenceLimit)) {
						 return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: Solo se puede anular clases cuando falten menos de "+  (appConfig.get().getEventMinutesToAllow()/60)/24 +" día/s para su comienzo"));
					}else  {
						
						 return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: La clase ya ha comenzado o faltan menos de "+  appConfig.get().getEventMinutes()/60 +" día/s para su comienzo"));
						
//						 return ResponseEntity
//									.badRequest()
//									.body(new MessageResponse("Error: No se pueden crear ausencia en fechas pasadas"));
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
						.body(new MessageResponse("Error: No se pueden crear ausencias los días festivos"));
  	    }
		 
		
	}
	
}
