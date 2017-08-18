package actors;

import java.util.Date;

import javax.inject.Inject;

import service.FnacCrawler;
import play.api.Play;


public class ActorsWebServiceProtocol {

	 public static class SayHello {
		    
		 //@Inject
		 //FnacCrawler crawler;  //inject is not working here maybe because of the static class
	     public final String name;
	        
	    	

	        public SayHello(String name ) {
	            
	        
	        	 play.Logger.debug("ActorsWebServiceProtocol : SayHello begining  :"+ name  );
	        	//FnacCrawler serviceFnacCrawler =crawler;
	        	FnacCrawler crawler =Play.current().injector().instanceOf(FnacCrawler.class);
	        	
	        	crawler.launchFnacCrawler();
	        	
	        	this.name = name+ new Date();
	        	
	    	    play.Logger.debug("ActorsWebServiceProtocol : SayHello end :"+ name  );

	        }
	    }
	
	
}
