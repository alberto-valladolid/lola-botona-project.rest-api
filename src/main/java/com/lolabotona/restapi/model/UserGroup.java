package com.lolabotona.restapi.model;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@IdClass(UserGroupKey.class)
public class UserGroup implements Serializable {
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonBackReference
	@Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

	@JsonBackReference
    @Id
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;
    
    
 
    @NotBlank    
    private String type; //recurrent, absencse or  retrieve 
    
    @Column(name="retrieved", columnDefinition="tinyint(1) default 1")
    private boolean retrieved ; //only for  retrieve
    
    private Timestamp dateAt; // for absencse or retrieve 
    
    
    public UserGroup(User user, Group group) {
        this.user = user;
        this.group = group;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        UserGroup that = (UserGroup) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(group, that.group) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getName(), group.getId(), type);
    }


    
    

}
