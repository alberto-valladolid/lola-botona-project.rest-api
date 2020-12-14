package com.lolabotona.restapi.model;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity

public class UserTeacher implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@JsonBackReference(value="user2")
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;

	@JsonBackReference(value="user3")
    @ManyToOne
    @JoinColumn(name = "teacherid", referencedColumnName = "id")
    private User teacher;
	   
    public UserTeacher(User user, User teacher) {
    	
        this.user = user;
        this.teacher = teacher;
        
    }  	    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTeacher)) return false;
        UserTeacher that = (UserTeacher) o;
        return Objects.equals(id, that.id)  ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }    
    
	public long getUserId() {	
		return user.getId();
	}

	public long getTeacherId() {
		return teacher.getId();
	}
	

	
	public String getTeacherName() {	
		return teacher.getName();
	}

	public String getTeacherUsername() {
		return teacher.getUsername();
	}
    
    @Override
    public String toString() {
    	return "UserGroup [id=" + id + ",user=" + user + ", teacher=" + teacher + "]";
    }
    

}
