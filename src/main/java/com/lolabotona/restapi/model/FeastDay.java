package com.lolabotona.restapi.model;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(	name = "feast_day", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = {"date"})
})
public class FeastDay {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	private Date date;
	
	public FeastDay() {
	}

	public FeastDay(Date date) {
		this.date = date;
	

	}
	
	
	@Override
	public String toString() {
		return "FeastDay [id=" + id + ", date=" + date + "]";
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        FeastDay that = (FeastDay) o;
        return Objects.equals(id, that.id)  ;
    }
	
}
