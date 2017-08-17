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
import models.LoginForm;
//import models.LoginForm;
import models.Owners;
//import models.Owners;
import models.ScraperResults;
import models.StatisticsBD;
import play.Configuration;
import play.Logger;
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
//import views.html.login;

@Singleton
public class BdController extends Controller {

	
	@Inject
	FnacScanBD scanFromFnac;
	
	@Inject 
	WSClient ws;
	
	@Inject
	FormFactory formFactory;
	
	@Inject
	Configuration configuration;
	
	@Inject
	StatisticsBD myStat;
	
	//controller to display the scanTest page
	//used to test the use of the webcam into a web page
	public Result  scanTest(){
		play.Logger.debug("scan : MCA is HEre : scanTest");
		return ok(views.html.scanTest.render());
				} 
	
	
	@Security.Authenticated(Secured.class)
	public Result  scan(){
		play.Logger.debug("scan : MCA is HEre : scan");
		Logger.debug("BdController : scan : session(\"testMCA\") = "+session("testMCA"));
		Logger.debug("BdController : scan : session(\"connectedBD\") = "+session("connectedBD"));
		return ok(views.html.scan.render());
				} 
	
	
	public Result searchCollection(String term,String userFilter) {
    	//look for the list of distinct list of line managers
    	
    	response().setHeader(CACHE_CONTROL, "no-cache");
    	play.Logger.debug(" searchCollection term=" + term);
    	//List <CollectionBD> collection = CollectionBD.find.where().setDistinct(true).select("title").where().icontains("title", "").orderBy("title").findList();
    	
    	Owners owner =Owners.find.where().eq("login", userFilter).findUnique();
    	List <CollectionBD> collection = CollectionBD.find.where().eq("owner",owner).setDistinct(true).where().icontains("title", term).findList();
		play.Logger.debug(" searchCollection size:" + collection.size());
	
		
		List<String> listOfCollection = collection.stream()
				.filter((p)->{
					         play.Logger.debug(" getBddata size:" + p.getBddata().size());
					         play.Logger.debug(" getBdDisplay size:" + p.getBdDisplay().size());
				             return (p.getBddata().size()==0||p.getBdDisplay().size()==0);})
				.map(o->o.getTitle())
				.collect(Collectors.toList());
		
		return ok(Json.toJson(listOfCollection),"utf-8");
		
	}
	
	
	
	
	public Result login() {
		
	
		response().setHeader(CACHE_CONTROL, "no-cache");
	
		Logger.debug("BdController : login : session(\"connectedBD\")" + session("connectedBD"));
	
		return ok(views.html.login.render("scan"));
		
	}
	
	
	//action to manage the security
	
