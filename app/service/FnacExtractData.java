package service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FnacExtractData {
	
	
	
	  //i'm looking for something like :"Trolls de Troy - Tome 5 : Les maléfices de la Thaumaturge"
    //the number of the comic should be always between " Tome" and ":"
    //bdTitle.matches(".*\\bTome\\b.*")&&  (StringUtils.countMatches(bdTitle, "Tome")==1) &&(StringUtils.countMatches(bdTitle, ":")==1)
	
	static boolean isItemArticleValidated (Element bdItem){
		
		String bdTitle=getTitle(bdItem);
		return (bdTitle.matches(".*\\bTome\\b.*")&&  (StringUtils.countMatches(bdTitle, "Tome")==1) &&(StringUtils.countMatches(bdTitle, ":")==1));
	}
	
	
	
	
	//In the store Fnac the comic is always in a tag <li class="Article-item"> 
	   //I'm looking for a comic from the list of all the comics from the search 
	   //Elements listBD = doc.select("div.Article-itemGroup");
	static Elements getItemsList(String html){
		
		 Document doc = Jsoup.parse(html);
		 Elements listBD = doc.select("li[class*=Article-item]"); 
		 return listBD;
	}
	static String getEditor(Element bdItem) {
		Element bdEditor= bdItem.select("div[class=editorialInfo]").first();
        String editor="No information";
        
        if (bdEditor !=null){
     	  
        	editor = bdEditor.text();
     	    if (editor.split(" - ").length>1) {
     	    	editor= editor.split(" - ")[2].trim();
     	    }
     	   
        	play.Logger.debug("FnacExtractData : bdEditor :_____MCA___________________________ :"+"bdEditor : "+ bdEditor.text());
                      	    
        }
		
        //bug here if editor have not "-" i get an error exception
        return editor;
	}
	static String getCollection(Element bdItem) {
		//Element bdTitle = bdItem.select("a").first();
        String collection="No information";
        
       
     	  
           collection = getTitle(bdItem);
        	if (collection.contains("Tome")){
        		
        		collection = (collection.split("Tome"))[0];
        	};
     	   play.Logger.debug("FnacExtractData : getCollection :_____MCA___________________________ :"+"collection : "+ collection);
                      	    
       
		return collection.replaceAll(" - ", "").trim();
	}
	
	
	static String getTitle(Element bdItem) {
		Element bdTitle = bdItem.select("a").first();
        String title="No information";
        
        if (bdTitle !=null){
     	  
        	title = bdTitle.text();
        	
     	   play.Logger.debug("FnacExtractData : getTitle :_____MCA___________________________ :"+"bdTitle : "+ bdTitle.text());
                      	    
        }
		return title.trim();
	}
	
	static String getTitleCleaned(Element bdItem) {
		//Element bdTitle = bdItem.select("a").first();
        String title="No information";
        
       
     	  
        	title = getTitle(bdItem);
        	if (title.contains("Tome")){
        		
        		title = (title.split(":"))[1];
        	};
     	   play.Logger.debug("FnacExtractData : getTitleCleaned :_____MCA___________________________ :"+"title : "+ title);
                      	    
       
		return title;
	}
	
	static int getNumber(Element bdItem) {
		
		String bdNumber=getTitle(bdItem);
		int number=0;
		
        if (!bdNumber.contentEquals("No information")){
        	// take the number between" tome" and ":" and remove all the non numeric characters -
        	if (bdNumber.contains("Tome")){
        		number = Integer.parseInt(((bdNumber.split("Tome"))[1].split(":"))[0].replaceAll("[^\\d.]", ""));
        	}
        	
     	   play.Logger.debug("FnacExtractData : getNumber :_____MCA___________________________ :"+"bdNumber : "+ bdNumber);
                      	    
        }
		return number;
	}
	
	
	static String getAvailability(Element bdItem) {
		Element bdAvailability = bdItem.select("span[class=Dispo-txt]").first();
        String availability="No information";
        
        if (bdAvailability !=null){
     	  
        	availability = bdAvailability.text();
     	   play.Logger.debug("FnacExtractData : getAvailability :_____MCA___________________________ :"+"bdAvailability : "+ bdAvailability.text());
                      	    
        }
		return availability;
	}

	//Private function to look for the price of the comic into the HTML fragment bdItem
	static double getPrice(Element bdItem) throws Exception {
		 Element bdPrice = bdItem.select("strong[class=userPrice]").first();
         double price=0;
         
         if (bdPrice !=null){
      	   DecimalFormat df = new DecimalFormat(); 
      	   DecimalFormatSymbols sfs = new DecimalFormatSymbols();
      	   sfs.setDecimalSeparator('€'); 
      	   df.setDecimalFormatSymbols(sfs);
      	   price = df.parse(bdPrice.text()).doubleValue();
      	   play.Logger.debug("FnacExtractData : getPrice :_____MCA___________________________ :"+"bdPrice : "+ bdPrice.text());
                       	    
         }
		return price;
	}
     
	//Private function to look for the designer and the scenario  into the HTML fragment bdItem
	private static Map<String, String> getAuthorScriptWriter(Element bdItem) {
		 Elements bdAuthorScriptWriter = bdItem.select("p[class=Article-descSub]");
         
         play.Logger.debug("FnacExtractData : getAuthorScriptWriter :_____MCA___________________________ :"+"bdAuthorScriptWriter.size"+ bdAuthorScriptWriter.size()); 
         
         
         
         Map<String, String> authorAndScriptWriter = new HashMap<>();
         
         String author="";
    	 String ScriptWriter="";
          //an author exist ?
         if  (bdAuthorScriptWriter.size() >0){
      	  
      	   //where is the author and the script writer ?
      	   //not always in the same order....
        	 
      	   String [] authorOrScriptWriter= bdAuthorScriptWriter.get(0).text().split(",");
      	   
      	  play.Logger.debug("FnacExtractData : authorOrScriptWriter.length="+authorOrScriptWriter.length) ;
      	   if (authorOrScriptWriter.length ==1){
      		   author=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      		   ScriptWriter=authorOrScriptWriter[0].split(Pattern.quote("("))[0];
      	   }else{
      		 for(int i = 0; i< authorOrScriptWriter.length; i++){
      		 
      		
      			if (authorOrScriptWriter[i].contains( "(Dessinateur)")||authorOrScriptWriter[i].contains( "(Auteur)")||authorOrScriptWriter[i].contains( "(Illustration)")){
      				author=author+" - "+authorOrScriptWriter[i].split(Pattern.quote("("))[0];
      			}
      			if (authorOrScriptWriter[i].contains( "(Scénario)")){
      				ScriptWriter=ScriptWriter+" - "+authorOrScriptWriter[i].split(Pattern.quote("("))[0];
      			}
      		};
      	   }
      	   
      	   
      	  
      	   play.Logger.debug("FnacExtractData : getAuthorScriptWriter2:_ bdAuthorScriptWriter.get(0).text()_"+   bdAuthorScriptWriter.get(0).text());
      	    	 
         }
         
         
         authorAndScriptWriter.put("author",author.replaceAll(" - ", "").trim());
         authorAndScriptWriter.put("ScriptWriter",ScriptWriter.replaceAll(" - ", "").trim());
         
         
		return authorAndScriptWriter;
	}
	
	static String getAuthor (Element bdItem) {
		Map<String, String> authorAndScriptWriter = getAuthorScriptWriter(bdItem);
		 play.Logger.debug("FnacExtractData : getAuthor");
		return authorAndScriptWriter.get("author");
		
	}
	
	static String getScriptWriter(Element bdItem) {
		Map<String, String> authorAndScriptWriter = getAuthorScriptWriter(bdItem);
		 play.Logger.debug("FnacExtractData : getScriptWriter");
		return authorAndScriptWriter.get("ScriptWriter");
		
	}
	
}
