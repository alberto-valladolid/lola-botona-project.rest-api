package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.AppConfig;
import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.request.SignupRequest;
import com.lolabotona.restapi.payload.request.ChgAppConfig;
import com.lolabotona.restapi.payload.request.NewAssistRequest;
import com.lolabotona.restapi.payload.request.NewFeastDayRequest;
import com.lolabotona.restapi.payload.request.NewGroupRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.repository.AppConfigRepository;
import com.lolabotona.restapi.repository.FeastDayRepository;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.repository.UserRepository;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.Optional;
import java.util.Optional;

import javax.validation.Valid;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/secure")
public class AdminController {
	
	@Autowired 
	private AppConfigRepository appConfigRepository; 

	
	@Autowired 
	private FeastDayRepository feastDayRepository; 

	@Autowired 
	private UserRepository userRepository; 
	
	@Autowired 
	private UserGroupRepository userGroupRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	@Autowired 
	private GroupRepository groupRepository; 
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/users")
    public ResponseEntity<List<User>> getAllTutorials() {
	  try {
		  
	      List<User> users = new ArrayList<User>();	 
	      userRepository.findAll().forEach(users::add);	
	      if (users.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	      }	
	      return new ResponseEntity<>(users, HttpStatus.OK);	
	      
       } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
     }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		Optional<User> user = userRepository.findById( id);
	
