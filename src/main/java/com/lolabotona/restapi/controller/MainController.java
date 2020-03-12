package com.lolabotona.restapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/auth")
public class MainController {
	
	@GetMapping("/home")
	@ResponseBody
	public String home() {
		return "Bienvenido a la home"; 
	}
	
	
}
