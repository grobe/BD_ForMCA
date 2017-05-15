package models;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

@MappedSuperclass
public abstract class BdModel extends Model {
	@Id
    @Constraints.Min(10)
    public Long id;

	@ManyToOne
	@JsonBackReference
	@Constraints.Required
    public CollectionBD collection;
    
    @Constraints.Required
    public String title;
    
    @Constraints.Required
    public int number;
    
    @Constraints.Required
    public double price;
    
    @Constraints.Required
    public Date creationDate;
    
    @Constraints.Required
    public String scenario;
    
    @Constraints.Required
    public String designer;
    

    
    
    
  

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
