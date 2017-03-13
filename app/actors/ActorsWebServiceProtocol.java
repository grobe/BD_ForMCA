package actors;

import java.util.Date;

import service.FnacCrawler;
import play.api.Play;


public class ActorsWebServiceProtocol {

	 public static class SayHello {
	        public final String name;

	        public SayHello(String name) {
	            
	        	
	        	
	        	//FnacCrawler serviceFnacCrawler =
	        	Play.current().injector().instanceOf(FnacCrawler.class);
	        	
	        	
	        	
	        	this.name = name+ new Date();
	        	
	    	    play.Logger.debug("ActorsWebServiceProtocol : SayHello :"+ name);

	        }
	    }
	
	
}
