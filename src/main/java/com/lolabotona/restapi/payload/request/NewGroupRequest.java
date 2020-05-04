package com.lolabotona.restapi.payload.request;

import javax.validation.constraints.*;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NewGroupRequest {
	
		@NotNull  
	    private int capacity;
		
	    @NotBlank    
	    private String description; 
	    
	    @NotNull   
	    private int showorder; 
	    @NotNull  
	    private int dayofweek;

	    
	    private boolean active;
	    
	    @NotNull   
	    @Min(value = 0, message = "startTimeHours must be >= 0")
	    @Max(value = 23, message = "startTimeHours must be <= 23")
	    private int startTimeHours; 
	    
	    @NotNull  
	    @Min(value = 0, message = "startTimeMins must be >= 0")
	    @Max(value = 59, message = "startTimeMins must be <= 59")
	    private int startTimeMins; 
	  
	 

}
