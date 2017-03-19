package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonFormat;

import play.data.validation.Constraints;

@Entity
public class Collection extends Model {
	@Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String title;
    
    @Constraints.Required
    public String editor;
    
    @Constraints.Required
    public boolean completed;
    
    
    @OneToMany(mappedBy = "collection" ,cascade = CascadeType.ALL)
    //@JsonBackReference
   // @Constraints.Required
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<CrowlerResults> crowlerResults;
    
    public static Finder<Long, Collection> find = new Finder<Long,Collection>(Collection.class);

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

	public List<CrowlerResults> getCrowlerResults() {
		return crowlerResults;
	}

	public void setSeries(List<CrowlerResults> crowlerResults) {
		this.crowlerResults = crowlerResults;
	}
    
    
}
