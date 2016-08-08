/**
 * 
 */
package dk.mvaas.mvaas;

/**
 * @authors Karun and Paul
 * 
 */

public class HealthCheckRequest extends Request {
	/**
	 * It check the health status of the system components
	 * HealthCheckRequest.java
	 */

	private MVaaSComponent producer;
	private Consumer consumer;

	public HealthCheckRequest(MVaaSComponent producer, Consumer consumer) {
		super();
		this.producer = producer;
		this.consumer = consumer;

		System.out.println(" health check req --> " + this.consumer.getID());

	}

	public MVaaSComponent getProd() {
		return producer;
	}

	public Consumer getconsumer() {
		return consumer;
	}

}
