package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import models.Test;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

public class FnacCrawler {
	@Inject 
	WSClient ws;
	
	
	public  FnacCrawler() {
		play.Logger.debug("FnacCrawler : FnacCrawler1 : :"+ new Date());
		 CompletionStage<String> promiseOfcrawler = CompletableFuture.supplyAsync(() -> crawler());
		 
		 promiseOfcrawler.thenApply(
	     reponse->{
	    	 play.Logger.debug("FnacCrawler : FnacCrawler222222222222222222222222222 : :"+ promiseOfcrawler.toString() +" "+ new Date());
	    	   return "done promiseOfcrawler";
	          } );
		 
    	
    }
	public String crawler(){
		
		CompletableFuture<String> future = new CompletableFuture<>();
		
		List <String> urlTab = new ArrayList<>();
		
		urlTab.add("http://www.fnac.com/Trolls-de-Troy/si245");
		urlTab.add("http://bd.grobe.fr");
		urlTab.add("https://news.grobe.fr");
		play.Logger.debug("FnacCrawler : crawler1 : : urlTab" + new Date());
		for (String  url: urlTab){
		
					CompletionStage<String> retour = ws.url(url).get().thenApply(
			  	           response ->{
			                   play.Logger.debug("FnacCrawler : crawler2 : :"+url+" "+ new Date());
			                   
			                   
			                   return (response.getBody());
			                }
			         ).whenComplete((value, exception) -> {
			            play.Logger.debug("FnacCrawler : crawler3 : :"+url+" "+ new Date());
			                 if (exception != null) {
			              	   play.Logger.debug("FnacCrawler : crawler4 : :"+url+" "+ new Date()); 
			                   future.completeExceptionally(exception);
			                 }
			                 else {
			              	   play.Logger.debug("FnacCrawler : crawler5 : :"+url+" "+ new Date());
			              	   play.Logger.debug("FnacCrawler : crawler6 : :"+url+" "+ value.substring(0, 100));
			              	   
			              	   Test test = new Test();
			              	   test.data=value.substring(0, 100);
			              	   test.save();
			              	   
			              	   
			              	   play.Logger.debug("FnacCrawler : crawler7 : :"+url+" "+  new Date());
			              	  future.complete(value);
			                 }
			             });
		}
		
		return "done";
	}
	
}
