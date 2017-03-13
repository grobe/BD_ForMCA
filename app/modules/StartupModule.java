package modules;

import com.google.inject.AbstractModule;
import service.SchedulingStartUp;
import actors.ActorsWebService;
import play.libs.akka.AkkaGuiceSupport;

public class StartupModule extends AbstractModule implements AkkaGuiceSupport {

	@Override
	protected void configure() {
		bindActor(ActorsWebService.class, "update-db-crawler");
        bind(SchedulingStartUp.class).asEagerSingleton(); 
        
        play.Logger.debug("StartupModule : configure");
		
	}

}
