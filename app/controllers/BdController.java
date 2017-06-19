package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import models.BdData;
import models.BdDisplay;
import models.CollectionBD;
import models.CollectionDisplay;
import models.ScraperResults;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import service.FnacExtractData;
import service.FnacScanBD;

@Singleton
public class BdController extends Controller {

	
	@Inject
	FnacScanBD scanFromFnac;
	
	@Inject 
	WSClient ws;
	
	@Inject
	FormFactory formFactory;
	
	//controller to display the scanTest page
	//used to test the use of the webcam into a web page
	public Result  scanTest(){
		play.Logger.debug("scan : MCA is HEre : scanTest");
		return ok(views.html.scanTest.render());
				} 
	
	
	public Result  scan(){
		play.Logger.debug("scan : MCA is HEre : scan");
		return ok(views.html.scan.render());
				} 
	
	
	
	
	
	//Action done for Create or Update a Scanned BD
	public Result  scannedBD(){
		play.Logger.debug("______________________________________________scannedBD___________________________________ ");
		
		Form<CollectionBD> collectionForm = formFactory.form(CollectionBD.class);
		
		CollectionBD formCollection = collectionForm.bindFromRequest().get();
		/*
		    DynamicForm requestData = formFactory.form().bindFromRequest();
		    String editor = requestData.get("editor");
		    String collection = requestData.get("collection");
		    String title = requestData.get("title");
		    String isbn = requestData.get("isbn");
		    String number = requestData.get("number");
		    String price = requestData.get("price");
		   */
		    
		    
			//i check if the collection extracted from the web store alredy exist or not
			//in order to know if i have to create it
		    CollectionBD bdCollection= CollectionBD.find.where().eq("title", formCollection.title).findUnique();
					
			if (bdCollection==null){
				play.Logger.debug("New scannedBD collection____New:"+formCollection.title);
				bdCollection = formCollection;
				bdCollection.save();
			}else{
				play.Logger.debug("Existing scannedBD collection____New:"+formCollection.title);
				bdCollection.setEditor(formCollection.editor);
				bdCollection.setTitle(formCollection.title);
				bdCollection.update();
				
				// if you change the collection that mean's you have to update all the book of the collection to be link to the new connection.
			}
			play.Logger.debug("BD info__Before");
			play.Logger.debug("BD info________:"+formCollection.bddata.get(0).isbn+"++++--");
			play.Logger.debug("BD info__After");
			
			BdData bdInfo =BdData.find.where().eq("isbn", formCollection.bddata.get(0).isbn).findUnique();
			if (bdInfo==null){
				
				
				bdInfo =formCollection.bddata.get(0);
				bdInfo.collection=bdCollection;
				
				bdInfo.save();
				play.Logger.debug("New scannedBD BD____New");
				//TODO
			}else{
				play.Logger.debug("existing bdinfo"+bdInfo.title);
				play.Logger.debug("form collectionBD.bddata.get(0)"+formCollection.bddata.get(0).title);
				// idon't know why but .update is not working with 
				//bdInfo =collectionBD.bddata.get(0);
				//only with setters as below :
				bdInfo.setCollection(bdCollection);
				bdInfo.setTitle(formCollection.bddata.get(0).getTitle());
				bdInfo.setCreationDate(formCollection.bddata.get(0).getCreationDate());
				bdInfo.setDesigner(formCollection.bddata.get(0).getDesigner());
				bdInfo.setImageBase64(formCollection.bddata.get(0).getImageBase64());
				bdInfo.setIsbn(formCollection.bddata.get(0).getIsbn());
				bdInfo.setNumber(formCollection.bddata.get(0).getNumber());
				bdInfo.setPrice(formCollection.bddata.get(0).getPrice());
				bdInfo.setScenario(formCollection.bddata.get(0).getScenario());
				
				
				bdInfo.update();
				play.Logger.debug("existing scannedBD BD____Updated");
			}
		    
		    play.Logger.debug("editor ="+bdCollection.editor +" & collection= " +bdCollection.title + " & BD title = "+bdInfo.title);
		
		return ok("C'est OK");
	}
	
	
	//controller to display the scan page , bddata.number asc
		public Result  listBD(){
			play.Logger.debug("scan : MCA is HEre : ListBD");
			
			 List <CollectionBD> resultCollections = CollectionBD.find.where().orderBy("title asc").findList();
			  
			 
			 // i catch all the collection content to be displayed in to the Web page
			 List<CollectionDisplay> myBD = resultCollections.stream()
					                   .map(rc->{
					                	         CollectionDisplay tempRc = new CollectionDisplay();	                	        
					                	         //i catch all the BD from each collection to be displayed into the web page
					                	         //List<BdDisplay>  bdDisplay2 = new ArrayList<BdDisplay> ();	                	         
					                	         tempRc.setEditor(rc.editor);
					                	         tempRc.setTitle(rc.title);
					                	         tempRc.setFollowOnWebstore(rc.getFollowingOnWebstores());
					                	         tempRc.setBdDisplay(rc.getBdDisplay());
					                	        return tempRc;
					                	   
					                   })
					                   .collect(Collectors.toList()
					                		   
					                  );  
					        
			 
		
			 
			 //.stream().distinct().collect(Collectors.toList()) )
			 //return ok( Json.toJson(result));
			 return ok(  views.html.listBD.render(myBD)); 	
		 } 
	
