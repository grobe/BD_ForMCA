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
	
	//check if the isbnCode already exist in the Database
	default boolean bdExist(String isbnCode, CollectionBD bdInfo){
		play.Logger.debug("ScanBD : bdExist : isbnCode ="+isbnCode);
		play.Logger.debug("ScanBD : bdExist : bdInfo.id ="+bdInfo.id);
		play.Logger.debug("ScanBD : bdExist : bdInfo.getBddata().get(0).number ="+bdInfo.getBddata().get(0).number);
		
		BdData bdDataWithISBN = BdData.find.where().eq("isbn", isbnCode).findUnique();
		 play.Logger.debug("ScanBD : bdExist : bdDataWithISBN ="+bdDataWithISBN);
		BdData bdDataWithCollectionAndNumber = BdData.find.where().eq("collection_id", bdInfo.id).eq("number", bdInfo.getBddata().get(0).number).findUnique();
		play.Logger.debug("ScanBD : bdExist : bdDataWithCollectionAndNumber ="+bdDataWithCollectionAndNumber);
		
		
		return (!(bdDataWithISBN==null&&bdDataWithCollectionAndNumber==null));
	}
	
}
