package service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.BdData;
import models.CollectionBD;
import models.ScraperResults;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;

public class FnacScanBD  implements ScanBD {
	
	@Inject 
	WSClient ws;
	public FnacScanBD() {
		
	}

	public  BdData extractDataFromSearch( String html, String isbn){
		play.Logger.debug("extractData 1");
		
			play.Logger.debug("extractData 2");
			BdData bdInfo =new BdData();
			CollectionBD bdCollection;
			
			play.Logger.debug("extractData 3");
			               
			/*i get the result from the HTML
			 * the HTML should content only
			 * 1 values
			 * 
			 */
			Elements listBD = FnacExtractData.getItemsList(html);  
			
			play.Logger.debug("extractData 3.5 length= "+listBD.size());
			
			//I check if the answer from the Fnac webStore return a validated ITem based on the ISBN code 
			if ((listBD.size()>0)){
				
				//i check if the collection extracted from the web store alredy exist or not
				//in order to know if i have to create it
				bdCollection= CollectionBD.find.where().eq("title", FnacExtractData.getCollection(listBD.get(0))).findUnique();
						//il faut chercher l'édituer --> classe "editorialInfo"
				        //
				if (bdCollection==null){
					play.Logger.debug("New ____extractData collection____New");
					bdCollection = new CollectionBD();
					bdCollection.setTitle(FnacExtractData.getCollection(listBD.get(0)));
					bdCollection.setEditor(FnacExtractData.getEditor(listBD.get(0)));
					bdCollection.save();
					
				}
						
				bdInfo.setCollection(bdCollection);
				play.Logger.debug("extractData collection");
		    	
				bdInfo.setIsbn(isbn);
				play.Logger.debug("extractData isbn-"+isbn);
		    	bdInfo.setCreationDate(new Date());
		    	play.Logger.debug("extractData creationDate"+bdInfo.creationDate);
		    	bdInfo.setTitle(FnacExtractData.getTitleCleaned(listBD.get(0)));
		    	play.Logger.debug("extractData 7");
		    	bdInfo.setNumber(FnacExtractData.getNumber(listBD.get(0)));
		    	play.Logger.debug("extractData 8");
		    	bdInfo.setDesigner(FnacExtractData.getAuthor(listBD.get(0)));
		    	play.Logger.debug("extractData 9");
		    	bdInfo.scenario=FnacExtractData.getScriptWriter(listBD.get(0));
		    	play.Logger.debug("extractData 10");
		    	try {
					bdInfo.price=FnacExtractData.getPrice(listBD.get(0));
					play.Logger.debug("extractData 11");
				} catch (Exception e) {
					play.Logger.error(this.getClass().getName()+"\n : "+e.getMessage());
				}
		    	play.Logger.debug("extractData 12");
			}
	    	
			 play.Logger.debug("extractData 13");
		return   bdInfo;
		

	}
	
	
	
	
	
	
}
