package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.repository.UserRepository;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;

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



@RestController
@RequestMapping("/secure/rest")
public class AdminController {

	@Autowired 
	private UserRepository userRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
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
	@PostMapping("/admin/add")
	public String addUserByAdmin(@RequestBody User user) {		
		String pwd=user.getPassword();
		String encryptPwd=passwordEncoder.encode(pwd);
		user.setPassword(encryptPwd);
		userRepository.save(user); 
		return " usuario creado con exito"; 
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/hi")
	public String holaMundo() {		

		return "Hola mundo"; 
	}
	
}
