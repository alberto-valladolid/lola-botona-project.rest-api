package com.lolabotona.restapi.payload.request;

import javax.validation.constraints.*;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NewGroupRequest {
	
	 	
	    private int capacity;
		
	    @NotBlank    
	    private String description; 
	    
	    @NotBlank    
	    private String timeofday; //morning or afternoon. Es posible que no sea necesario 

	    private int dayofweek;

	    
	    private boolean actived;
	  
	 

}
