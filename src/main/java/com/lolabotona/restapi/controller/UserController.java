package com.lolabotona.restapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.lolabotona.restapi.repository.UserRepository;
import com.lolabotona.restapi.repository.GroupRepository;
import com.lolabotona.restapi.repository.UserGroupRepository;
import com.lolabotona.restapi.service.UserDetailsImpl;
import com.lolabotona.restapi.model.Group;
import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserGroup;
import com.lolabotona.restapi.payload.request.ChgPwRequest;
import com.lolabotona.restapi.payload.response.MessageResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired 
	private UserRepository userRepository; 
	
	@Autowired 
	private UserGroupRepository userGroupRepository; 
	
	
	@Autowired 
	private GroupRepository groupRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; 

	@GetMapping("/all")
	@PreAuthorize("hasRole('USER')  or hasRole('ADMIN')")
	public String allAccess() {
		return "Landing usuarios invitados";
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
	
}
