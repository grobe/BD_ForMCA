package controllers;

import models.Person;

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

public class PersonController extends Controller {
	@Inject WSClient ws;
	
    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public PersonController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    public Result index() {
        return ok(views.html.index.render());
    }

    public CompletionStage<Result>  webService(){
    	
    	
    	
    	CompletableFuture<Result> future = new CompletableFuture<>();
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
    			            	future.completeExceptionally(exception);
    			            }
    			            else { play.Logger.debug("webService : response6 :"+ new Date());
    			            	future.complete(value);
    			            }
    			        });
   
    	 
    	
    	
    }
    
    public Result  webService2(){
    	   
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
    @Transactional
    public Result addPerson() {
        Person person = formFactory.form(Person.class).bindFromRequest().get();
        jpaApi.em().persist(person);
        return redirect(routes.PersonController.index());
    }

    @Transactional(readOnly = true)
    public Result getPersons() {
        List<Person> persons = (List<Person>) jpaApi.em().createQuery("select p from Person p").getResultList();
        return ok(toJson(persons));
    }

}
