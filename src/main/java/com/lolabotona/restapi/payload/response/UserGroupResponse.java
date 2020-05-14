package com.lolabotona.restapi.payload.response;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupResponse {
	
	
	private int userId; 
	private String userName; 
	private String userRealName; 
	private int groupId;
	private String groupDescription; 
	private String type;
}
