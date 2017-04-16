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
import models.CrowlerBot;
import models.CrowlerResults;
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
	    	 play.Logger.debug("FnacCrawler : FnacCrawler :done promiseOfcrawler"+ promiseOfcrawler.toString() +" "+ new Date());
	    	   return "done promiseOfcrawler";
	          } );
		 
    	
    }
	
	/*
	 * Private function to launch by promise all the URL from the object collection.crowlerBots which contains all the URLS
	 * to be used in order to create the list of all the Comics for all our collections
	 * each call by WS is a single promise 
	 */
	private String crawler(){
		
		CompletableFuture<String> future = new CompletableFuture<>();
		 //Test test;
		//List <String> urlTab = new ArrayList<>();
		//Look for all the collection 
		List <CollectionBD> collection =  CollectionBD.find.all();
		
		play.Logger.debug("FnacCrawler : crawler1 : : collection.size() :" +collection.size() + new Date());
		
		collection.stream().forEach((col)->{
		            
			play.Logger.debug("FnacCrawler : crawler1.5 : : col.size() :" +col.crowlerBots.size()+ new Date());
			 col.crowlerBots.stream().forEach((bot)->{		
			        
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
			              	   play.Logger.error("FnacCrawler : crawler4 : ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exception:"+exception.getMessage()+ "---"+url+"--"+ new Date()); 
			                   future.completeExceptionally(exception);
			                 }
			                 else {
			              	   //play.Logger.debug("FnacCrawler : crawler5 *****************************: NOt exception :"+url+" "+ new Date());
			              	  // play.Logger.debug("FnacCrawler : crawler6 : :"+url+" "+ value.substring(0, 100));
			              	   
			              	  
			                	 setAllCrowlerResult(value, bot);
			              	   
			              	   //play.Logger.debug("FnacCrawler : crawler12 *****************************:end of NOt exception  :"+url+" "+  new Date());
			              	  future.complete(value);
			                 }
			             });
			 });        
		});
		
		return "done";
	}
	
	/*
	 * Private function used to parse the HTML response for each URL in order to create or update the model  CrowlerResults
	 */
	private void setAllCrowlerResult (String html, CrowlerBot bot) {
		
		 Document doc = Jsoup.parse(html);
    	   
      	 
     	  // play.Logger.debug("FnacCrawler : crawler6.5 : :"+url+"  doc.data():"+   doc.data());

     	   
     	   //In the store Fnac the comic is always in a tag <li class="Article-item"> 
     	   //I'm looking for a comic from the list of all the comics from the search 
     	   //Elements listBD = doc.select("div.Article-itemGroup");
     	   Elements listBD = doc.select("li[class*=Article-item]"); //ul.articleList
     	   
     	   
     	   
     	   play.Logger.debug("FnacCrawler : setAllCrowlerResult :listBD.size()     ________________________________ :"+" listBD.size() :"+ listBD.size()); 
     	   listBD.forEach(bd -> {
     		                  
     		                  setCrowlerItemResult (bd, bot.collection);
     		                         
     		       
     	   });
		
	}
	
	
	
	/*
	 * Private function used to parse each item of the parsed HTML document in order to update or create the 
	 * model  CrowlerResults list
	 */
	private void setCrowlerItemResult (Element bdItem,CollectionBD collection) {
		CrowlerResults result;
		 Document article = Jsoup.parseBodyFragment(bdItem.outerHtml());
		
		 //STEP1 : i'm looking for the title and the number of the comic in the collection	 
          Element bdTitle = article.select("a").first();
          play.Logger.debug("FnacCrawler : setCrowlerItemResult1 :_____MCA___________________________ : bdTitle "+ bdTitle.text()+ " --- "+ new Date()); 
          
    
          //i'm looking for something like :"Trolls de Troy - Tome 5 : Les maléfices de la Thaumaturge"
          //the number of the comic should be always between " Tome" and ":"
          if (bdTitle.text().matches(".*\\bTome\\b.*")&&  (StringUtils.countMatches(bdTitle.text(), "Tome")==1) &&(StringUtils.countMatches(bdTitle.text(), ":")==1)) {
       	    	   
       	    	    
        	        try{
        	        	
        	        	// take the number between" tome" and ":" and remove all the non numeric characters -
        	            int number = Integer.parseInt(((bdTitle.text().split("Tome"))[1].split(":"))[0].replaceAll("[^\\d.]", ""));
		 
		                   result = CrowlerResults.find.where().eq("collection", collection).eq("number", number).findUnique();
		                    
		                   //if new comics i create the row
		                   if (result ==null){
		                	   result= new CrowlerResults();
		                	    play.Logger.debug("crowlerResult :New"); 
		                	    result.setCollection(collection);
		                	    result.setNumber(number);
		                	    result.setCreationDate(new Date());
		                	    result.save();
		                  
		                   }
		                  
		                   //i update the data of the comics in case of update on the web site.
		                   result.setTitle(bdTitle.text());
		                 
		                 
		                  //step2 :i'm looking for the author and the script writer  
		              	  Map<String, String> authorAndScriptWriter = getAuthorScriptWriter(bdItem);
		              	  result.setDesigner(authorAndScriptWriter.get("author"));
		         		  result.setScenario(authorAndScriptWriter.get("ScriptWriter"));
		                  
		                   //step3 :i'm looking for the price
		         		  double price= getPrice(bdItem);
		                  result.setPrice(price);
		                  
		                 //step4 :i'm looking for the price
		                  
		                  String availability =getAvailability(bdItem);
		                  result.setAvailability(availability);
		               
		                  result.update();
		                  play.Logger.debug("crowlerResult : crowlerResult.save()================================ :"+bdTitle.text()+"- date ="+new Date()); 
        	        }catch (Exception e){
        	        	play.Logger.error("FnacCrawler : setCrowlerItemResult error++++++++++++++++++++++++++++++++ Exception:"+e.getMessage()); 
        	        }
        	        
          
          
          }else{
   	    	 play.Logger.debug("FnacCrawler : setCrowlerItemResult4  :_________________ : Line NOT selected : " + bdTitle.text()) ;    
   	       }
       	    	 
             

                 
		
	}

	private String getAvailability(Element bdItem) {
		Element bdAvailability = bdItem.select("span[class=Dispo-txt]").first();
        String availability="No information";
        
        if (bdAvailability !=null){
     	  
        	availability = bdAvailability.text();
     	   play.Logger.debug("FnacCrawler : getAvailability :_____MCA___________________________ :"+"bdAvailability : "+ bdAvailability.text());
                      	    
        }
		return availability;
	}

	//Private function to look for the price of the comic into the HTML fragment bdItem
	private double getPrice(Element bdItem) throws Exception {
		 Element bdPrice = bdItem.select("strong[class=userPrice]").first();
         double price=0;
         
         if (bdPrice !=null){
      	   DecimalFormat df = new DecimalFormat(); 
      	   DecimalFormatSymbols sfs = new DecimalFormatSymbols();
      	   sfs.setDecimalSeparator('€'); 
      	   df.setDecimalFormatSymbols(sfs);
      	   price = df.parse(bdPrice.text()).doubleValue();
      	   play.Logger.debug("FnacCrawler : getPrice :_____MCA___________________________ :"+"bdPrice : "+ bdPrice.text());
                       	    
         }
		return price;
	}
     
	//Private function to look for the designer and the scenario  into the HTML fragment bdItem
	private Map<String, String> getAuthorScriptWriter(Element bdItem) {
		 Elements bdAuthorScriptWriter = bdItem.select("p[class=Article-descSub]");
         
         play.Logger.debug("FnacCrawler : getAuthorScriptWriter :_____MCA___________________________ :"+"bdAuthorScriptWriter.size"+ bdAuthorScriptWriter.size()); 
         
         
         
         Map<String, String> authorAndScriptWriter = new HashMap<>();
         
         String author="No author";
    	 String ScriptWriter="No ScriptWriter";
          //an author exist ?
         if  (bdAuthorScriptWriter.size() >0){
      	  
      	   //where is the author and the script writer ?
      	   //not always in the same order....
      	   String [] authorOrScriptWriter= bdAuthorScriptWriter.get(0).text().split(",");
      	   if (authorOrScriptWriter.length ==1){
      		   author=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      		   ScriptWriter=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      	   }
      	   
      	   else if (authorOrScriptWriter[0].contains( "(Dessinateur)")||authorOrScriptWriter[0].contains( "(Auteur)")){
      		   author=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      		   ScriptWriter=authorOrScriptWriter[1].split(Pattern.quote("("))[0];
      	   }else{
      		   author=authorOrScriptWriter[1].split(Pattern.quote("("))[0];
      		   ScriptWriter=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      	   }
      	   play.Logger.debug("FnacCrawler : getAuthorScriptWriter2:_ bdAuthorScriptWriter.get(0).text()_"+   bdAuthorScriptWriter.get(0).text());
      	    	 
         }
         authorAndScriptWriter.put("author",author);
         authorAndScriptWriter.put("ScriptWriter",ScriptWriter);
         
         
		return authorAndScriptWriter;
	}
	
	
	
	
	
	
}
