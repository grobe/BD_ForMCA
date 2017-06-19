package service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.CollectionBD;
import models.ScraperBot;
import models.ScraperResults;
import play.libs.ws.WSClient;

public class FnacCrawler {
	@Inject 
	WSClient ws;
	
	
	/*
	 * Controller which invoke a promise to crawl all the URL from the Fnac web site
	 */
	public  FnacCrawler() {
		//play.Logger.debug("FnacCrawler : FnacCrawler1 : :"+ new Date());
		 CompletionStage<String> promiseOfcrawler = CompletableFuture.supplyAsync(() -> crawler());
		 
		 promiseOfcrawler.thenApply(
	     reponse->{
	    	 play.Logger.debug(this.getClass().getName()+": FnacCrawler :done promiseOfcrawler"+ promiseOfcrawler.toString() +" "+ new Date());
	    	   return "done promiseOfcrawler";
	          } );
		 
    	
    }
	
	/*
	 * Private function to launch by promise all the URL from the object collection.crowlerBots which contains all the URLS
	 * to be used in order to create the list of all the Comics for all our collections
	 * each call by WS is a single promise 
	 */
	private String crawler(){
		
		//CompletableFuture<String> future = new CompletableFuture<>();
		 //Test test;
		//List <String> urlTab = new ArrayList<>();
		//Look for all the collection 
		List <CollectionBD> collection =  CollectionBD.find.all();
		
		play.Logger.debug(this.getClass().getName()+": crawler1 : : collection.size() :" +collection.size() + new Date());
		
		collection.stream().forEach((col)->{
		            
			play.Logger.debug(this.getClass().getName()+": crawler1.5 : : col.size() :" +col.scraperBots.size()+ new Date());
			 col.scraperBots.stream().forEach((bot)->{		
			        
				    //Look for the URL to be used by the bot to search into the store FNAC
				    String url  =bot.url;
				   
			        CompletionStage<String> retour = ws.url(url).get().thenApply(
			  	           response ->{
			                  // play.Logger.debug("FnacCrawler : crawler2 :thenApply :"+url+" "+ new Date());
			                   
			                   
			                   return (response.getBody());
			                }
			         ).whenComplete((value, exception) -> {
			                 //play.Logger.debug("FnacCrawler : crawler3 : whenComplete :"+url+" "+ new Date());
			                 if (exception != null) {
			              	   play.Logger.error(this.getClass().getName()+": crawler4 : ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exception:"+exception.getMessage()+ "---"+url+"--"+ new Date()); 
			                   //future.completeExceptionally(exception);
			                 }
			                 else {	              	  
			              	  	 setAllCrowlerResult(value, bot);
			              	  }
			             });
			 });        
		});
		
		return "done";
	}
	
	/*
	 * Private function used to parse the HTML response for each URL in order to create or update the model  CrowlerResults
	 */
	private void setAllCrowlerResult (String html, ScraperBot bot) {
		
		
     	   //In the store Fnac the comic is always in a tag <li class="Article-item"> 
     	   //I'm looking for a comic from the list of all the comics from the search 
     	   //Elements listBD = doc.select("div.Article-itemGroup");
     	   Elements listBD = FnacExtractData.getItemsList(html);
     	   
     	   
     	   
     	   play.Logger.debug(this.getClass().getName()+": setAllCrowlerResult :listBD.size()     ________________________________ :"+" listBD.size() :"+ listBD.size()); 
     	   listBD.forEach(bd -> {
     		                  
     		                  setCrowlerItemResult (bd, bot.collection);
     		                         
     		       
     	   });
		
	}
	
	
	
	/*
	 * Private function used to parse each item of the parsed HTML document in order to update or create the 
	 * model  CrowlerResults list
	 */
	private void setCrowlerItemResult (Element bdItem,CollectionBD collection) {
		ScraperResults result;
		
		
		  
    
          //i'm looking for something like :"Trolls de Troy - Tome 5 : Les maléfices de la Thaumaturge"
          //the number of the comic should be always between " Tome" and ":"
          //bdTitle.matches(".*\\bTome\\b.*")&&  (StringUtils.countMatches(bdTitle, "Tome")==1) &&(StringUtils.countMatches(bdTitle, ":")==1)
          if (FnacExtractData.isItemArticleValidated(bdItem)==true) {
       	    	         	    	    
        	        try{
        	        	//STEP1 : i'm looking for the title and the number of the comic in the collection	 
        	            
        	  		  String bdGlobalTitle = FnacExtractData.getTitle(bdItem);
        	            play.Logger.debug(this.getClass().getName()+": setCrowlerItemResult1 :_____MCA___________________________ : bdTitle "+ bdGlobalTitle+ " --- "+ new Date()); 
        	           
        	        	
        	        	// take the number between" tome" and ":" and remove all the non numeric characters -
        	            String number = FnacExtractData.getNumber(bdItem);
		 
		                   result = ScraperResults.find.where().eq("collection", collection).eq("number", number).findUnique();
		                    
		                   //if new comics i create the row
		                   if (result ==null){
		                	   result= new ScraperResults();
		                	    play.Logger.debug("crowlerResult :New"); 
		                	    result.setCollection(collection);
		                	    result.setNumber(number);
		                	    result.setCreationDate(new Date());
		                	    result.save();
		                  
		                   }
		                  
		                   //i update the data of the comics in case of update on the web site.
		                   result.setTitle(FnacExtractData.getTitleCleaned(bdItem));
		                 
		                 
		                  //I look for the picture of the book
		                   
		                   result.setImageBase64(FnacExtractData.getImageBAse64(bdItem));
		                   
		                  //step2 :i'm looking for the author and the script writer  
		              	 // Map<String, String> authorAndScriptWriter = FnacExtractData.getAuthorScriptWriter(bdItem);
		              	  result.setDesigner(FnacExtractData.getAuthor(bdItem));
		         		  result.setScenario(FnacExtractData.getScriptWriter(bdItem));
		                  
		                   //step3 :i'm looking for the price
		         		  double price= FnacExtractData.getPrice(bdItem);
		                  result.setPrice(price);
		                  
		                 //step4 :i'm looking for the availability of the BD
		                  
		                  String availability =FnacExtractData.getAvailability(bdItem);
		                  result.setAvailability(availability);
		               
		                  result.update();
		                  play.Logger.debug(this.getClass().getName()+": crowlerResult.save()================================ :"+result.getTitle()+"- date ="+new Date()); 
        	        }catch (Exception e){
        	        	play.Logger.error(this.getClass().getName()+": setCrowlerItemResult error++++++++++++++++++++++++++++++++ Exception:"+e.getMessage()); 
        	        }
        	        
          
          
          }else{
   	    	 play.Logger.debug(this.getClass().getName()+": setCrowlerItemResult4  :_________________ : Line NOT selected : " + FnacExtractData.getTitle(bdItem)) ;    
   	       }
       	    	 
             

                 
		
	}


	
	
	
}
