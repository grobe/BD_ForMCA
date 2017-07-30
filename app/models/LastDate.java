package models;

import java.util.Date;

import javax.persistence.Entity;

import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class LastDate {
Date lastDate;

public Date getLastDate() {
	return lastDate;
}

public void setLastDate(Date lastDate) {
	this.lastDate = lastDate;
}
}
