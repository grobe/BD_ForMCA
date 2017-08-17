package controllers;


/*
 * This class is not usefull for my project
 * It was useful at the beginning when i choice my technical stack 
 * and i did some test on future & asynchrone stuff 
 */
import javax.inject.Inject;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class FuturController extends Controller {
	@Inject 
	WSClient ws;
	
 
    public CompletionStage<Result>  webService(){
    	
    	
    	
    	//CompletableFuture<Result> future = new CompletableFuture<>();
    	play.Logger.debug("webService : response1 :"+ new Date());
    	
    	return ws.url("http://www.fnac.com/Trolls-de-Troy/si245").get()
    			.thenApply(
    					response ->{
    						        play.Logger.debug("webService : response2  :"+ new Date());
    						        
    						        
    						        return ok(views.html.bd.render(response.getBody().replaceAll("(?i)trolls", "Miguel_Casado webService : response3: "+new Date())));
    							   }
    					).whenComplete((value, exception) -> {
    						 play.Logger.debug("webService : response4  :"+ new Date());
    			            if (exception != null) {
    			            	 play.Logger.debug("webService : response5  :"+ new Date());
    			            	//future.completeExceptionally(exception);
    			            }
    			            else { play.Logger.debug("webService : response6 :"+ new Date());
    			            	//future.complete(value);
    			            }
    			        });
   
    	 
    	
    	
    }
    
    public Result  webService2(){
    	   
    	 play.Logger.debug("webService2 : response0 :"+ new Date());
    	 
    	 CompletionStage<Double> promiseOfPIValue = CompletableFuture.supplyAsync(() -> computePIAsynchronously());
    	
    	
    	
    	   CompletableFuture<String> future = new CompletableFuture<>();
    	   
    	   play.Logger.debug("webService2 : response1 :"+ new Date());
    	   
    	   CompletionStage<String> request = ws.url("http://bd.grobe.fr/").get()
    	       .thenApply(
    	           response ->{
    	                     play.Logger.debug("webService2 : response2 : :"+ new Date());
    	                     
    	                     
    	                     return (response.getBody());
    	                  }
    	           ).whenComplete((value, exception) -> {
    	              play.Logger.debug("webService2 : response3 : :"+ new Date());
    	                   if (exception != null) {
    	                	 play.Logger.debug("webService2 : response4 : :"+ new Date()); 
    	                     future.completeExceptionally(exception);
    	                   }
    	                   else {
    	                	   play.Logger.debug("webService2 : response5 : :"+ new Date());
    	                	  future.complete(value);
    	                   }
    	               });
    	     
    	    play.Logger.debug("webService2 : response6 :"+ new Date());
    	   
    	   return  ok("ok fini : webService2 : response7 :"+new Date());
    	 }
    
    private Double computePIAsynchronously() {
    	play.Logger.debug("computePIAsynchronously : debut :"+ new Date());
    	Double sum= new Double(0);
    	long SomeNumber=25000000000L;
    	for(double i=0; i<SomeNumber; i++)
    	{//play.Logger.debug("computePIAsynchronously : dans boucle :"+ new Date());
    	    if(i%2 == 0) // if the remainder of `i/2` is 0
    	        sum += -1 / ( 2 * i - 1);
    	    else
    	        sum += 1 / (2 * i - 1);
    	}
    	play.Logger.debug("computePIAsynchronously : fin :"+sum+" "+ new Date());
		return sum;
	}

	

}
