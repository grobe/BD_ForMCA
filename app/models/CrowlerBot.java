package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;

@Entity
public class CrowlerBot extends Model {
	@Id
    @Constraints.Min(10)
    public Long id;

	@ManyToOne
    @JsonIgnore
	@Constraints.Required
    public Collection collection;
    
    @Constraints.Required
    public String url;
    
    public static Finder<Long, CrowlerBot> find = new Finder<Long,CrowlerBot>(CrowlerBot.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
}
