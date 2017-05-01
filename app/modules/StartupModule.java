package modules;

import javax.inject.Inject;

import com.google.inject.AbstractModule;

import service.FnacCrawler;
import service.FnacScanBD;
import service.ScanBD;
import service.SchedulingStartUp;
import actors.ActorsWebService;
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
        bind(ScanBD.class).to(FnacScanBD.class);
        
        play.Logger.debug("StartupModule : configure");
		
	}

}
