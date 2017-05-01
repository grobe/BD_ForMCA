package actors;

import java.util.Date;

import javax.inject.Inject;

import service.FnacCrawler;
import play.api.Play;


public class ActorsWebServiceProtocol {

	 public static class SayHello {
	        public final String name;
	        
	    	@Inject 
  	        FnacCrawler crawler;

	        public SayHello(String name) {
	            
	        
	        	
	        	//FnacCrawler serviceFnacCrawler =crawler;
	        	Play.current().injector().instanceOf(FnacCrawler.class);
	        	
	        	
	        	
	        	this.name = name+ new Date();
	        	
	    	    play.Logger.debug("ActorsWebServiceProtocol : SayHello :"+ name);

	        }
	    }
	
	
}
