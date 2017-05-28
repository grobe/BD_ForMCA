package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

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
import models.CollectionBD;
import models.ScraperResults;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import service.FnacScanBD;

@Singleton
public class BdController extends Controller {

	
	@Inject
	FnacScanBD scanFromFnac;
	
	@Inject 
	WSClient ws;
	
	//controller to display the scan page
	public Result  scan(){
		play.Logger.debug("scan : MCA is HEre : scan");
		return ok(views.html.scan.render());
				} 
	
	
	//controller to display the scan page
		public Result  listBD(){
			play.Logger.debug("scan : MCA is HEre : ListBD");
			
			 List <CollectionBD> result = CollectionBD.find.all();
			
			return ok( Json.toJson(result));
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
	  	        	 BdData bdInfo;
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
			  	        	if (bdExist==false) bdInfo.save();
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

			 
		  

