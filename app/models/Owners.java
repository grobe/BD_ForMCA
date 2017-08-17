package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import com.avaje.ebean.annotation.Formula;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

@Entity
public class Owners extends Model {

    @Id
    @Constraints.Min(10)
    @Column(name="owner_ID")
    public Long id;

    @Constraints.Required
    public String login;
    
    @Constraints.Required
    public String password;
    
   

	@OneToMany(mappedBy = "owner" ,cascade = CascadeType.ALL)
    //@JsonBackReference
   // @Constraints.Required
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<CollectionBD> collectionBD;
    
   
	
    
    
    public static Finder<Long, Owners> find = new Finder<Long,Owners>(Owners.class);




	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public String getLogin() {
		return login;
	}




	public void setLogin(String login) {
		this.login = login;
	}




	public String getPassword() {
		return password;
	}




	public void setPassword(String password) {
		this.password = password;
	}




	public List<CollectionBD> getCollectionBD() {
		return collectionBD;
	}




	public void setCollectionBD(List<CollectionBD> collectionBD) {
		this.collectionBD = collectionBD;
	}




	
	}

	

