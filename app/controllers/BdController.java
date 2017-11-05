package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import models.BdPivot;
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
import play.twirl.api.Content;
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
	
	
	// private function used by some actions to get the list of CollectionDisplay from one user
		private List<CollectionDisplay>  askForCollectionsToBeDisplayed(String login){
			Owners owner;
			
			 /*
			  * Done : Override login if session("connectedBD exist") to use it instead of the value of the input parameter of this action  
			  */
			if (session("connectedBD")!=null)
				{ owner =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
				  login=owner.getLogin();
				}else {
					owner =Owners.find.where().eq("login", login).findUnique();
				}
	         List <CollectionBD> resultCollections =(List<CollectionBD>) owner.getCollectionBD().stream()
	        		                                                       .sorted((o1, o2)->o1.getTitle().compareToIgnoreCase(o2.getTitle())
	        		                                                               )
	        		                                                       .collect(Collectors.toList());
			 
	         
	         
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
				 return (myBD); 
			}
	
	
	
	
	
	//controller to display the scanTest page
	//used to test the use of the webcam into a web page
	public Result  scanTest(){
		play.Logger.debug(this.getClass().getName()+"scanTest : MCA is HEre : scanTest");
		return ok(views.html.scanTest.render());
				} 
	
	
	@Security.Authenticated(Secured.class)
	public Result  scan(){
		play.Logger.debug("scan : MCA is HEre : scan");
		
		Logger.debug(this.getClass().getName()+" scan : session(\"connectedBD\") = "+session("connectedBD"));
		return ok(views.html.scan.render());
				} 
	
	
	public Result searchCollection(String term,String userFilter) {
    	//look for the list of distinct list of line managers
    	
    	response().setHeader(CACHE_CONTROL, "no-cache");
    	//play.Logger.debug(" searchCollection term=" + term);
        Owners owner =Owners.find.where().eq("login", userFilter).findUnique();
    	List <CollectionBD> collection =owner.getCollectionBD();
    	
    	play.Logger.debug(" searchCollection size:" + collection.size());
	
		
		List<String> listOfCollection = collection.stream()
				.filter((p)->{
					         //play.Logger.debug(" getBddata size:" + p.getBddata().size() +"p.title"+p.title);
					         //play.Logger.debug(" getBdDisplay size:" + p.getBdDisplay().size()+"p.title"+p.title);
					         //play.Logger.debug(" getBdDisplay term:" + term +"(p.getTitle().indexOf(term) ="+(p.getTitle().indexOf(term)));
					         //play.Logger.debug("p.getTitle().indexOf(term) >-1 &&( (p.getBddata().size()>0||p.getBdDisplay().size()>0):" + (p.getTitle().indexOf(term) >-1 &&(  p.getBddata().size()>0||p.getBdDisplay().size()>0)));
				             return (p.getTitle().toLowerCase().indexOf(term.toLowerCase()) >-1 &&(  p.getBddata().size()>0||p.getBdDisplay().size()>0));})
				.map(o->o.getTitle())
				.sorted()
				.collect(Collectors.toList());
		//listOfCollection=listOfCollection.stream().sorted();
		
		
		return ok(Json.toJson(listOfCollection),"utf-8");
		
	}
	
	
	public Result logout(String callBackURL) {
		
		
		Logger.debug(this.getClass().getName()+" - logout");
		session().clear();
		
		return  redirect("/");
		
	}
	
	public Result login(String callBackURL) {
		
	
		response().setHeader(CACHE_CONTROL, "no-cache");
	
		Logger.debug(this.getClass().getName()+" login : session(\"connectedBD\")" + session("connectedBD"));
		Logger.debug(this.getClass().getName()+" login : callBackURL" + callBackURL);
		
		return ok(views.html.login.render(callBackURL));
		
	}
	
	
	//action to manage the security
	
	public Result security() {

		play.Logger.debug("______________________________________________security called___________________________________ ");
		
		Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
		
		// here i check if my form is well filled
		//if not go back the login form
		Logger.debug(this.getClass().getName()+"security : before loginForm.hasError  ");
		if (loginForm.hasErrors()) {
			Logger.debug(this.getClass().getName()+"security : loginForm.hasError  ");
			session().clear();
			flash("Security", "Please fill correctly the form");
			Logger.debug(this.getClass().getName()+"security : loginForm.errors().size():"+loginForm.errors().size());
			Logger.debug(this.getClass().getName()+"security :loginForm.data().get(\"callBackURL\")1:"+loginForm.data().get("callBackURL"));
			return redirect(controllers.routes.BdController.login(loginForm.data().get("callBackURL")));
		} 
		Logger.debug(this.getClass().getName()+"security: after loginForm.hasError  ");
		
		Logger.debug(this.getClass().getName()+"security: loginForm.data().get(login)="+loginForm.data().get("password"));
		
		Logger.debug(this.getClass().getName()+"security: loginForm.data().get(password)="+loginForm.data().get("password"));
		
		LoginForm userLogin = loginForm.bindFromRequest().get();
		//session("testMCA","testMCA is here !!!!!!!!!!!!!!");
		//check if the owner exist --> match between login and password
		
		Logger.debug(this.getClass().getName()+"security: userLogin.getLogin()="+userLogin.getLogin());
		
		Logger.debug(this.getClass().getName()+"security: userLogin.getPassword()="+userLogin.getPassword());
		
		Owners owner = Owners.find.where().eq("login",userLogin.getLogin()).eq("password", userLogin.getPassword()).findUnique();

		

		if (owner != null)  {
			/*i'm looking for urls like /scan or /addBD/523 
			 *first value between / & / is Object to be instanciate
			 *the others values between the others / & / are parameter
			 *meaning
			*/
			String callBAckURL =userLogin.getCallBackURL().replaceFirst("/", "").split("\\?")[0];
			/*
			 * First value of the tab objectParameters = Object to instantiate
			 * others values are parameters 
			 */
			String []objectParameters =callBAckURL.split("/");
			
			// the user is authenticated
			Logger.debug(this.getClass().getName()+" security :the user is authenticated :callBAckURL "+objectParameters[0]);
			Logger.debug(this.getClass().getName()+" security :the user is authenticated :owner.getId() "+owner.getId());
			session("connectedBD", String.valueOf(owner.getId()));
            
			
			
			/*
			 * Below : define a way to dispatch more or less dynamically where the login page has to send
			 * based on callBackURL parameter from the login form
			 */
			
			try {
				Logger.debug(this.getClass().getName()+" security :1");
				Class<?>  myViewTobeDisplayed = Class.forName("controllers.BdController"); //."+objectParameters[0]+"()");
				Object obj =myViewTobeDisplayed.newInstance();
				
				Logger.debug(this.getClass().getName()+" security :2 : objectParameters[0]=" +objectParameters[0]); 
			
				int numParams = objectParameters.length;//methode.getParameterCount();
				Logger.debug(this.getClass().getName()+" security :3.6: objectParameters.length ="+numParams);
				Result result=null;
				
				if (numParams ==1) {
					Logger.debug(this.getClass().getName()+" security :3.7.0: if ==0"); 
					//result = (Content) method.invoke(null);
					Method methode = obj.getClass().getMethod(objectParameters[0]);
					result= (Result)methode.invoke(myViewTobeDisplayed.newInstance());
					//call = controllers.routes.BdController.scan();
					Logger.debug(this.getClass().getName()+" security :3.7.1: after if invoke");
				}else {
					Logger.debug(this.getClass().getName()+" security :3.7.2: else");
						String[] paramString = new String[numParams];
						
						for (int i = 1; i< paramString.length;i++) {
							Logger.debug(this.getClass().getName()+" security :3.7.3: in the for : objectParameters[i]="+ objectParameters[i]);
							paramString[i] = objectParameters[i];
						}
						Logger.debug(this.getClass().getName()+" security :3.7.4: before else invoke");
						//result = (Content) method.invoke(paramString);
						Method methode = obj.getClass().getMethod(objectParameters[0],String.class);
						result= (Result)methode.invoke(myViewTobeDisplayed.newInstance(),new String(objectParameters[1]));
						Logger.debug(this.getClass().getName()+" security :3.7.5: after else invoke");
					}
					
				
				
				
				Logger.debug(this.getClass().getName()+" security :4");
				return result;
				//return redirect((Call)call);
			} catch (NoSuchMethodException 
					| SecurityException 
					| IllegalAccessException 
					| IllegalArgumentException 
					| InvocationTargetException | ClassNotFoundException | InstantiationException e) {
				// TODO Auto-generated catch block
				Logger.error(this.getClass().getName()+" security : Class.forName "+ e.getMessage() +"\n cause = "+e.getCause());
				//return redirect(controllers.routes.BdController.listBD(userLogin.getLogin()));
			}
			

		}
		Logger.debug(this.getClass().getName()+" security : :the user is not authenticated ");
		session().clear();
		flash("Security", "You are not authenticated !");
		
		
		Logger.debug(this.getClass().getName()+"security : loginForm.field(callBackURL)2:"+ loginForm.field("callBackURL"));
		Logger.debug(this.getClass().getName()+"security : userLogin.getCallBackURL()2:"+ userLogin.getCallBackURL());
		
		return redirect(controllers.routes.BdController.login(loginForm.data().get("callBackURL")));
		//return badRequest(views.html.login.render(loginForm.data().get("callBackURL")));

	}
	
	
	//Action do to add a BD from the list from the WebStore
	@Security.Authenticated(Secured.class)
	public Result  addBD(String id){
		
		String nextSection="No_NExt";
        boolean fromModalLoginCalled = false;  
		
		/*
		 * if id contains an id with "_NotConnected" : e.g :"453_NotConnected"
		 * that means i come from the login modal 
		 * and i need to close it through dedicated view & javascript
		 * if not that means i can make my redirect normally : e.g :"453"
		 */
        play.Logger.debug(this.getClass().getName()+" addBD : id before indexOf(_): " + id);
        if (id.indexOf("_") !=-1) {
        	play.Logger.debug(this.getClass().getName()+" addBD : id.indexOf(\"_\") ==-1 : true");
        	id=id.split("_")[0];
			fromModalLoginCalled=true;
		}
		play.Logger.debug(this.getClass().getName()+" addBD : id after indexOf(_): " + id);
		play.Logger.debug(this.getClass().getName()+" addBD :  fromModalLoginCalled : " + fromModalLoginCalled);
		play.Logger.debug(this.getClass().getName()+" addBD :  Long.valueOf(id) : " + String.valueOf(Long.valueOf(id)));
		
		
		//i check if the collection extracted from the web store (data into the form) already exist or not
		//in order to know if i have to create it
	    
	   
		ScraperResults bdscraper =ScraperResults.find.where().eq("id", String.valueOf(id)).findUnique();
		
		/*
		 * Now i check if the ID from ScraperResults come from the connected user
		 */
		 Owners owner =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
		 
		// owner.getCollectionBD().get(0).getBdDisplay().stream()
		 
		 CollectionBD bdCollection=owner.getCollectionBD().stream()
		    		.filter(e ->{ 
		    			          play.Logger.debug(this.getClass().getName()+" addBD : in filter : e.id ="+e.id);
		    			          play.Logger.debug(this.getClass().getName()+" addBD : in filter : bdscraper.getCollection().id ="+bdscraper.getCollection().id);
		    			          play.Logger.debug(this.getClass().getName()+" addBD : in filter : (e.id==bdscraper.getCollection().id) ="+(e.id==bdscraper.getCollection().id));
		    			          // i have to use longValue to compare the value, otherwise  the comparison is not working
		    			          return(e.id.longValue()==bdscraper.getCollection().id.longValue());
		    		              }) //-->
		            .findFirst()
		            .orElse(null);  
		 
		
		
		if (bdCollection !=null) {
			play.Logger.debug(this.getClass().getName()+" addBD :  The owner has the collection updated title : " + bdCollection.title);
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
			
		}else {
			play.Logger.error(this.getClass().getName()+" addBD :  The owner has not the the collection updated title : ");
		}
	

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
			
		if (fromModalLoginCalled==false ) {
			return redirect("/" + "#"+nextSection);
		}else {
			return ok(views.html.addBD.render());
		}
		
	}
	
	
	//Action done for Create or Update a Scanned BD
	@Security.Authenticated(Secured.class)
	public Result  scannedBD(){
		response().setHeader(CACHE_CONTROL, "no-cache");
		play.Logger.debug("______________________________________________scannedBD___________________________________ ");
		String resultToBeDisplayed;
		
		Form<CollectionBD> collectionForm = formFactory.form(CollectionBD.class);
		
		CollectionBD formCollection = collectionForm.bindFromRequest().get();
			    
		    
			//i check if the collection extracted from the web store (data into the form) already exist or not
			//in order to know if i have to create it
		    
		    Owners owner =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
		  
		    CollectionBD bdCollection=owner.getCollectionBD().stream()
								    		.filter(e -> e.getTitle().equals( formCollection.title))
								            .findFirst()
								            .orElse(null);
		    formCollection.setOwner(owner);
		    
			if (bdCollection==null){
				play.Logger.debug(this.getClass().getName()+" scannedBD : New scannedBD collection____New:"+formCollection.title);
				play.Logger.debug(this.getClass().getName()+" scannedBD : New scannedBD owner:"+owner.login);
				bdCollection = formCollection; 
				bdCollection.save(); //the bdDATA object is also saved
				resultToBeDisplayed ="Created";
				 
			}else{
				/* here i need to update the collection
				  and check if i need to update an existing comic or 
				  add a new one
				
				*/
				play.Logger.debug(this.getClass().getName()+" scannedBD : Existing scannedBD collection____New:"+formCollection.title);
				formCollection.setId(bdCollection.getId()); //here i update the ID collection of the form object 	
				bdCollection.setEditor(formCollection.editor);
				bdCollection.setTitle(formCollection.title);
				
//				BdData bdData =bdCollection.getBddata().stream()
//						               .filter((item)->{
//						            		play.Logger.debug(this.getClass().getName()+" scannedBD :formCollection.bddata.get(0).title ="+formCollection.bddata.get(0).title);
//						            		play.Logger.debug(this.getClass().getName()+" scannedBD :item.getTitle() ="+item.getTitle());
//						            	   return (  /*formCollection.bddata.get(0).getTitle().equals(item.getTitle())
//						            			   ||*/formCollection.bddata.get(0).getIsbn().equals(item.getIsbn())
//						            			   ||formCollection.bddata.get(0).getNumber().equals(item.getNumber())
//						            			   );
//						               })
//						               .findFirst()
//						               .orElse(null);
//						               
				
				
				List <BdData> BdDataList =bdCollection.getBddata();
				
				boolean BdExisting = BdDataList.stream()
						                       .filter((e)->{
						                    	   return (formCollection.bddata.get(0).getNumber().equals(e.getNumber())
						                    			   ||formCollection.bddata.get(0).getIsbn().equals(e.getIsbn()));
						                       })
						                       .map(returnedValue -> true)
						                       .findFirst()
								               .orElse(false);
				
				if (BdExisting==false) {//New BD into an existing collection
					play.Logger.debug(this.getClass().getName()+" scannedBD : bdData is null");
					
					BdDataList.add(formCollection.getBddata().get(0));
					resultToBeDisplayed ="Created";
				}else { //Update BD into an existing collection
				   

					resultToBeDisplayed ="Updated";		
				}
				
				play.Logger.debug(this.getClass().getName()+" scannedBD : bdData is not null");
				play.Logger.debug(this.getClass().getName()+" scannedBD : formCollection.bddata.get(0).title ="+formCollection.bddata.get(0).title);
				play.Logger.debug(this.getClass().getName()+" scannedBD : formCollection.bddata.get(0).number ="+formCollection.bddata.get(0).number);
				BdDataList = BdDataList.stream()
						.map(item ->{if (/*item.getTitle().equals(formCollection.bddata.get(0).getTitle())
								         ||*/item.getIsbn().equals(formCollection.bddata.get(0).getIsbn())
								         ||item.getNumber().equals(formCollection.bddata.get(0).getNumber())) {
							          return formCollection.bddata.get(0);
						             }else {
						            	 return item;
						             }
							          
						})
						
						.collect(Collectors.toList());
				
				bdCollection.setBddata(BdDataList); 
				bdCollection.update(); 
				
				
				//!\ if you change the collection that mean's you have to update all the book of the collection to be link to the new connection./!\
			}
			play.Logger.debug(this.getClass().getName()+" scannedBD : look for ISBN"+formCollection.bddata.get(0).isbn);
		    play.Logger.debug(this.getClass().getName()+" scannedBD : editor ="+bdCollection.editor +" & collection= " +bdCollection.title );
		
		return ok(views.html.scannedBD.render(resultToBeDisplayed));
	}
	
   
	
	//action to retrieve the cover from ajax call from listBD page
	public Result displayCover (String coverSeeked) {
		
		//use values hardcoded (bdOwned_ or bdWebstore_) from the page  listBD page
		String result[] =coverSeeked.split("_");
		String cover="";
		
		
		if (result.length==2) {
			
			if (result[0].compareToIgnoreCase("bdOwned")==0) {
				BdData bdData =BdData.find.where().eq("id",Long.valueOf(result[1])).findUnique();
				cover=bdData.getImageBase64();
			}else {
				ScraperResults scraperResults =ScraperResults.find.where().eq("id",Long.valueOf(result[1])).findUnique();
				cover=scraperResults.getImageBase64();
			}
			
			return ok (cover);
		
		}
         
		return badRequest();
		

	}
	
	
	 
	
	
		
	
	
	
	//controller to Display the JS page to ask the cover seeked differed from the HTML page
	
	public Result  listCoversJs(String user){
		play.Logger.debug(this.getClass().getName()+" listCoversJs --> login ="+ user);
		
		List<CollectionDisplay> collectionBD = askForCollectionsToBeDisplayed(user);
		//
		return ok(views.html.listCoversJs.render(collectionBD)); 	
	 } 
	
	
	//controller to display the scan page , bddata.number asc
	//by default login ="grobe" defined in routes file.
		public Result  listBD(String login){
			
			List<CollectionDisplay> collectionBD = askForCollectionsToBeDisplayed(login);
			
			play.Logger.debug(this.getClass().getName()+"-listBD session(\"connectedBD\")"+session("connectedBD"));
	        
			 
			 myStat.setStatisticsByLogin(login);
			 
			 
			 return ok(  views.html.listBD.render(collectionBD, myStat,login)); 	
		 } 
	
		
		//controller to get the data of the collection
		//by default login ="grobe" defined in routes file.
			public Result  pivot(String login){
				Owners owner;
				
				
				List<BdPivot> bdPivotListGlobal =new ArrayList <BdPivot>();
				
				if (session("connectedBD")!=null)
					
				{ owner =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
				  login=owner.getLogin();
				}else {
					owner =Owners.find.where().eq("login", login).findUnique();
				}
				
				
				
				List<CollectionBD> collectionBD =owner.getCollectionBD();
						     
				    		
			//create the pivot list to be displayed					 
				for (CollectionBD item :collectionBD) {
                    	List<BdPivot> bdPivotList = item.bddata.stream()
							    .map(bd->{
							        BdPivot bdData = new BdPivot();	
								    bdData.creationDate=bd.getCreationDate();
						    		bdData.designer=bd.getDesigner();
						    		bdData.isbn=bd.getIsbn();
						    		bdData.number=bd.getNumber();
						    		bdData.price=bd.getPrice();
						    		bdData.scenario=bd.getScenario();
						    		bdData.title=bd.getTitle();
						    		bdData.collectionEditor=item.editor;
						    		bdData.collectionTitle=item.title;
						    		return (bdData);
					             })
							    .collect(Collectors.toList());
						                        	
                    	//merge the global list with the new collection
                    	bdPivotListGlobal = Stream.of(bdPivotListGlobal, bdPivotList)
                                .flatMap(x -> x.stream())
                                .collect(Collectors.toList());
				}		                      
				
				//sort by colectionTitel and by number
				Comparator<BdPivot> sortByCollection = (p, o) -> p.collectionTitle.compareToIgnoreCase(o.collectionTitle);
                Comparator<BdPivot> sortByBDNumber  = (p, o) -> p.number.compareToIgnoreCase(o.number);
				
				
				play.Logger.debug(this.getClass().getName()+"-pivot session(\"connectedBD\")"+session("connectedBD"));
		        
				
				 
				bdPivotListGlobal=bdPivotListGlobal.stream().sorted(sortByCollection.thenComparing(sortByBDNumber))
                                                .collect(Collectors.toList());
				 
				 return ok(views.html.pivot.render(Json.toJson(bdPivotListGlobal).toString(),login)); 	
			 } 
		
		
	
			
	private CompletionStage<Result> extractDataFromIsbn (String isbnCode,Owners ownerLogged )		{
		 String url =configuration.getString("webStore.fnac.searchUrl") +isbnCode;
		  
		    play.Logger.debug(this.getClass().getName()+" infoBD :  URL =  " +url);
		    play.Logger.debug(this.getClass().getName()+" infoBD :  listBD :IsbnCode="+isbnCode+ " - size of the file :"+"file.length()");
		  
		  
		
		  
		  //return scanFromFnac.extractDataFromSearch(IsbnCode);  
		  
		  return ws.url(url).get().thenApply(
	  	           response ->{
	  	        	 CollectionBD bdInfo;
		                  // play.Logger.debug("FnacCrawler : crawler2 :thenApply :"+url+" "+ new Date());
		  	        	 play.Logger.debug(this.getClass().getName()+" infoBDinfoBD : isbnCode="+isbnCode);
		  	        	//play.Logger.debug("response.getBody() : "+response.getBody());
		  	        	if (!isbnCode.isEmpty()){
		  	        	    /*Here we check that the IsbnCode is not empty
		  	        	     * in order to save only validated IsbnCode
		  	        	     */
		  	        		
		  	        		bdInfo = scanFromFnac.extractDataFromSearch( response.getBody(),isbnCode,ownerLogged) ;
		  	        		play.Logger.debug(this.getClass().getName()+" infoBD 1.5 : bdExist bdInfo ="+bdInfo);
		  	        		play.Logger.debug("infoBD 1.5 + bdExist bdInfo.id ="+bdInfo.getId());
		  	        		
		  	        	    //First:  i' check if the Bd extracted is already existing on my DB from isbn code
		  	        		boolean bdExist =scanFromFnac.bdExist(isbnCode, bdInfo,ownerLogged);
		  	        		
			  	        	//if (bdExist==false) bdInfo.save();
			  	        	play.Logger.debug(this.getClass().getName()+" infoBD 2 : bdExist ="+bdExist);
			  	        	play.Logger.debug(this.getClass().getName()+" infoBD 3 : bdInfo.getOwner().getLogin() ="+bdInfo.getOwner().getLogin());
			  	        	
			  	        	
			  	        	return ok(views.html.bdInfo.render(bdInfo,bdExist));
		  	        	}else{
		  	        		return ok("Barcode or ISBN code not recognized !! ");
		  	        	}
		  	        	    
		  	        	 
		                
		        
		             });
	}
			
	    //controller to display the data extract from the code bar filled manually by the user
		//and based on the ISBN searches the data on the website Fnac
		  @Security.Authenticated(Secured.class)
		  public CompletionStage<Result>   infoBDManually(String isbnCode){
			  play.Logger.debug(this.getClass().getName()+" infoBD : IsbnCode="+isbnCode);
			  //here i get the connected owner
			  Owners ownerLogged =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
			  
			  return extractDataFromIsbn ( isbnCode,ownerLogged );
		  }
			  
	
	//controller to display the data extract from the code bar scanned
	//and based on the ISBN searches the data on the website Fnac
	  @Security.Authenticated(Secured.class)
	  public CompletionStage<Result>   infoBD(){
		  
		  //String loginConnected;
		  play.Logger.debug(this.getClass().getName()+" infoBD ");
		
		  
		 //here i get the connected owner
		  Owners ownerLogged =Owners.find.where().eq("id", Long.valueOf(session("connectedBD"))).findUnique();
		 
		  
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
		
		    play.Logger.debug(this.getClass().getName()+" infoBD : before scanFromFnac.Scan");
		    String isbnCode =scanFromFnac.scan(file);
		    play.Logger.debug(" BdController : infoBD : after scanFromFnac.Scan");
		   
		    
		    return extractDataFromIsbn ( isbnCode,ownerLogged );
		  
	  }
	  
	  public Result   infoBDFromWebCam(){
		  
		  String isbnCode ="";
		  
		  
		  return ok(isbnCode);
	  }
	  

}                

			 
		  

