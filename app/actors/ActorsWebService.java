package actors;


import akka.actor.*;
import play.api.Play;
import service.FnacCrawler;

import java.util.Date;

import javax.inject.Inject;

import actors.ActorsWebServiceProtocol.*;

public class ActorsWebService extends UntypedActor {
	
    public static Props props = Props.create(ActorsWebService.class);
   
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof SayHello) {
            sender().tell("Hello, " + ((SayHello) msg).name, self());
            play.Logger.debug("ActorsWebService- : onReceive : msg instanceof SayHello)");
        }
        if (msg.equals("update")){
        	
        	 //Play.current().injector().instanceOf(FnacCrawler.class);
        	SayHello sh = new SayHello((String)msg);
        	//sender().tell("Hello, " + sh.name, self());
        	 play.Logger.debug("ActorsWebService- : onReceive : msg.equals(update):"+"-"+new Date());
        }
    }

}
