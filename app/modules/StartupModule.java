package modules;

import javax.inject.Inject;

import com.google.inject.AbstractModule;

import service.*;
import actors.ActorsWebService;
import models.StatisticsBD;
import play.Configuration;
import play.Environment;
import play.libs.akka.AkkaGuiceSupport;

public class StartupModule extends AbstractModule implements AkkaGuiceSupport {
	
	 private final Environment environment;
	 private final Configuration configuration;
	 
	 @Inject
	    public StartupModule(Environment environment, Configuration configuration) {
	        this.environment = environment;
	        this.configuration = configuration;
	    }
	 

	@Override
	protected void configure() {
		bindActor(ActorsWebService.class, "update-db-crawler");
        bind(SchedulingStartUp.class).asEagerSingleton(); 
        bind(FnacScanBD.class);
		bind(DecitreScanBD.class);
        bind(StatisticsBD.class);
        bind(FnacCrawler.class); //not working don't understand why to see later
        
        play.Logger.debug("StartupModule : configure");
		
	}

}
