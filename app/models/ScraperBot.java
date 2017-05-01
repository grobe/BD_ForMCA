package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;

@Entity
public class ScraperBot extends Model {
	@Id
    @Constraints.Min(10)
    public Long id;

	@ManyToOne
    @JsonIgnore
	@Constraints.Required
    public CollectionBD collection;
    
    @Constraints.Required
    public String url;
    
    @Constraints.Required
    public String Store;
    
    public static Finder<Long, ScraperBot> find = new Finder<Long,ScraperBot>(ScraperBot.class);

	public String getStore() {
		return Store;
	}

	public void setStore(String store) {
		Store = store;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
}
