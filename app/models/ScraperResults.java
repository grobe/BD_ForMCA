package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;

@Entity
public class ScraperResults extends BdModel {

	
	
    @Constraints.Required
    public String availability;
    
    public Date lastReviewFromWebStore;
	
	


	public static Finder<Long, ScraperResults> find = new Finder<Long,ScraperResults>(ScraperResults.class);

    public String getAvailability() {
  		return availability;
  	}

  	public void setAvailability(String availability) {
  		this.availability = availability;
  	}
    public Date getLastReviewFromWebStore() {
		return lastReviewFromWebStore;
	}

	public void setLastReviewFromWebStore(Date lastReviewFromWebStore) {
		this.lastReviewFromWebStore = lastReviewFromWebStore;
	}
}
