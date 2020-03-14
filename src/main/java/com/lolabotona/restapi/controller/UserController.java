package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.RestController;



import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/user/rest")
public class UserController {


	@GetMapping("/test/notSecured")
	public String notSecured() {		

		return "endpoint que no necesita seguridad"; 
	}
	

	@GetMapping("/test/secured")
	public String secured() {		

		return "endpoint que SI necesita seguridad"; 
	}
	
}
