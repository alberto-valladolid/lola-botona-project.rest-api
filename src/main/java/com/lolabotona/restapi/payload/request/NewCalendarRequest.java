package com.lolabotona.restapi.payload.request;

import java.sql.Timestamp;


import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NewCalendarRequest {

    @NotNull  
    private Timestamp date; 
    
    @NotNull  
    private Long groupid;

}
