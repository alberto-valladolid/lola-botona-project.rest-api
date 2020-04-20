package com.lolabotona.restapi.model;

import java.io.Serializable;
import java.util.Objects;




public class UserGroupKey implements Serializable{

	/**
	 * No se usa, la borraré en un futuro
	 */
	private static final long serialVersionUID = 1L;
	

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
