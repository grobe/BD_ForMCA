package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class StatisticsBD {
	
	private String bdNumber="0";
	private String collectionNumber="0";
	private String lastUpdateWeb="never";
	private String lastUpdateOwned="never";
	
	public  StatisticsBD(){
		
		String dateTimeFormatPattern = "dd/MM/YYYY z";
		final DateFormat format = new SimpleDateFormat(dateTimeFormatPattern);
		
		String dateTimeFormatPatternDetailed = "dd/MM/YYYY HH:mm:ss z";
		final DateFormat formatDetailed = new SimpleDateFormat(dateTimeFormatPatternDetailed);
		
		//collectionNumber
		int resultCollectionValue;
		resultCollectionValue = CollectionBD.find.where().findRowCount();
		collectionNumber = String.valueOf(resultCollectionValue);
		
		//bdNumber
		int resultBdvalue;
		resultBdvalue = BdData.find.where().findRowCount();
		bdNumber = String.valueOf(resultBdvalue);
		
		//lastUpdateOwned
		String sql ="SELECT max(creation_date) as lastDate FROM bd.bd_data;";
		RawSql rawSql =	RawSqlBuilder
						  .parse(sql)
						  .create();
		LastDate lastDateOwned = Ebean.find(LastDate.class)
				      .setRawSql(rawSql)
				      .findUnique();
		if (lastDateOwned.getLastDate()!=null) lastUpdateOwned = format.format(lastDateOwned.getLastDate()); 
		 
		//lastUpdateWeb
	   String sqlWebStore ="SELECT max(last_review_from_web_store) as lastDate FROM bd.scraper_results;";
	   RawSql rawSqlWebStore =	RawSqlBuilder
	    					  .parse(sqlWebStore)
							  .create();
	   LastDate lastDateWebStore = Ebean.find(LastDate.class)
					      .setRawSql(rawSqlWebStore)
					      .findUnique();   
	   if (lastDateWebStore.getLastDate()!=null) lastUpdateWeb = formatDetailed.format(lastDateWebStore.getLastDate()); 
		
		
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
