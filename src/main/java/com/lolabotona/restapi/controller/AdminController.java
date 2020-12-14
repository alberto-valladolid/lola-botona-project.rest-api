package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.AppConfig;
import com.lolabotona.restapi.model.FeastDay;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserTeacher;
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
import com.lolabotona.restapi.repository.UserTeacherRepository;
import com.lolabotona.restapi.service.UserGroupService;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
	
	@Autowired 
	private UserTeacherRepository userTeacherRepository; 
	
	@Autowired 
	private UserGroupService userGroupService; 
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/users")
    public ResponseEntity<?> getAllTutorials() {
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
	public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
		Optional<User> user = userRepository.findById( id);
    	HashMap<String, Object> response = new HashMap<String , Object>();

	
		if (user.isPresent()) {
			
			response.put("user", user.get());
			List<UserTeacher> userTeacherList  = userTeacherRepository.findByUser(user.get());
			
			List<Long> teachersIdList = new ArrayList<Long>();

			if(!userTeacherList.isEmpty()){		
				
				for (UserTeacher teacher : userTeacherList) {
            		  
					teachersIdList.add(teacher.getTeacher().getId());
				}
				
			}
			
			//System.out.println(userTeacherList);
	    	
			response.put("UserTeachers", teachersIdList);	  
	    	  
		    return new ResponseEntity<>(response, HttpStatus.OK);				
			
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
   // public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User newUser) {
	 public ResponseEntity<?> updateUser(@PathVariable("id") long id,  @RequestBody SignupRequest signUpRequest) {
	
      Optional<User> storedUserData = userRepository.findById(id);
      
      
      if (storedUserData.isPresent()) { 
    	  User user = storedUserData.get();    	
          if(user.getUsername().equals(signUpRequest.getUsername())   ){
              
              user.setRoles(signUpRequest.getRole());
              user.setName(signUpRequest.getName());  
              user.setUsername(signUpRequest.getUsername());  
              user.setType(signUpRequest.getType()); 
              
              if(signUpRequest.getPassword()!= "") {    
              	user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
              }  
              
              
              
              userTeacherRepository.deleteInBatch(userTeacherRepository.findByUser(user));
              
              
              if(signUpRequest.getTeachers() != null ) {
            	  
            	  for (int teacher : signUpRequest.getTeachers()) {
            		  
            		  Optional<User> teacherObject =  userRepository.findById((long) teacher);
            		  
            		  if(teacherObject.isPresent()) {
	            		  UserTeacher newUserTeacher = new UserTeacher(user, teacherObject.get());
	            		  userTeacherRepository.save(newUserTeacher);
            		  }
            	  }
              }
              
              return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
              
          } else {
        	 
        	  if(!userRepository.existsByUsername(signUpRequest.getUsername())   ) {       
                
                  user.setRoles(signUpRequest.getRole());
                  user.setName(signUpRequest.getName());  
                  user.setUsername(signUpRequest.getUsername());  
                  user.setType(signUpRequest.getType()); 
                  
                  if(signUpRequest.getPassword()!= "") {    
                  	user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                  }    
                  
                  userTeacherRepository.deleteInBatch(userTeacherRepository.findByUser(user));
                  
                  
                  if(signUpRequest.getTeachers() != null ) {
                	  
                	  for (int teacher : signUpRequest.getTeachers()) {
                		  
                		  Optional<User> teacherObject =  userRepository.findById((long) teacher);
                		  
                		  if(teacherObject.isPresent()) {
    	            		  UserTeacher newUserTeacher = new UserTeacher(user, teacherObject.get());
    	            		  userTeacherRepository.save(newUserTeacher);
                		  }
                	  }
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
							 signUpRequest.getName(),
							 signUpRequest.getType()							 
		);

		String requestRole = signUpRequest.getRole();

		if (requestRole == null) {
				new RuntimeException("Error: Role is required.");		
		}
		
		if (signUpRequest.getType() != "alumn" || signUpRequest.getType() != "teacher") {
			new RuntimeException("Error: Type must be 'alumn' or 'teacher'.");	
		}
		
		user.setRoles(requestRole);
		userRepository.save(user);	
		
		if(signUpRequest.getTeachers().length > 0) {
		    for(int i=0; i<signUpRequest.getTeachers().length; i++) {
		    	
		    	Optional<User> userTeacher = userRepository.findById((long)signUpRequest.getTeachers()[i]);
				if (userTeacher.isPresent()) {		
					
		    		UserTeacher newUserTeacher = new UserTeacher(user, userTeacher.get());	
		    		userTeacherRepository.save(newUserTeacher);
		    		
		    				
		    	}		    					 
	
		    }
		}
		

		return ResponseEntity.ok(new MessageResponse("Usuario creado con éxito!"));
	}
	
	
	@GetMapping("/groups")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getGroups() {
	
		  try {
			   
		      List<Group> groups = new ArrayList<Group>();	 
		      groupRepository.findAll().forEach(groups::add);	
		      System.out.println(groups);
		      if (groups.isEmpty()) {
		        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		      }
		      
		      List<User> teachers = new ArrayList<User>();	 
		      teachers = userRepository.findByType("teacher");  		      
		      
		      HashMap<String, Object> response = new HashMap<String , Object>();
	    	  response.put("groups", groups);
	    	  response.put("teachers", teachers);		      
		      
	    	  return new ResponseEntity<>(response, HttpStatus.OK);	
		      
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
			
			Optional<User> teacher = userRepository.findByIdAndType(newGroupRequest.getTeacherId(), "teacher");
			if (teacher.isPresent()) {
				
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
					
				Group group = new Group(newGroupRequest.getCapacity(), newGroupRequest.getDescription(), newGroupRequest.getShoworder(),  newGroupRequest.getDayofweek(), newGroupRequest.isActive(), startTime, teacher.get());
				groupRepository.save(group);
				return ResponseEntity.ok(new MessageResponse("Grupo creado con éxito!"));					
				
			} else {	
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Teacherid not found."));	
			}					
			
		}else {			
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe un grupo en ese día y en ese orden"));
		}
		
		
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/actualizargrupos")
	public ResponseEntity<?> actualizargrupos(  NewGroupRequest newGroupRequest) throws ParseException {
		
		  List<Group> groups = new ArrayList<Group>();	 
	      groupRepository.findAll().forEach(groups::add);					
		
		
		Optional<User> teacher = userRepository.findByIdAndType(1, "teacher");
		
		for (Group group : groups) { 
			
			group.setTeacher(teacher.get());
			groupRepository.save(group);
		}
		
		return new ResponseEntity<>(groups, HttpStatus.OK);
		
		
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
				
				
				Optional<User> teacher = userRepository.findByIdAndType(newGroup.getTeacherId(), "teacher");	
				
				if(teacher.isPresent()) {
				
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
			    	group.setTeacher(teacher.get());
		            return new ResponseEntity<>(groupRepository.save(group), HttpStatus.OK); 
		            
				}else {	
					
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Profesor no encontrado"));
					
				}
	              
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
	public ResponseEntity<?> addFeastDay(@Valid @RequestBody NewFeastDayRequest newFeastDayRequest) throws ParseException {	
		
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		System.out.println(newFeastDayRequest.getDate());
		
		String dateRequestString = newFeastDayRequest.getDate(); 
		
		java.util.Date dateRequest =   format.parse(dateRequestString);
		
		java.sql.Date sqlDate = new java.sql.Date(dateRequest.getTime());

		
		
		if ( !feastDayRepository.existsByDate(sqlDate)) { 
			
			FeastDay feastDay = new FeastDay(sqlDate); 
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
		    appConfigUpdate.setEventMinutesToAllow(chgAppConfig.getEventMinutesToAllow());
	
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
	
	
    @DeleteMapping("/events/{id}")
    public ResponseEntity<HttpStatus> deleteAssist(@PathVariable("id") long id) {   
      try {
    	  	 
    	  Optional<UserGroup> userGroup = userGroupRepository.findById(id);
    	  
    	  if(userGroup.isPresent()) {
 
    		  if(userGroup.get().getType().equals("retrieve")) {
    			  
    			  if(userGroup.get().getAbsenceid()  != null) {
    				  
					  Optional<UserGroup> retrieveUserGroup = userGroupRepository.findById(userGroup.get().getAbsenceid());
					 
					  if (retrieveUserGroup.isPresent()) {								  
						 
							  UserGroup retrieveUserGroupUpdate = retrieveUserGroup.get();					    
							  retrieveUserGroupUpdate.setRetrieved(false);
							  userGroupRepository.save(retrieveUserGroupUpdate);					
			            
					  }
			  	  }
			 		    
    			  userGroupRepository.deleteById(id);
    			  
    			  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    			  
    			  
    		  }else {
    			  
        		  Optional<User> user = userRepository.findById(userGroup.get().getuserid());
        		  Optional<Group> group = groupRepository.findById(userGroup.get().getgroupid());
       			  
    			  if(userGroup.get().getType().equals("absence")) {
    				   
    				  Optional<UserGroup> retrieveUserGroup =  userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(),group.get() , "retrieve", userGroup.get().getDateat());
    				  
    				  if(retrieveUserGroup.isPresent()) {
    					  userGroupRepository.delete(retrieveUserGroup.get());
    				  }
    				  
    				  
    				  userGroupRepository.deleteById(id);
    				  
    				  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    				  
    			  }else {
    				  if(userGroup.get().getType().equals("recurrent")) {
    					  
    					  userGroupRepository.deleteAll(userGroupRepository.findByUserAndGroup(user.get(), group.get()));
    					  
    					  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    					  
        			  }else {
        				  return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        			  }
    			  }
    			  
    		  }
    		  
    	  }else {
    		  return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    	  }  
        
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
      }

    }
	
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/events")
	public ResponseEntity<?> addEvent(@Valid @RequestBody NewAssistRequest newAssistRequest) {
		
		//System.out.println(signUpRequest.getDate());

		
		if((newAssistRequest.getType().equals("absence") || newAssistRequest.getType().equals("retrieve") ) && (newAssistRequest.getDate() == null || newAssistRequest.getAddDeleteRetrieve() == null )) {
			
			if(newAssistRequest.getDate() == null)
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Es necesario seleccionar una fecha para crear una ausencia o recuperacion"));
			else
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Es necesario completar el campo 'Generar falta/consumir falta'"));

		}else {
			
			
			Optional<Group> group = groupRepository.findById(newAssistRequest.getGroupId());	
			Optional<User> user = userRepository.findById( newAssistRequest.getUserId());
	
			if( !group.isPresent() || !user.isPresent() ) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: UserId o el groupId no encontrados "));
			}else {
				
				Optional<UserGroup> recurrentUserGroup = userGroupRepository.findByUserAndGroupAndType(user.get(), group.get(), "recurrent");
				
				List<UserGroup> recurrentsGroup = userGroupRepository.findByGroupAndType( group.get(), "recurrent");
				
				if(newAssistRequest.getType().equals("recurrent")) {
					
					
					if( group.get().getCapacity() <= recurrentsGroup.size() ) {
						
						return ResponseEntity.badRequest().body(new MessageResponse("Error: Grupo completo "));
						
					}else {
					
						if(recurrentUserGroup.isPresent()) {
							return ResponseEntity.badRequest().body(new MessageResponse("Error: El usuario ya se encuentra asignado a ese grupo" ));
						}else{
							UserGroup newUserGroup = new UserGroup(user.get(), group.get(), "recurrent", false, null ,null); 
							System.out.println(newUserGroup);						
							userGroupRepository.save(newUserGroup);	
							
							return ResponseEntity.ok(new MessageResponse("Asistencia recurrent creada con éxito!"));
						}
						
					}
				}else {
					
					
					Calendar requestDateCal = Calendar.getInstance();
					
					requestDateCal.setTime(newAssistRequest.getDate());
					
					requestDateCal.add(Calendar.HOUR_OF_DAY, 12); 
					
					requestDateCal.add(Calendar.MINUTE, group.get().getshoworder()); 
					
					Timestamp timestampDate = new java.sql.Timestamp(requestDateCal.getTimeInMillis());
					
					
	      			List<UserGroup> abcensesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group.get(), "absence", timestampDate);
	      			List<UserGroup> retrievesGroup = userGroupRepository.findByGroupAndTypeAndDateat( group.get(), "retrieve", timestampDate);
					
	      			
	      			Optional<UserGroup> abcenseUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group.get(), "absence", timestampDate);
	      			Optional<UserGroup> retrieveUserGroup = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group.get(), "retrieve", timestampDate);
	      			
	      			boolean ignorePendingRetrieves;								  		
	      			if(newAssistRequest.getAddDeleteRetrieve().equals("true"))
	      				
	      				ignorePendingRetrieves = false; 
	      			
	      			else if(newAssistRequest.getAddDeleteRetrieve().equals("false")) {
	      				
	      				ignorePendingRetrieves = true; 
	      				
	      			}else {
	      				return ResponseEntity.badRequest().body(new MessageResponse("Error: Valor incorrecto del campo 'Generar falta/consumir falta'"));
	      			}
	      			
											
					if(newAssistRequest.getType().equals("absence")) {
						
						System.out.println(userGroupService.userAssists(group.get(), timestampDate, user.get(), recurrentUserGroup, abcenseUserGroup, retrieveUserGroup));
						
						if (userGroupService.userAssists(group.get(), timestampDate, user.get(), recurrentUserGroup, abcenseUserGroup, retrieveUserGroup)) {	
											
							
							Optional<UserGroup> existRetrieve = userGroupRepository.findByUserAndGroupAndTypeAndDateat(user.get(), group.get(), "retrieve", timestampDate);			
							
							if(existRetrieve.isPresent()) {
								
						        try {
						        
						        	userGroupRepository.deleteById(existRetrieve.get().getId());
						        	
						        	Optional<UserGroup> absenceGroup = userGroupRepository.findById(existRetrieve.get().getAbsenceid());
						        	
						    		if (absenceGroup.isPresent() && !ignorePendingRetrieves ) {
						
						    			UserGroup currUserGroup  = absenceGroup.get(); 
						    			currUserGroup.setRetrieved(false);
						    			userGroupRepository.save(currUserGroup); 			
						    			
						    		} 		
						        	
						        } catch (Exception e) {
						        	return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
						        }
						  		
							}else {
								
								UserGroup newUserGroup ;
								if(ignorePendingRetrieves)
									newUserGroup = new UserGroup(user.get(), group.get(), "absence", true, timestampDate,null); 	
								else
									newUserGroup = new UserGroup(user.get(), group.get(), "absence", false, timestampDate,null); 	

								//System.out.println(newUserGroup);
								userGroupRepository.save(newUserGroup);		
								
							}						
							
							return ResponseEntity.ok(new MessageResponse("Ausencia creada con éxito!"));
							
						}else {
							return ResponseEntity.badRequest().body(new MessageResponse("Error: Error al crear la ausencia, el usuario no asiste a clase ese día "  ));
						}	
						
					}else if (newAssistRequest.getType().equals("retrieve")) {
						
						if (userGroupService.userAssists(group.get(), timestampDate, user.get(), recurrentUserGroup, abcenseUserGroup, retrieveUserGroup)) {
							return ResponseEntity
									.badRequest()
									.body(new MessageResponse("Error: El usuario ya  asiste a clase ese día"));
						}else {									

							//ME LLEGO AQUI  if(ignorePendingRetrieves){}
							if (userGroupService.eventIsFull(group.get(), timestampDate, recurrentsGroup, abcensesGroup, retrievesGroup)) {
								return ResponseEntity
										.badRequest()
										.body(new MessageResponse("Error: El grupo está completo "));
							}else {
								
								
								Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
								
								if (!appConfig.isPresent()) {
								  
									return ResponseEntity
											.badRequest()
											.body(new MessageResponse("Error: La aplicación necesita autogenerar la configuración. El administrador debe acceder a la configuración de la aplicación."));
								  
								} 
								
								//comprueba si hay que restar una retrieve. 
								if(ignorePendingRetrieves) {									
									
									UserGroup newUserGroup = new UserGroup(user.get(), group.get(), "retrieve", true, timestampDate, null); 
									
									userGroupRepository.save(newUserGroup);
									
									return ResponseEntity.ok(new MessageResponse("Recuperación creada con éxito!"));						
			
																
								}else {										
																		
									if(userGroupService.getPendingRecieveCount(user.get(),appConfig.get()) > 0) {	
										
										long absenceId = userGroupService.decreasePendingRecieveCount(user.get(), appConfig.get());							
										
										UserGroup newUserGroup = new UserGroup(user.get(), group.get(), "retrieve", true, timestampDate, absenceId); 
										
										userGroupRepository.save(newUserGroup);
										
										return ResponseEntity.ok(new MessageResponse("Recuperación creada con éxito!"));
										
									}else {
										return ResponseEntity
												.badRequest()
												.body(new MessageResponse("Error: El usuario no tiene clases pendientes."));
									  
									}
									
								}
																	   
								
							}
							
						}
							
							
					}else {
						return ResponseEntity.badRequest().body(new MessageResponse("Error: Type debe ser recurrent, retrieve o absence "  ));
					}
						
				}
				
			}
			
			
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
	
	
	
	//@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/users/teachers")
    public ResponseEntity<?> getAllTeachers() {
	  try {		 

	      List<User> teachers = new ArrayList<User>();	 
	      teachers = userRepository.findByType("teacher");
	      if (teachers.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	      }	  
	      	      
	      return new ResponseEntity<>(teachers, HttpStatus.OK);	      
	      
       } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
     }
	
	
	
}
