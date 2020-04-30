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
	    
	    @NotNull   
	    private int showorder; 
	    
	    private int dayofweek;

	    
	    private boolean actived;
	  
	 

}