	//controller to display the data extract from the code bar scanned
	//and based on the ISBN search the data on the website Fnac
	
	  public CompletionStage<Result>   infoBD(){
		
		  play.Logger.debug("infoBD : MCA is HEre");
		  //String IsbnCode="No code";
		 
		  File file;
		 
		  MultipartFormData<File> body = request().body().asMultipartFormData();
		  FilePart<File> picture = body.getFile("picture");
		    if (picture != null) {
		        String fileName = picture.getFilename();
		        String contentType = picture.getContentType();
		        file = picture.getFile();
		        
		    } else {
		        flash("error", "Missing file");
		        return    (CompletionStage<Result>) badRequest("bad request");
		    }
		
		    play.Logger.debug("before scanFromFnac.Scan");
		    final String isbnCode =scanFromFnac.scan(file);
		    play.Logger.debug(" after scanFromFnac.Scan");
		    String url ="http://recherche.fnac.com/SearchResult/ResultList.aspx?Search="+isbnCode;
		  
		  play.Logger.debug("BdController :  listBD :IsbnCode="+isbnCode+ " - size of the file :"+file.length());
		  
		  
		
		  
		  //return scanFromFnac.extractDataFromSearch(IsbnCode);
		  
		  return ws.url(url).get().thenApply(
	  	           response ->{
	  	        	 CollectionBD bdInfo;
		                  // play.Logger.debug("FnacCrawler : crawler2 :thenApply :"+url+" "+ new Date());
		  	        	 play.Logger.debug("infoBD 1 + isbnCode="+isbnCode);
		  	        	//play.Logger.debug("response.getBody() : "+response.getBody());
		  	        	if (!isbnCode.isEmpty()){
		  	        	    /*Here we check that the IsbnCode is not empty
		  	        	     * in order to save only validated IsbnCode
		  	        	     */
		  	        		
		  	        		bdInfo = scanFromFnac.extractDataFromSearch( response.getBody(),isbnCode) ;
		  	        		
		  	        		
		  	        	    //i' check if the Bd extracted is already existing on my DB
		  	        		boolean bdExist =scanFromFnac.bdExist(isbnCode);
			  	        	//if (bdExist==false) bdInfo.save();
			  	        	play.Logger.debug("infoBD 2 + bdExist ="+bdExist);
			  	        	return ok(views.html.bdInfo.render(bdInfo,bdExist));
		  	        	}else{
		  	        		return ok("Barcode not recognized !! ");
		  	        	}
		  	        	    
		  	        	 
		                
		        
		             });
		  
	  }
	  
	  public Result   infoBDFromWebCam(){
		  
		  String isbnCode ="";
		  
		  
		  return ok(isbnCode);
	  }
	  

}                

			 
		  

