package com.lolabotona.restapi.model;

import java.time.YearMonth;
import java.util.Calendar;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarDay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; 
	private int day; 	
	private boolean currentMonth; 
	private CalendarEvent firstEvent; 
	private CalendarEvent secondEvent; 
	
	
	public CalendarDay(int day,boolean currentMonth, CalendarEvent firstEvent, CalendarEvent secondEvent) {
		this.day = day; 
		this.currentMonth = currentMonth; 
		this.firstEvent = firstEvent; 
		this.secondEvent = secondEvent; 
	}
	
	
	public static int calcPreMonthExtraDays(Calendar c){
		
	      //Calculate last previus month days when current month doesnt start on Monday. 

		  int dayOfWeek1thMonth,preMonthExtraDays;
			
	      c.set(Calendar.DAY_OF_MONTH, 1);	   
	      
	      dayOfWeek1thMonth = c.get(Calendar.DAY_OF_WEEK);	  
	      
	      if(dayOfWeek1thMonth == 1)
	    	  preMonthExtraDays = 6;
	      else
	    	  preMonthExtraDays = dayOfWeek1thMonth - 2;
	      
	      return preMonthExtraDays; 
	
	}
	
	public static int calcPostMonthExtraDays(Calendar c){
		
		  //Calculate first days of next month when current month doesnt end on Sunday
		  int currMonthDays,lastDayOfCurrMonth,postMonthExtraDays;		 
		  
		  YearMonth yearMonthObject = YearMonth.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);	      
		  
		  currMonthDays = yearMonthObject.lengthOfMonth();
		  
		  c.set(Calendar.DAY_OF_MONTH, currMonthDays);
		  
		  lastDayOfCurrMonth = c.get(Calendar.DAY_OF_WEEK);	  
		  

		  
		  if(lastDayOfCurrMonth == 1)
			  postMonthExtraDays = 0;
		  else
			  postMonthExtraDays = 8 - lastDayOfCurrMonth;
		  
		  return postMonthExtraDays; 
	
	}
	

//	@Override
//	public String toString() {
//		return "CalendarDay [id=" + id + ", capacity=" + capacity + ", description=" + description + /*", orderShown=" + orderShown +*/ " , timeofday=" + timeofday +" , dayofweek=" + dayofweek +" , active=" + active + "]";
//	}
	
	
}
