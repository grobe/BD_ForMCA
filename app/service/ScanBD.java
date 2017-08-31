package service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.ImplementedBy;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import models.BdData;
import models.CollectionBD;
import models.Owners;
import models.ScraperResults;

@ImplementedBy(FnacScanBD.class)
public interface ScanBD {
	
	
	
	//based on an image of barcode this function extract the ISBN code. 
	default String scan(File file){
		
		String  isbnCode="";
		
		 try {
				
			  
			  
			  InputStream barCodeInputStream = new FileInputStream(file);  
			  BufferedImage barCodeBufferedImage = ImageIO.read(barCodeInputStream);  
			    
			  LuminanceSource source = new BufferedImageLuminanceSource(barCodeBufferedImage);  
			  BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
			  Reader reader = new MultiFormatReader();
			  Hashtable<DecodeHintType, Object> hint = new Hashtable<DecodeHintType, Object>();
		       hint.put(DecodeHintType.TRY_HARDER, BarcodeFormat.EAN_13);
			  com.google.zxing.Result result = reader.decode(bitmap,hint);  
			  
			  isbnCode=result.getText();
			 
		} catch (NotFoundException | ChecksumException | FormatException | IOException e) {
			
			 play.Logger.error(this.getClass().getName()+": ScanBD :"+e.getMessage());
		} finally{		  
		  play.Logger.debug(this.getClass().getName()+": ScanBD :"+"- IsbnCode="+isbnCode+ " - size of the file :"+file.length());
		 
		}
		return (isbnCode);
	}
	
	//i'm looking for if ISBN or title or number  exist on the current collection into the database for the connected owner
	default boolean bdExist(String isbnCode, CollectionBD bdInfo, Owners ownerLogged){
		play.Logger.debug("ScanBD : bdExist : isbnCode ="+isbnCode);
		play.Logger.debug("ScanBD : bdExist : bdInfo.title ="+bdInfo.title); //title and not id because id can be null for a new collection.
		play.Logger.debug("ScanBD : bdExist : bdInfo.getBddata().get(0).number ="+bdInfo.getBddata().get(0).number);
		

		
        boolean bdDataFound ;
        play.Logger.debug("ScanBD : bdExist : owner ="+ownerLogged.getLogin());
        
        
        play.Logger.debug("ScanBD : bdExist : collectionScanned size of collections ="+ownerLogged.getCollectionBD().size());
        
        
        CollectionBD collectionScanned = ownerLogged.getCollectionBD().stream()
		 
				               .filter(collection1->{play.Logger.debug("ScanBD : bdExist : collection1.title="+collection1.title);
				               						play.Logger.debug("ScanBD : bdExist : bdInfo.title="+bdInfo.title);
				            	                    return (collection1.title.equals(bdInfo.title));})
				                .findFirst()
				                .orElse(null);
		 
        //
        
        if   (collectionScanned !=null){
			play.Logger.debug("ScanBD : bdExist : collectionScanned is not null");
			play.Logger.debug("ScanBD : bdExist : collectionScanned size of BdDAta ="+collectionScanned.getBddata().size());
			
			/*I don't understand why but i cannot used collectionScanned.getBddata() to retrieve all the comics from the selected
			 * collection. I have to make a new SQL request thanks to Ebean to get all the data from BDdata.
			 * To be investigate later. 
			 */
			 
			CollectionBD collection= CollectionBD.find.where().eq("id",collectionScanned.getId() ).findUnique();
			 
			 BdData bdFromCollectionScanned =collection.bddata.stream()
					 .filter(e -> { 
					  play.Logger.debug("ScanBD : bdExist : collectionScanned="+collectionScanned.title);
					  play.Logger.debug("ScanBD : bdExist : e.title="+e.title); 
					  play.Logger.debug("ScanBD : bdExist : e.isbn="+e.isbn); 
					  play.Logger.debug("ScanBD : bdExist : e.number="+e.number);
			            return( e.number.equals(bdInfo.getBddata().get(0).number)
			            		||e.isbn.equals(bdInfo.getBddata().get(0).isbn));
			            		
			           })
				      .findFirst()
				      .orElse(null);
		      if (bdFromCollectionScanned != null) {
		    	  bdDataFound=true;
		    	  play.Logger.debug("ScanBD : bdExist : bd is not null");
		      }else {
		    	  bdDataFound=false;
		    	  play.Logger.debug("ScanBD : bdExist : bd is null");
		      }
		    	  
		
		}else {
			bdDataFound=false;
			play.Logger.debug("ScanBD : bdExist : collectionScanned is null");
		}
		  
        
        
		play.Logger.debug("ScanBD : bdExist : bdDataFound ="+bdDataFound);
		
		 
		
		
		return ((bdDataFound));
	}
	
}
