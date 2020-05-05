package router;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class Router {

	public static void main(String[] args) throws Exception {
		// create default context
		CamelContext camel = new DefaultCamelContext();

		// register ActiveMQ as the JMS handler
		ActiveMQConnectionFactory activeMqFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		JmsComponent jmsComponent = JmsComponent.jmsComponent(activeMqFactory);
		camel.addComponent("jms", jmsComponent);

		// transfer the entire exchange, or just the body and headers?
		jmsComponent.setTransferExchange(true);

		// trust all classes being used to send serialised domain objects
		activeMqFactory.setTrustAllPackages(true);

		// turn exchange tracing on or off (false is off)
		camel.setTracing(false);

		// enable stream caching so that things like loggers don't consume the messages
		camel.setStreamCaching(true);

		// create and add the builder(s)
		camel.addRoutes(new BobTheBuilder());
		camel.addRoutes(new WendyTheBuilder());

		// start routing
		System.out.println("Starting router...");
		camel.start();
		System.out.println("... ready.  Press enter to shutdown.");
		System.in.read();
		camel.stop();
	}

}
