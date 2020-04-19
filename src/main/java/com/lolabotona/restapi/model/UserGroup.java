package com.lolabotona.restapi.model;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
//import javax.persistence.Table;
import javax.validation.constraints.NotBlank;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@IdClass(UserGroupKey.class)
public class UserGroup implements Serializable {
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@JsonBackReference(value="user")
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;

	@JsonBackReference(value="group")
    @ManyToOne
    @JoinColumn(name = "groupid", referencedColumnName = "id")
    private Group group;
  
    @Column(length = 20)
    private String type; //recurrent, absence or  retrieve 
	
	
	

//	@JsonBackReference(value="user")
//	@Id
//    @ManyToOne
//    @JoinColumn(name = "userid", referencedColumnName = "id")
//    private User user;
//
//	@JsonBackReference(value="group")
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "groupid", referencedColumnName = "id")
//    private Group group;
//	@Id
//    @NotBlank    
//    @Column(length = 20)
//    private String type; //recurrent, absence or  retrieve 
    
    @Column(name="retrieved", columnDefinition="tinyint(1) default 0",nullable = false)
    private boolean retrieved ; //only for  retrieve
    
    private Timestamp dateat; // for absence or retrieve 
    
    
    public UserGroup(User user, Group group,String type,boolean retrieved,Timestamp dateat) {
    	
        this.user = user;
        this.group = group;
        this.type = type;
        this.retrieved = retrieved;
        this.dateat = dateat;
        
    }
    
	public long getuserid() {	
		return user.getId();
	}

	public long getgroupid() {
		return group.getId();
	}
	
	   
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        UserGroup that = (UserGroup) o;
        return Objects.equals(id, that.id)  ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    
    @Override
    public String toString() {
    	return "UserGroup [id=" + id + ",user=" + user + ", group=" + group + ", type=" + type + ", retrieved=" + retrieved + " , dateat=" + dateat + "]";
    }


	
	

	
    

}
