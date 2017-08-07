package models;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;

@Entity
public class CollectionBD extends Model {
	@Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String title;
    
    @Constraints.Required
    public String editor;
    
    @Constraints.Required
    public boolean completed;
    
    @ManyToOne
    @JsonIgnore
    //@JsonManagedReference       
    public Owners owner;
    
    
    
     @OneToMany(mappedBy = "collection" ,cascade = CascadeType.ALL)
     // @Constraints.Required
     @OrderBy("number asc")
     @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
     public List<ScraperResults> scraperResults;
    
    @OneToMany(mappedBy = "collection" ,cascade = CascadeType.ALL)
   // @Constraints.Required
    @OrderBy("collection asc")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ScraperBot> scraperBots;
    
    @OneToMany(mappedBy = "collection" ,cascade = CascadeType.ALL)
    // @Constraints.Required
    @OrderBy("number asc")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<BdData> bddata;
    
    
    public static Finder<Long, CollectionBD> find = new Finder<Long,CollectionBD>(CollectionBD.class);
    
    //indicate if yes or not the current collection have some look for into external webstore
    public boolean getFollowingOnWebstores(){
    boolean value=false;
    
    if (scraperBots.size()>0) value=true; 
    	
    	return value;
    }
    
   
    
    
    //Convert the list of BDdata + ScraperResult to list of BdDisplay
    public List<BdDisplay> getBdDisplay (){
		
    	List<BdDisplay> bdDisplay =new ArrayList <BdDisplay>();
    	
    	
    	
    	List<BdData> listBdData = this.getBddata();
    	listBdData.forEach(item ->{
    		                       BdDisplay bdData = new BdDisplay();
						    		bdData.availability="N/A";
									                   //item.collection.id
						    		bdData.creationDate=item.getCreationDate();
						    		bdData.designer=item.getDesigner();
						    		bdData.isbn=item.getIsbn();
						    		bdData.number=item.getNumber();
						    		bdData.price=item.getPrice();
						    		bdData.scenario=item.getScenario();
						    		bdData.imageBase64=item.getImageBase64();
						    		bdData.title=item.getTitle();
			                        bdDisplay.add(bdData);
			
		              });
		
		List<ScraperResults> listScraperResults = this.getScraperResults();
		listScraperResults.forEach(item ->{
				            //bd.availability=" to be complted";
				            //item.collection.id
			                BdDisplay scraperResults = new BdDisplay();
			                scraperResults.id=String.valueOf(item.getId());
			                scraperResults.creationDate=item.getCreationDate();
							scraperResults.designer=item.getDesigner();
							scraperResults.availability=item.getAvailability();
							scraperResults.imageBase64=item.getImageBase64();
							scraperResults.isbn="N/A";
							scraperResults.price=item.getPrice();
							scraperResults.number=item.getNumber();
							scraperResults.scenario=item.getScenario();
							scraperResults.title=item.getTitle();
				         		   
				            bdDisplay.add(scraperResults);
				
				   });
		
		 //remove all the number already added
		 //first list will content some number
		 //second list could have some number used by the first list
		 //the purpose is to delete the duplicate from the second list.
		
		
		 HashSet<Object> seen=new HashSet<>();
		 bdDisplay.removeIf(e->!seen.add(e.number));
		 bdDisplay.sort((BdDisplay bd1, BdDisplay bd2)->
		                 {   int result=-1;
		                	 if ((Double.valueOf(bd1.number)) >(Double.valueOf(bd2.number))) result=1;
		                 
		                     return result;
		                  });
		
	   
		 
		return bdDisplay;
	}
    
    

	public List<ScraperBot> getScraperBots() {
		return scraperBots;
	}

	public void setScraperBots(List<ScraperBot> scraperBots) {
		this.scraperBots = scraperBots;
	}

	public List<BdData> getBddata() {
		return bddata;
	}

	public void setBddata(List<BdData> bddata) {
		this.bddata = bddata;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public List<ScraperResults> getScraperResults() {
		return scraperResults;
	}

	public void setScraperResults(List<ScraperResults> scraperResults) {
		this.scraperResults = scraperResults;
	}




	public Owners getOwner() {
		return owner;
	}




	public void setOwner(Owners owner) {
		this.owner = owner;
	}
    
    
}
