package service;

import models.BdData;
import models.CollectionBD;
import models.Owners;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DecitreScanBD implements ScanBD {

    private static final int ArrayList = 0;


    @Inject
    WSClient ws;
    public DecitreScanBD() {

    }
    public CollectionBD extractDataFromSearch(String html, String isbn, Owners owner){
        play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 1");

        play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 2");
        BdData bdInfo =new BdData();
        CollectionBD bdCollection;

        play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 3");

        /*i get the result from the HTML
         * the HTML should content only
         * 1 values
         *
         */
        Element listBD = DecitreExtractData.getItemList(html);

        play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 3.5 = ");

        //I check if the answer from the Fnac webStore return a validated ITem based on the ISBN code
        if ((listBD!=null)){
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 3.6 = "+listBD.text());
            //i check if the collection extracted from the web store already exist or not
            //in order to know if i have to create it
            bdCollection=owner.getCollectionBD().stream()
                    .filter(e -> e.getTitle().equals(DecitreExtractData.getCollection(listBD)))
                    .findFirst()
                    .orElse(null);

            //Todo raise an error  if BD collection has more than one result

            if (bdCollection==null){
                play.Logger.debug("DecitreScanBD : extractDataFromSearch : New ____extractData collection____New");
                bdCollection = new CollectionBD();
                bdCollection.setTitle(DecitreExtractData.getCollection(listBD));
                bdCollection.setEditor(DecitreExtractData.getEditor(listBD));
                bdCollection.setOwner(owner);
                //bdCollection.save();

            }

            bdInfo.setCollection(bdCollection);
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : DecitreScanBD : extractDataFromSearch : extractData collection");

            bdInfo.setIsbn(isbn);
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData isbn-"+isbn);



            bdInfo.setCreationDate(new Date());
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData creationDate"+bdInfo.creationDate);

            bdInfo.setImageBase64(DecitreExtractData.getImageBAse64(listBD));
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData creationDate"+bdInfo.creationDate);

            bdInfo.setTitle(DecitreExtractData.getTitle(listBD));
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 7");
            bdInfo.setNumber(DecitreExtractData.getNumber(listBD));
            //play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 7.9 :listBD"+listBD);
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 8 :DecitreExtractData.getNumber(listBD=" + DecitreExtractData.getNumber(listBD));
            bdInfo.setDesigner(DecitreExtractData.getDesigner(listBD));
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 9");
            bdInfo.scenario=DecitreExtractData.getScriptWriter(listBD);
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 10");
            try {
                bdInfo.price=DecitreExtractData.getPrice(listBD);
                play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 11");
            } catch (Exception e) {
                play.Logger.error(this.getClass().getName()+"\n : "+e.getMessage());
            }
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 12");
        }else{
            //put here the setup of an bdingo empty with only an isbn code
            bdCollection=owner.getCollectionBD().stream()
                    .filter(e -> e.getTitle().equals("Not Found"))
                    .findFirst()
                    .orElse(null);

            play.Logger.debug("DecitreScanBD : extractDataFromSearch : bdCollection");
            if (bdCollection==null){
                play.Logger.debug("DecitreScanBD : extractDataFromSearch : bdCollection = null");
                bdCollection = new CollectionBD();
                bdCollection.setTitle("Not Found");
                bdCollection.setEditor("Not Found");
                bdCollection.setOwner(owner);
                //bdCollection.save();
            }
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : bdCollection = null after if");
            bdInfo.setCollection(bdCollection);
            play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData not bd found from Web store");

            bdInfo.setIsbn(isbn);
        }

        List<BdData> bddata= new ArrayList<BdData>();
        bddata.add(bdInfo);
        bdCollection.setBddata(bddata);
        play.Logger.debug("DecitreScanBD : extractDataFromSearch : extractData 13");

        return   bdCollection;


    }

}
