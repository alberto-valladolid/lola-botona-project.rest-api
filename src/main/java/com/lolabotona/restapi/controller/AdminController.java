package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.payload.request.SignupRequest;
import com.lolabotona.restapi.payload.request.NewGroupRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserRepository;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
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
	private UserRepository userRepository; 
	
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
    	System.out.println("entra"); 
      try {
    	  groupRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
      }
    }
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/groups")
	public ResponseEntity<?> addGroupByAdmin(@Valid @RequestBody NewGroupRequest newGroupRequest) {
		
		List<Group> groups = new ArrayList<Group>();	
		groups = groupRepository.findByDayofweekAndTimeofday(newGroupRequest.getDayofweek(), newGroupRequest.getTimeofday());
				
		System.out.println(groups);
		
		if ( groups.isEmpty()) { 
			
			Group group = new Group (newGroupRequest.getCapacity(), newGroupRequest.getDescription(), newGroupRequest.getTimeofday(), newGroupRequest.getDayofweek(), newGroupRequest.isActived());
			groupRepository.save(group);
			return ResponseEntity.ok(new MessageResponse("Grupo creado con éxito!"));			
			
		}else {			
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe un grupo en ese día y ese turno"));
		}
		
		
	}
	
	
}
