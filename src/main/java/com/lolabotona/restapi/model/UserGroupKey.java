package com.lolabotona.restapi.model;

import java.io.Serializable;
import java.util.Objects;


public class UserGroupKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long user;
	private Long group;
	

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
	
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) 
            return false;

        UserGroupKey that = (UserGroupKey) o;
        return Objects.equals(user, that.user) && 
               Objects.equals(group, that.group);
    }

    @Override   
    public int hashCode() {
        return Objects.hash(user, group);
    }
    
    

    @Override
    public String toString() {
        return "ProfesormoduloId [userid=" + user + ", groupid=" + group + "]";
    }
    
	

}
