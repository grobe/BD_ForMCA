package service;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.inject.name.Named;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.*;
import scala.concurrent.duration.Duration;

public class SchedulingStartUp  {
	   
	  Configuration configuration;

		@Inject
	    public SchedulingStartUp(Configuration configuration, final ActorSystem system,
	                          @Named("update-db-crawler") ActorRef updateDbActor) {
			
			int initialDelay=Integer.parseInt(configuration.getString("SchedulingStartUp.InitialDelay"));
			int frequency = Integer.parseInt(configuration.getString("SchedulingStartUp.Frequency"));
			
			
			play.Logger.debug("SchedulingStartUp");
	        system.scheduler().schedule(
	            //Duration.create(30, TimeUnit.SECONDS), //Initial delay
	            //Duration.create(1, TimeUnit.MINUTES),     //Frequency

	        		
	        		
	        	Duration.create(initialDelay, TimeUnit.SECONDS), //Initial delay
		        Duration.create(frequency, TimeUnit.MINUTES),     //Frequency
		            	
	            updateDbActor,
	            "update",
	            system.dispatcher(),
	            null);
	    }	

}