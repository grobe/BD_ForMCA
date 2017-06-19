package models;

import java.text.SimpleDateFormat;
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
    public String number;
    
    @Constraints.Required
    public double price;
    
    @Constraints.Required
    public Date creationDate;
    
    @Constraints.Required
    public String scenario;
    
    @Constraints.Required
    public String designer;
    
    @javax.persistence.Column(length=20000)
    @Constraints.Required
    public String imageBase64;

    
    
    
    public String getFormatedDate(){
    	String date= "No creationDate exists for this bd, please set the creatino date before used the formatedDate";
    	
    	if (this.creationDate !=null){
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    		
    		date = formatter.format(this.creationDate);
    		
    	}
    	
    	return date;
    }
    
  

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
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
