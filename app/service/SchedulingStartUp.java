package service;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.inject.name.Named;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.*;
import scala.concurrent.duration.Duration;

public class SchedulingStartUp  {

		@Inject
	    public SchedulingStartUp(final ActorSystem system,
	                          @Named("update-db-crawler") ActorRef updateDbActor) {
			
			
			play.Logger.debug("SchedulingStartUp");
	        system.scheduler().schedule(
	            Duration.create(30, TimeUnit.SECONDS), //Initial delay
	            Duration.create(1, TimeUnit.MINUTES),     //Frequency
	            updateDbActor,
	            "update",
	            system.dispatcher(),
	            null);
	    }	

}