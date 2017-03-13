package controllers;

import javax.inject.*;
import akka.actor.*;
import play.mvc.*;
import scala.compat.java8.FutureConverters;
import java.util.concurrent.CompletionStage;
import actors.ActorsWebServiceProtocol.*;

import static akka.pattern.Patterns.ask;

@Singleton
public class ActorController extends Controller {

	
	final ActorRef actorsWebService;

    @Inject 
    public  ActorController(ActorSystem system) {
    	play.Logger.debug("ActorController : ActorController");
    	actorsWebService = system.actorOf(actors.ActorsWebService.props);
    }
	
    public CompletionStage<Result> sayHello(String name) {
    	
    	play.Logger.debug("ActorController : sayHello(String name)");
        return FutureConverters.toJava(ask(actorsWebService, new SayHello(name), 1000))
                .thenApply(response -> ok((String) response));
    }

    //j
}
