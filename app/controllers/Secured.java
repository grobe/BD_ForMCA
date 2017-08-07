package controllers;

import javax.inject.Inject;

import play.Configuration;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

/*
 * This class secures the  access from people trying to update my collection  without correct login
 * 
 */

/*TODO implement the Secured Class
 * 
 */
public class Secured extends Security.Authenticator {
	 @Inject private Configuration configuration;
		
	    @Override
	    public String getUsername(Context ctx) {
	    	
	    	
			String idOwner =ctx.session().get("connected");
			
			Logger.info("owner.getId() "+idOwner);
			if (idOwner !=null){
				Logger.info("already connected");
					return ("ok");
				}
			Logger.info("not connected yet");
			return null;
			
	    }

	    @Override
	    public Result onUnauthorized(Context ctx) {
	    	
	    	
		    ctx.flash().put("Failed", "You have to log in first !!");
		    ctx.session().clear();
	    	/*TODO implement the redirect
	    	 * 
	    	 */
		    return redirect("controllers.routes.HomeController.index()");
	        
	    }
}