		if (user.isPresent()) {
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
    	System.out.println("entra"); 
      try {
      	userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
      }
    }
    
	
	// ejemplo: @PreAuthorize("hasAnyRole('ADMIN') and #newUser.getUsername() == authentication.principal.username ")
	@PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User newUser) {
      Optional<User> storedUserData = userRepository.findById(id);
      
      
      if (storedUserData.isPresent()) { 
    	  User user = storedUserData.get();    	
          if(user.getUsername().equals(newUser.getUsername())   ){
              
              user.setRoles(newUser.getRole());
              user.setName(newUser.getName());  
              user.setUsername(newUser.getUsername());  
              
              if(newUser.getPassword()!= "") {    
              	user.setPassword(passwordEncoder.encode(newUser.getPassword()));
              }               
              
              return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
              
          } else {
        	 
        	  if(!userRepository.existsByUsername(newUser.getUsername())   ) {       
                
                  user.setRoles(newUser.getRole());
                  user.setName(newUser.getName());  
                  user.setUsername(newUser.getUsername());  
                  
                  if(newUser.getPassword()!= "") {    
                  	user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                  }               
                  
                  return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
                
              }else {
          	       return ResponseEntity
	      					.badRequest()
	      					.body(new MessageResponse("Error: Ya existe un usuario con ese teléfono!"));
              }    
        	  
          }      
          
      } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
              
    }    
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<?> addUserByAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Ya existe un usuario con ese teléfono!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getRole(),					
							 passwordEncoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getName());

		String requestRole = signUpRequest.getRole();


		if (requestRole == null) {

				new RuntimeException("Error: Role is required.");
		
		}
		
		user.setRoles(requestRole);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Usuario creado con éxito!"));
	}
	
	
	@GetMapping("/groups")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Group>> getGroups() {
	
		  try {
			   
		      List<Group> groups = new ArrayList<Group>();	 
		      groupRepository.findAll().forEach(groups::add);	
		      
		      if (groups.isEmpty()) {
		        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		      }
		      
		      return new ResponseEntity<>(groups, HttpStatus.OK);	
		      
	       } catch (Exception e) {
		      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	       }
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {    
      try {
    	  groupRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
      }
    }
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/groups")
	public ResponseEntity<?> addGroupByAdmin(@Valid @RequestBody NewGroupRequest newGroupRequest) throws ParseException {
		
		List<Group> groups = new ArrayList<Group>();			
		groups = groupRepository.findByDayofweekAndShoworder(newGroupRequest.getDayofweek(), newGroupRequest.getShoworder());
				
		
		if ( groups.isEmpty()) { 
			
			String stringTime; 
			
			if(newGroupRequest.getStartTimeHours() < 10) {
				stringTime = "0"+  newGroupRequest.getStartTimeHours(); 
			}else {
				stringTime =  ""+ newGroupRequest.getStartTimeHours(); 
			}
			
		    stringTime += ":"; 
		    		
			if(newGroupRequest.getStartTimeMins() < 10) {
				stringTime += "0"+  newGroupRequest.getStartTimeMins(); 
			}else {
				stringTime += ""+  newGroupRequest.getStartTimeMins(); 
			}
			
			
			SimpleDateFormat dateSDF = new SimpleDateFormat("HH:mm");
		
			long ms = dateSDF.parse(stringTime).getTime();
			Time startTime = new Time(ms);		
				
			Group group = new Group(newGroupRequest.getCapacity(), newGroupRequest.getDescription(), newGroupRequest.getShoworder(),  newGroupRequest.getDayofweek(), newGroupRequest.isActive(), startTime);
			groupRepository.save(group);
			return ResponseEntity.ok(new MessageResponse("Grupo creado con éxito!"));			
			
		}else {			
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe un grupo en ese día y en ese orden"));
		}
		
		
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/groups/{id}")
	public ResponseEntity<Group> getGroupById(@PathVariable("id") long id) {
		Optional<Group> group = groupRepository.findById( id);
		if (group.isPresent()) {
			return new ResponseEntity<>(group.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	

	@PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/groups/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable("id") long id,@Valid @RequestBody NewGroupRequest newGroup) throws ParseException {
		

		
		Optional<Group> storedGroupData = groupRepository.findById(id);	
		
		if(storedGroupData.isPresent()) {
			
			List<Group> groups = new ArrayList<Group>();	
			
			groups = groupRepository.findByDayofweekAndShoworder(newGroup.getDayofweek(), newGroup.getShoworder());
			
			if ( ( storedGroupData.get().getdayofweek() == newGroup.getDayofweek()  &&    newGroup.getShoworder() == storedGroupData.get().getshoworder() )  ||  groups.isEmpty())  {
				
				
				String stringTime; 
				
				if(newGroup.getStartTimeHours() < 10) {
					stringTime = "0"+  newGroup.getStartTimeHours(); 
				}else {
					stringTime =  ""+ newGroup.getStartTimeHours(); 
				}
				
			    stringTime += ":"; 
			    		
				if(newGroup.getStartTimeMins() < 10) {
					stringTime += "0"+  newGroup.getStartTimeMins(); 
				}else {
					stringTime += ""+  newGroup.getStartTimeMins(); 
				}
				
				SimpleDateFormat dateSDF = new SimpleDateFormat("HH:mm");
			
				long ms = dateSDF.parse(stringTime).getTime();
				Time startTime = new Time(ms);
				
				Group group = storedGroupData.get(); 
				group.setStartTime(startTime);
		    	group.setCapacity(newGroup.getCapacity());
		    	group.setDescription(newGroup.getDescription());  
		    	group.setshoworder(newGroup.getShoworder());
		    	group.setdayofweek(newGroup.getDayofweek());
		    	group.setActive(newGroup.isActive());  
	            return new ResponseEntity<>(groupRepository.save(group), HttpStatus.OK); 
	              
			}else {	
				
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe un grupo en ese día y en ese orden"));
				
			}
	
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
      
    } 
	
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/feast-days")
    public ResponseEntity<List<FeastDay>> getAllFeastDays() {
	  try {
		  
	      List<FeastDay> feastDays = new ArrayList<FeastDay>();	 
	      feastDayRepository.findAll().forEach(feastDays::add);	
	      if (feastDays.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	      }		      
	      
	      return new ResponseEntity<>(feastDays, HttpStatus.OK);	
	      
       } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
     }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/feast-days/{id}")
    public ResponseEntity<HttpStatus> deleteFeastDay(@PathVariable("id") long id) {   
      try {
    	  feastDayRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
      }
    }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/feast-days")
	public ResponseEntity<?> addFeastDay(@Valid @RequestBody NewFeastDayRequest signUpRequest) {
		
		//System.out.println(signUpRequest.getDate());
		
		if ( !feastDayRepository.existsByDate(signUpRequest.getDate())) { 
			
			FeastDay feastDay = new FeastDay(signUpRequest.getDate()); 
					feastDayRepository.save(feastDay);
			return ResponseEntity.ok(new MessageResponse("Festivo creado con éxito!"));			
			
		}else {			
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe ese día como festivo "));
		}			
		
	}	
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/app-config")
    public ResponseEntity<?> getAppConfig() {			
		
	  try {
		  
		  
		  Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
		
		  if (!appConfig.isPresent()) {
			  
		    AppConfig newAppConfig = new AppConfig(1,120,30); //default config values
		  	
			appConfigRepository.save(newAppConfig);	
			
			appConfig = appConfigRepository.findById( (long) 1);
			  
		  } 

		  return new ResponseEntity<>(appConfig.get(), HttpStatus.OK);	
	      
       } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
	  
     }
	
	@PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/app-config")
    public ResponseEntity<?> updateAppConfig(  @Valid @RequestBody ChgAppConfig chgAppConfig ) {
		
	  try {
		  
		  Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
	
		  if (appConfig.isPresent()) {		
			  
		    AppConfig appConfigUpdate = appConfig.get();
		    
		    appConfigUpdate.setAbsenceDays(chgAppConfig.getAbsenceDays());
		    appConfigUpdate.setEventMinutes(chgAppConfig.getEventMinutes());			  
	
            return new ResponseEntity<>(appConfigRepository.save(appConfigUpdate), HttpStatus.OK);         
            
		  } else {
		    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		  }
		  
		  
      } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        
      }

	
	}
	
	

	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN'))")
	@GetMapping("events")
//    public ResponseEntity<?> getCalendarData() {
	//public ResponseEntity<?> getCalendarData(@RequestParam Map<String,String> requestParams)  throws Exception {
	public ResponseEntity<?> getCalendarData(
											@RequestParam("userId") String userId,
											@RequestParam("groupId") String groupId,
											@RequestParam("type") String type,
//											@RequestParam("fromDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)   LocalDateTime fromDate, 
//											@RequestParam("toDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)   LocalDateTime toDate
											@RequestParam("fromDate") String fromDate,
											@RequestParam("toDate") String toDate
											)  throws Exception {
		
		
        List<User> users = new ArrayList<User>();	
        List<Group> groups = new ArrayList<Group>();
        List<String> types = new ArrayList<String>();
        
        Timestamp fromDateTimestamp;
        Timestamp toDateTimestamp;
        
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        
        String DATE_FORMAT = "yyyy-MM-dd";
		
		if(!userId.equals("null") ) {
			
			Optional<User> user  = userRepository.findById(Long.parseLong(userId));
			
			if(user.isPresent()) {
				users.add(user.get()); 
			}else {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: User with id " + userId + " not found"));
			}
			
		}else {
			 userRepository.findAll().forEach(users::add);	
		}
			
					
			
		if(!groupId.equals("null") ) {
			
			Optional<Group> group  = groupRepository.findById(Long.parseLong(groupId));
			
			if(group.isPresent()) {
				groups.add(group.get()); 
			}else {
				return ResponseEntity.badRequest().body(new MessageResponse("Error:  Group with id " + groupId + " not found"));
			}
			
		}else {
			groupRepository.findAll().forEach(groups::add);	
		}
			
			
		if(!type.equals("null") ) {
			types.add(type);
		}else {
			types.add("absence");
			types.add("retrieve");
			types.add("recurrent");
		}
		
		if(!fromDate.equals("null") ) {
			
	        try {
	            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(fromDate);
	            fromDate += " 00:00:00"; 
	            fromDateTimestamp = Timestamp.valueOf(fromDate);
	            //System.out.println("fecha fromdate valida");
	            
	        } catch (ParseException e) {
	        	return ResponseEntity.badRequest().body(new MessageResponse("Error: From Date not valid " ));
	        }
			
		}else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: From Date not found " ));
		}

		if(!toDate.equals("null") ) {
			
	        try {
	            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(toDate);
	            toDate += " 00:00:00"; 
	            toDateTimestamp = Timestamp.valueOf(toDate);
	            //System.out.println("fecha toDate valida");
	            
	        } catch (ParseException e) {
	        	return ResponseEntity.badRequest().body(new MessageResponse("Error: toDate not valid " ));
	        }

		}else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: To date not valid "));
		}
		
		List<UserGroup> userGroupList = userGroupRepository.findByGroupInAndUserInAndTypeInAndDateatBetweenOrderByDateat(groups, users,   types  , fromDateTimestamp, toDateTimestamp);
			
		if(types.contains("recurrent"))
			userGroupList.addAll(userGroupRepository.findByGroupInAndUserInAndType(groups, users,"recurrent")) ;
		
		System.out.println("userGroupList " + userGroupList);
		
	    return new ResponseEntity<>(userGroupList, HttpStatus.OK);         
	}
	
	
	
	
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/events")
	public ResponseEntity<?> addEvent(@Valid @RequestBody NewAssistRequest newAssistRequest) {
		
		//System.out.println(signUpRequest.getDate());
		
		System.out.println(newAssistRequest.getDate()== null);
		
		if((newAssistRequest.getType() == "absence" || newAssistRequest.getType() == "retrieve") && newAssistRequest.getDate() == null  ) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Es necesario seleccionar una fecha para las ausencias o las recuperaciones "));
		}else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Todo ok "));
		}
		
		
		
		
//		if ( !feastDayRepository.existsByDate(newAssistRequest.getDate())) { 
//			
//			FeastDay feastDay = new FeastDay(newAssistRequest.getDate()); 
//					feastDayRepository.save(feastDay);
//			return ResponseEntity.ok(new MessageResponse("Asistencia creada con éxito!"));			
//			
//		}else {			
//			return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe ese día como festivo "));
//		}			
		
	}	
	
}
