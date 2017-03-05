package actors;


import akka.actor.*;
import actors.ActorsWebServiceProtocol.*;

public class ActorsWebService extends UntypedActor {
	
    public static Props props = Props.create(ActorsWebService.class);

    public void onReceive(Object msg) throws Exception {
        if (msg instanceof SayHello) {
            sender().tell("Hello, " + ((SayHello) msg).name, self());
        }
    }

}
