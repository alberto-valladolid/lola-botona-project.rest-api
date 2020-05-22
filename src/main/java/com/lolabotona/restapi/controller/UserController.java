package com.lolabotona.restapi.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lolabotona.restapi.repository.AppConfigRepository;
import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;
import com.lolabotona.restapi.service.UserGroupService;
import com.lolabotona.restapi.model.AppConfig;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.payload.request.ChgPwRequest;
import com.lolabotona.restapi.payload.request.SignupRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired 
	private UserRepository userRepository; 
	
	
	@Autowired 
	private UserGroupService userGroupService; 
	

	@Autowired 
	private AppConfigRepository appConfigRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; 

	@GetMapping("/test/all")
	public String allAccess() {
		return "Landing usuarios invitados";
	}




	@PostMapping("/test/all")
	public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signUpRequest) {
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











	
	@GetMapping("/user")
	@PreAuthorize("(hasRole('USER')  or hasRole('ADMIN'))")
	public String userAccess(Authentication authentication) {
		return "Vista usuario logado";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "panel de la administración";
	}
		
	// ejemplo: @PreAuthorize("hasAnyRole('ADMIN') and #newUser.getUsername() == authentication.principal.username ")
	// authentication.getName(); 	
		
	@PreAuthorize("(hasAnyRole('USER') or hasRole('ADMIN')) ")
	@PutMapping("/users/password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChgPwRequest chgPwRequest, Authentication authentication) {
		 
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Optional<User> user = userRepository.findById( userDetailsImpl.getId());
		
		if(passwordEncoder.matches(chgPwRequest.getCurrentPassword(), user.get().getPassword())) {	
			User updatedUser = user.get();		
            updatedUser.setPassword(passwordEncoder.encode(chgPwRequest.getNewPassword()));
            return new ResponseEntity<>(userRepository.save(updatedUser), HttpStatus.OK);
		}else {
	    	  return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Contraseña actual incorrecta"));
		}
		
		
	}	
	
	@GetMapping("/users/pending-retrieves")
	@PreAuthorize("(hasRole('USER')  or hasRole('ADMIN'))")
	public   Map<String, Integer> getUserPendingRetrieveCount( Authentication authentication) {		
		
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
  		User user = userRepository.findById( userDetailsImpl.getId()).get();	
  		Optional<AppConfig> appConfig = appConfigRepository.findById( (long) 1);
		

		
		int userPendingRetrieveCount = userGroupService.getPendingRecieveCount(user,appConfig.get());	
		
		return Collections.singletonMap("count", userPendingRetrieveCount );

		
	}
	
	
}