	public Result security() {

		play.Logger.debug("______________________________________________security called___________________________________ ");
		
		Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
		
		// here i check if my form is well filled
		//if not go back the login form
		Logger.debug("BdController :security : before loginForm.hasError  ");
		if (loginForm.hasErrors()) {
			Logger.debug("BdController :security : loginForm.hasError  ");
			session().clear();
			flash("Security", "Please fill correctly the form");
			Logger.debug("BdController :security : loginForm.errors().size():"+loginForm.errors().size());
		    return redirect(controllers.routes.BdController.login());
		} 
		Logger.debug("BdController :security: after loginForm.hasError  ");
		LoginForm userLogin = loginForm.bindFromRequest().get();
		//session("testMCA","testMCA is here !!!!!!!!!!!!!!");
		//check if the owner exist --> match between login and password
		Owners owner = Owners.find.where().eq("login",userLogin.getLogin()).eq("password", userLogin.getPassword()).findUnique();

		

		if (owner != null)  {
			// the user is authenticated
			
			Logger.debug("BdController : security :the user is authenticated :owner.getId() "+owner.getId());
			session("connectedBD", String.valueOf(owner.getId()));
            
			
			
			/*
			 * TODO define a way to dispatch more or less dynamically where the login page has to send
			 * based on callBackURL parameter from the login form
			 */

			switch (userLogin.getCallBackURL()) {
            case "scan":
            	return ok(views.html.scan.render());
        
           
            default:
            	redirect(controllers.routes.BdController.listBD(userLogin.getLogin()));
        }
			
			
			//return ok(views.html.scan.render());
			//redirect("/scan");
		}
		Logger.debug("BdController : security : :the user is not authenticated ");
		session().clear();
		flash("Security", "You are not authenticated !");
		
		
	
		return redirect(controllers.routes.BdController.login());
		

	}
	
	
	//Action do to add a BD from the list from the WebStore
	//
	public Result  addBD(String id){
		
		String nextSection="No_NExt";
		
		ScraperResults bdscraper =ScraperResults.find.where().eq("id", String.valueOf(id)).findUnique();
		BdData bdInfo = new BdData();
		
		bdInfo.setCollection(bdscraper.getCollection());
		bdInfo.setCreationDate(new Date());
		bdInfo.setDesigner(bdscraper.getDesigner());
		bdInfo.setImageBase64(bdscraper.getImageBase64());
		bdInfo.setIsbn("No isbn"+new Date());
		bdInfo.setNumber(bdscraper.getNumber());
		bdInfo.setPrice(bdscraper.getPrice());
		bdInfo.setScenario(bdscraper.getScenario());
		bdInfo.setTitle(bdscraper.getTitle());
		bdInfo.save();

		//The purpose is to Know the id of the next volume of the BD in order to display it to the user into the web page 
		//
		//TODO --> to be updated : not look for the next volume but the first volume searched on the collection.
		//
		
		//I'mm looking for the next volume to be added on the BDlist screen
		ScraperResults bdscraperNext =ScraperResults.find.where().eq("collection_id", bdscraper.getCollection().id).eq("number",String.valueOf(Double.valueOf(bdscraper.getNumber()).intValue()+1)).findUnique();
		//I'm checking if the next volume is not already on my own collection  
		BdData bdExistOnBdData =BdData.find.where().eq("collection_id", bdscraper.getCollection().id).eq("number",String.valueOf(Double.valueOf(bdscraper.getNumber()).intValue()+1)).findUnique();
		
		play.Logger.debug("addBD : the next volume is : " + String.valueOf(Double.valueOf(bdscraper.getNumber()).intValue()+1));
		
		//if the following item exist in ScrapperResult and not into BdData i will display the next item to be added on the screen
		//if not i will display the collection title
		//meaning the user will be sent to listBD page on the top of the collection he has added a volume
		//
		if (bdscraperNext!=null && bdExistOnBdData==null){
			nextSection=String.valueOf(bdscraperNext.getId());
		}else {
			nextSection="collection-"+bdscraper.getCollection().getId();
		}
			
		return redirect("/" + "#"+nextSection);
	}
	
	
	//Action done for Create or Update a Scanned BD
	@Security.Authenticated(Secured.class)
	public Result  scannedBD(){
		response().setHeader(CACHE_CONTROL, "no-cache");
		play.Logger.debug("______________________________________________scannedBD___________________________________ ");
		String resultToBeDisplayed;
		
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
		    
		    Owners owner =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
		    CollectionBD bdCollection= CollectionBD.find.where().eq("owner",owner).eq("title", formCollection.title).findUnique();
			
		    formCollection.setOwner(owner);
		    
			if (bdCollection==null){
				play.Logger.debug("BdController : scannedBD : New scannedBD collection____New:"+formCollection.title);
				play.Logger.debug("BdController : scannedBD : New scannedBD owner:"+owner.login);
				bdCollection = formCollection;
				//bdCollection.setOwner(owner);
				bdCollection.save();
				
			}else{
				play.Logger.debug("BdController : scannedBD : Existing scannedBD collection____New:"+formCollection.title);
				bdCollection.setEditor(formCollection.editor);
				bdCollection.setTitle(formCollection.title);
				bdCollection.update();
				
				
				// if you change the collection that mean's you have to update all the book of the collection to be link to the new connection.
			}
			play.Logger.debug("BdController : scannedBD : BD info__Before");
			play.Logger.debug("BdController : scannedBD : BD info________:"+formCollection.bddata.get(0).isbn+"++++--");
			play.Logger.debug("BdController : scannedBD : BD info__After");
			
			
			/* A test has to be done here to know if i have to use isbn to look for the book
			 * or the collection + title or number
			 * because when a book is added from Webstore list there has no ISBN code
			 */
			BdData bdInfo =BdData.find.where().eq("collection", bdCollection).eq("number", formCollection.bddata.get(0).number).findUnique();
			if (bdInfo==null){
				bdInfo =BdData.find.where().eq("isbn", formCollection.bddata.get(0).isbn).findUnique();
			}
			
			if (bdInfo==null){//the book doesn't exist no ISBN code existe and no same collection & same number 
				
				
				bdInfo =formCollection.bddata.get(0);
				bdInfo.collection=bdCollection;
				
				bdInfo.save();
				play.Logger.debug("BdController : scannedBD : New scannedBD BD____New");
				resultToBeDisplayed ="Created";
				//TODO
			}else{ //the book exists and i will update it with the new value
				play.Logger.debug("BdController : scannedBD : existing bdinfo"+bdInfo.title);
				play.Logger.debug("BdController : scannedBD : form collectionBD.bddata.get(0)"+formCollection.bddata.get(0).title);
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
				play.Logger.debug("BdController : scannedBD : existing scannedBD BD____Updated");
				resultToBeDisplayed ="Updated";
			}
		    
		    play.Logger.debug("BdController : scannedBD : editor ="+bdCollection.editor +" & collection= " +bdCollection.title + " & BD title = "+bdInfo.title);
		
		return ok(views.html.scannedBD.render(resultToBeDisplayed));
	}
	
	
	//controller to display the scan page , bddata.number asc
	//by default login ="grobe" defined in routes file.
		public Result  listBD(String login){
			play.Logger.debug("scan : MCA is HEre : ListBD");
			
		
			play.Logger.debug("BdController -listBD session(\"connectedBD\")"+session("connectedBD"));
			
			Owners owner =Owners.find.where().eq("login", login).findUnique();
			
			 List <CollectionBD> resultCollections = CollectionBD.find.where().eq("owner",owner).orderBy("title asc").findList();
			  
			 
			 // i catch all the collection content to be displayed in to the Web page
			 List<CollectionDisplay> myBD = resultCollections.stream()
					                   .map(rc->{
					                	         CollectionDisplay tempRc = new CollectionDisplay();	                	        
					                	         //i catch all the BD from each collection to be displayed into the web page
					                	         //List<BdDisplay>  bdDisplay2 = new ArrayList<BdDisplay> ();	                	         
					                	         tempRc.setId(String.valueOf(rc.id));//created to be used with BdController.addBD in order to display at least the collection to the user
					                	         tempRc.setEditor(rc.editor);
					                	         tempRc.setTitle(rc.title);
					                	         tempRc.setFollowOnWebstore(rc.getFollowingOnWebstores());
					                	         tempRc.setBdDisplay(rc.getBdDisplay());
					                	        return tempRc;
					                	   
					                   })//test
					                   .collect(Collectors.toList()
					                		   
					                  );  
					        
			 //StatisticsBD myStat =new StatisticsBD();
			 myStat.setStatisticsByLogin(login);
			 
			 //.stream().distinct().collect(Collectors.toList()) )
			 //return ok( Json.toJson(result));
			 return ok(  views.html.listBD.render(myBD, myStat,login)); 	
		 } 
	
	//controller to display the data extract from the code bar scanned
	//and based on the ISBN searches the data on the website Fnac
	
	  public CompletionStage<Result>   infoBD(){
		  
		  play.Logger.debug("infoBD : MCA is HEre");
		  Logger.debug("BdController : infoBD : session(\"testMCA\") = "+session("testMCA"));
		  
		  //String IsbnCode="No code";
		 
		  File file;
		 
		  MultipartFormData<File> body = request().body().asMultipartFormData();
		  FilePart<File> picture = body.getFile("picture");
		    if (picture != null) {
		        //String fileName = picture.getFilename();
		        //String contentType = picture.getContentType();
		        file = picture.getFile();
		        
		    } else {
		        flash("error", "Missing file");
		        return    (CompletionStage<Result>) badRequest("bad request");
		    }
		
		    play.Logger.debug("before scanFromFnac.Scan");
		    final String isbnCode =scanFromFnac.scan(file);
		    play.Logger.debug(" after scanFromFnac.Scan");
		   
		    
		    String url =configuration.getString("webStore.fnac.searchUrl") +isbnCode;
		  
		    play.Logger.debug("BdController :  URL =  " +url);
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

			 
		  

