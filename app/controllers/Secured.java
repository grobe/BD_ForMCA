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


public class Secured extends Security.Authenticator {
	 @Inject private Configuration configuration;
		
	    @Override
	    public String getUsername(Context ctx) {
	    	
	    
	    	Logger.debug("Secured : getUsername : session(\"connectedBD\")" + ctx.session().get("connectedBD"));
	    	String idOwner =ctx.session().get("connectedBD");
			String mig =ctx.session().get("miguel");

			Logger.debug("Secured : getUsername : ctx.request().uri().toString() = "+ctx.request().uri());

			String callBackURL =ctx.request().uri().replaceAll("/", "").split("\\?")[0];
			
			
			
			if (idOwner !=null){
				Logger.debug("Secured-getUsername already connected : idOwner="+idOwner);
					return ("ok");
				}
			Logger.info("Secured-getUsername : not connected yet : callBackURL = "+callBackURL);
			ctx.flash().put("callBackURL", callBackURL);
			return null;
			
	    }

	    @Override
	    public Result onUnauthorized(Context ctx) {
	    	String idOwner =ctx.session().get("connected");
			String mig =ctx.session().get("miguel");
			String callBackURL="not used";
			
	    	Logger.debug("Secured-onUnauthorized  ctx.session().size(); "+ctx.session().size());
			Logger.debug("Secured-onUnauthorized ctx.session().get(\"connectedDB\") "+idOwner);
		
			
		    ctx.flash().put("Secured : Failed", "You have to log in first before to be able to use this feature !!");
		    //ctx.session().clear();
	    	/*TODO implement the redirect
	    	 * 
	    	 */
		    callBackURL= ctx.flash().get("callBackURL");
		    ctx.flash().put("callBackURL", callBackURL);
		    Logger.debug("Secured-onUnauthorized ctx.session().get(\"miguel\") "+ mig);
		    return redirect(controllers.routes.BdController.login(callBackURL));
	        
	    }
}
