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


public class BdPivot {
	
	public String collectionTitle;
	
	public String title;
    
    
    public String number;
 
    public double price;
    
 
    public Date creationDate;
    
   
    public String scenario;
    
   
    public String designer;
    
    public String isbn;
   
    public String getIsbn() {
		return isbn;
	}


	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	
    
    public String collectionEditor;
    
    
    
    public String getCollectionTitle() {
		return collectionTitle;
	}


	public void setCollectionTitle(String collectionTitle) {
		this.collectionTitle = collectionTitle;
	}


	public String getCollectionEditor() {
		return collectionEditor;
	}


	public void setCollectionEditor(String collectionEditor) {
		this.collectionEditor = collectionEditor;
	}


	public String getFormatedDate(){
    	String date= "No creationDate exists for this bd, please set the creatino date before used the formatedDate";
    	
    	if (this.creationDate !=null){
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    		
    		date = formatter.format(this.creationDate);
    		
    	}
    	
    	return date;
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
