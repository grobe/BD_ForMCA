package models;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

public class LoginForm extends Owners {
	
	@Constraints.Required
	String callBackURL="";
	
	/*
	 * validate the form and return null if validated 
	 * or "false" if not
	 */
	
	public String getCallBackURL() {
		return callBackURL;
	}

	public void setCallBackURL(String callbackURL) {
		this.callBackURL = callbackURL;
	}

	public List<ValidationError> validate() {
	    Logger.debug("Owners : errors i'm in  :");
		List<ValidationError> errors = new ArrayList<>();
		
        if (login==null|| login.length() == 0) {
        	errors.add(new ValidationError("login", "No login was given."));
        }
        if (password==null|| password.length() == 0) {
        	errors.add(new ValidationError("password", "No password was given."));
        }
        if (callBackURL==null|| callBackURL.length() == 0) {
        	errors.add(new ValidationError("callBackURL", "No callBackURL was given."));
        }
       // if (callBackURL=="") return "false";
        Logger.debug("Owners : errors.size()  :"+errors.size());
        if(errors.size() > 0) return errors;
        return null;
    }

}
