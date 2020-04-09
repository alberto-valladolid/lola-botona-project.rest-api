package com.lolabotona.restapi.model;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;


public class UserGroupKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long user;
	private Long group;
    @Size(min = 6, max = 40)
	private String type;
	

	public Long getUserId() {
		return user;
	}
	
	public void setUserId(Long id) {
		this.user = id;
	}
	
	public Long getGroupId() {
		return group;
	}
	
	public void setGroupId(Long id) {
		this.group = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) 
            return false;

        UserGroupKey that = (UserGroupKey) o;
        return Objects.equals(user, that.user) && 
               Objects.equals(group, that.group)&& 
               Objects.equals(type, that.type);
    }

    @Override   
    public int hashCode() {
        return Objects.hash(user, group);
    }
    
    

    @Override
    public String toString() {
        return "ProfesormoduloId [userid=" + user + ", groupid=" + group + ", type="+ type +"]";
    }
    
	

}
