package service;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	
	private static final int ArrayList = 0;
	@Inject 
	WSClient ws;
	public FnacScanBD() {
		
	}

	public  CollectionBD extractDataFromSearch( String html, String isbn){
		play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 1");
		
			play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 2");
			BdData bdInfo =new BdData();
			CollectionBD bdCollection;
			
			play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 3");
			               
			/*i get the result from the HTML
			 * the HTML should content only
			 * 1 values
			 * 
			 */
			Elements listBD = FnacExtractData.getItemsList(html);  
			
			play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 3.5 length= "+listBD.size());
			
			//I check if the answer from the Fnac webStore return a validated ITem based on the ISBN code 
			if ((listBD.size()>0)){
				
				//i check if the collection extracted from the web store already exist or not
				//in order to know if i have to create it
				bdCollection= CollectionBD.find.where().eq("title", FnacExtractData.getCollection(listBD.get(0))).findUnique();
				
				//Todo raise an error  if BD collection has more than one result
				
				if (bdCollection==null){
					play.Logger.debug("FnacScanBD : extractDataFromSearch : New ____extractData collection____New");
					bdCollection = new CollectionBD();
					bdCollection.setTitle(FnacExtractData.getCollection(listBD.get(0)));
					bdCollection.setEditor(FnacExtractData.getEditor(listBD.get(0)));
					//bdCollection.save();
					
				}
						
				bdInfo.setCollection(bdCollection);
				play.Logger.debug("FnacScanBD : extractDataFromSearch : FnacScanBD : extractDataFromSearch : extractData collection");
		    	
				bdInfo.setIsbn(isbn);
				play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData isbn-"+isbn);
		    	
			
				
				bdInfo.setCreationDate(new Date());
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData creationDate"+bdInfo.creationDate);
		    	
		    	bdInfo.setImageBase64(FnacExtractData.getImageBAse64(listBD.get(0)));
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData creationDate"+bdInfo.creationDate);
		    	
		    	bdInfo.setTitle(FnacExtractData.getTitleCleaned(listBD.get(0)));
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 7");
		    	bdInfo.setNumber(FnacExtractData.getNumber(listBD.get(0)));
		    	
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 8 :FnacExtractData.getNumber(listBD.get(0)=" + FnacExtractData.getNumber(listBD.get(0)));
		    	bdInfo.setDesigner(FnacExtractData.getAuthor(listBD.get(0)));
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 9");
		    	bdInfo.scenario=FnacExtractData.getScriptWriter(listBD.get(0));
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 10");
		    	try {
					bdInfo.price=FnacExtractData.getPrice(listBD.get(0));
					play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 11");
				} catch (Exception e) {
					play.Logger.error(this.getClass().getName()+"\n : "+e.getMessage());
				}
		    	play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 12");
			}else{
				//put here the setup of zn bdingo empty with only an isbn code
				bdCollection= CollectionBD.find.where().eq("title", "Not Found").findUnique();
				
					if (bdCollection==null){
						play.Logger.debug("FnacScanBD : extractDataFromSearch : New ____extractData collection____New");
						bdCollection = new CollectionBD();
						bdCollection.setTitle("Not Found");
						bdCollection.setEditor("Not Found");
						//bdCollection.save();
					}
					bdInfo.setCollection(bdCollection);
					play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData not bd found from Web store");
			    	
					bdInfo.setIsbn(isbn);
			}
	    	
			List <BdData> bddata= new ArrayList <BdData>();
			bddata.add(bdInfo);
			bdCollection.setBddata(bddata); 
			play.Logger.debug("FnacScanBD : extractDataFromSearch : extractData 13");
			 
		return   bdCollection;
		

	}
	
	
	
	
	
	
}
