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
public class CrowlerResults extends Model {

	@Id
    @Constraints.Min(10)
    public Long id;

	@ManyToOne
    @JsonIgnore
	@Constraints.Required
    public CollectionBD collection;
    
    @Constraints.Required
    public String title;
    
    @Constraints.Required
    public int number;
    
    @Constraints.Required
    public double price;
    
    @Constraints.Required
    public Date CreationDate;
    
    @Constraints.Required
    public String scenario;
    
    @Constraints.Required
    public String designer;
    
    @Constraints.Required
    public String availability;
    
    public static Finder<Long, CrowlerResults> find = new Finder<Long,CrowlerResults>(CrowlerResults.class);

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public Date getCreationDate() {
		return CreationDate;
	}

	public void setCreationDate(Date creationDate) {
		CreationDate = creationDate;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CollectionBD getCollection() {
		return collection;
	}

	public void setCollection(CollectionBD collection) {
		this.collection = collection;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}
	
}
