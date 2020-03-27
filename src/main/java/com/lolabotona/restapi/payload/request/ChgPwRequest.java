package com.lolabotona.restapi.payload.request;

import javax.validation.constraints.*;

public class ChgPwRequest {
	
    @NotBlank
    @Size(min = 0, max = 40)
    private String currentPassword;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
  
    
  
    public String getCurrentPassword() {
        return currentPassword;
    }
 
    public void setCurrentPassword(String password) {
        this.currentPassword = password;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
 
    public void setNewPassword(String password) {
        this.newPassword = password;
    }


}
