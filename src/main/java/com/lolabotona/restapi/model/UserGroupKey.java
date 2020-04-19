package com.lolabotona.restapi.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Size;


public class UserGroupKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	

	
	
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) 
            return false;

        UserGroupKey that = (UserGroupKey) o;
        return Objects.equals(id, that.id) ;
    }

    @Override   
    public int hashCode() {
        return Objects.hash( id);
    }
    
    

    @Override
    public String toString() {
        return "ProfesormoduloId [id=" + id + "]";
    }
    
	

}
