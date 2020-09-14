package com.lolabotona.restapi.payload.request;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewAssistRequest {

	private Long id; 
	
	private Long userId; 
	
	private Long groupId; 	
	
	private Date date;
	
	private String type;
	
	private String addDeleteRetrieve;
	
	
}
