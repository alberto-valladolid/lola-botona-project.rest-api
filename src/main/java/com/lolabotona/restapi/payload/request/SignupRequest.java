package com.lolabotona.restapi.payload.request;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
 
    @NotBlank
    @Size(max = 50)
    private String role;
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    @NotBlank
    private String type;
    
    private int[] teachers; 
    

}