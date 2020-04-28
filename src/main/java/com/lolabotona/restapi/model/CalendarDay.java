package com.lolabotona.restapi.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarDay {


	private int monthDay; 	
	private int weekDay; 
	private boolean currentMonth; 
//    private Set<CalendarEvent> events;
	private ArrayList<CalendarEvent> events;
	 
	private boolean isFeastDay; 
	
	
	
	
	public CalendarDay(int monthDay,boolean currentMonth,  int weekDay,boolean isFeastDay,ArrayList<CalendarEvent> events) {
		this.monthDay = monthDay;
		this.weekDay = weekDay; 
		this.currentMonth = currentMonth; 
		this.events = events; 	
		this.isFeastDay = isFeastDay; 
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
