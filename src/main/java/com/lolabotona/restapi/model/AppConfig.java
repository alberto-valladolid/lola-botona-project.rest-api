package com.lolabotona.restapi.model;


import java.sql.Time;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(	name = "app_config")
public class AppConfig {
	
	@Id
	private Long id; 
	
	private int eventMinutes; // minutos restantes para bloquear la anulación de clases
	
	private int eventMinutesToAllow; // minutos restantes para permitir la anulación de clases
	
	private int absenceDays;  //días para recuperar una asuencia

	
	public AppConfig() {};
	
	public AppConfig(long id, int eventMinutes,int absenceDays) {
		this.id = id;
		this.eventMinutes = eventMinutes; 
		this.absenceDays = absenceDays; 
		
	};	
	

	@Override
	public String toString() {
		return "AppConfig [id=" + id + ", eventMinutes=" + eventMinutes +  ", absenceDays=" + absenceDays + ", eventMinutesToAllow=" + eventMinutesToAllow + "]";
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;
        AppConfig that = (AppConfig) o;
        return Objects.equals(id, that.id)  ;
    }
	
}
