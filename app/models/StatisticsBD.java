package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import play.Logger;

public class StatisticsBD {
	
	private String bdNumber="0";
	private String collectionNumber="0";
	private String lastUpdateWeb="never";
	private String lastUpdateOwned="never";
	
	public  StatisticsBD(){
		
		Logger.debug("StatisticsBD : StatisticsBD"); 
		
		
		
	}
	
	/*
	 * TODO refactor  StatisticsBD(String login) & StatisticsBD() to mutualise
	 */
	public  void setStatisticsByLogin (String login){
		
		// i check if the login exist, ort not
		Logger.debug("StatisticsBD : setStatisticsByLogin : login :"+login); 
		Owners owner =Owners.find.where().eq("login", login).findUnique();
		if (owner !=null) {
		     Logger.debug("StatisticsBD : setStatisticsByLogin OWner exist"); 
		     String dateTimeFormatPattern = "dd/MM/YYYY z";
		    final DateFormat format = new SimpleDateFormat(dateTimeFormatPattern);
		
		    String dateTimeFormatPatternDetailed = "dd/MM/YYYY HH:mm:ss z";
		    final DateFormat formatDetailed = new SimpleDateFormat(dateTimeFormatPatternDetailed);
		
			//collectionNumber
			int resultCollectionValue;
			
		
			resultCollectionValue = CollectionBD.find.where().eq("owner",owner).findRowCount();
			collectionNumber = String.valueOf(resultCollectionValue);
			
			//bdNumber
			int resultBdvalue;
			List<CollectionBD> collectionList =CollectionBD.find.where().eq("owner",owner).findList();
			resultBdvalue = BdData.find.where().in("collection", collectionList).findRowCount();
			bdNumber = String.valueOf(resultBdvalue);
			
			//lastUpdateOwned
			//String sql ="SELECT max(creation_date) as lastDate FROM bd.bd_data;";
			String sql ="SELECT max(creation_date) as lastDate FROM bd.bd_data, bd.collection_bd,bd.owners";
			sql=sql+" where bd.owners.login='"+login+"'";
			sql=sql+" and bd.collection_bd.owner_owner_id=bd.owners.owner_id";
			sql=sql+" and bd.collection_bd.id=bd.bd_data.collection_id;";
			RawSql rawSql =	RawSqlBuilder
							  .parse(sql)
							  .create();
			LastDate lastDateOwned = Ebean.find(LastDate.class)
					      .setRawSql(rawSql)
					      .findUnique();
			if (lastDateOwned.getLastDate()!=null) lastUpdateOwned = format.format(lastDateOwned.getLastDate()); 
			 
			//lastUpdateWeb
		   //String sqlWebStore ="SELECT max(last_review_from_web_store) as lastDate FROM bd.scraper_results;";

			String sqlWebStore ="SELECT max(last_review_from_web_store) as lastDate FROM bd.scraper_results, bd.collection_bd, bd.owners";
			sqlWebStore=sqlWebStore+" where bd.owners.login='"+login+"'";
			sqlWebStore=sqlWebStore+" and bd.collection_bd.owner_owner_id=bd.owners.owner_id";
			sqlWebStore=sqlWebStore+" and bd.collection_bd.id=bd.scraper_results.collection_id;";
			
			RawSql rawSqlWebStore =	RawSqlBuilder
		    					  .parse(sqlWebStore)
								  .create();
		   LastDate lastDateWebStore = Ebean.find(LastDate.class)
						      .setRawSql(rawSqlWebStore)
						      .findUnique();   
		   if (lastDateWebStore.getLastDate()!=null) lastUpdateWeb = formatDetailed.format(lastDateWebStore.getLastDate()); 
			
		}else {
			bdNumber="0";
			collectionNumber="0";
			lastUpdateWeb="never";
			lastUpdateOwned="never";
		}
		
		
	}
		
	
	public String getBdNumber(){
		
		return this.bdNumber;
		
	}


	public String getCollectionNumber() {
		return collectionNumber;
	}


	public void setCollectionNumber(String collectionNumber) {
		this.collectionNumber = collectionNumber;
	}


	public String getLastUpdateWeb() {
		return lastUpdateWeb;
	}


	public void setLastUpdateWeb(String lastUpdateWeb) {
		this.lastUpdateWeb = lastUpdateWeb;
	}


	public String getLastUpdateOwned() {
		return lastUpdateOwned;
	}


	public void setLastUpdateOwned(String lastUpdateOwned) {
		this.lastUpdateOwned = lastUpdateOwned;
	}
	
	
}
