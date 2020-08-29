package com.lolabotona.restapi.payload.request;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NewFeastDayRequest {
	
	private Long id; 
	
	private String date;
	
}
