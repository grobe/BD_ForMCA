package models;

import javax.persistence.Entity;

import com.avaje.ebean.Model.Finder;

import play.data.validation.Constraints;


@Entity
public class BdData extends BdModel {
	
	  @Constraints.Required
	    public String isbn;
	  
	  
	  public static Finder<Long, BdData> find = new Finder<Long,BdData>(BdData.class);


	public String getIsbn() {
		return isbn;
	}


	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}


}
