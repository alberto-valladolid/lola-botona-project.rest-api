package com.lolabotona.restapi.payload.request;


import javax.validation.constraints.NotNull;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChgAppConfig {
	
    @NotNull  
    private int absenceDays;
    
    @NotNull
    private int eventMinutes;
    
    @NotNull
    private int eventMinutesToAllow;
    
}
