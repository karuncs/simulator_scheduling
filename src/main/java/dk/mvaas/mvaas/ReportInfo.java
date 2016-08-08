/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @authors paul & Karun
 * 
 * */
public class ReportInfo {
	/**
	 * IThis class maintains scheduling information of scheduled storage
	 * requests by scheduling algorithm to to assign video stream, i.e,
	 * connection request variables.
	 * 
	 */
	private MVaaSComponent producer;
	private Consumer consumer;
	private Stream stream;
	private Routing routing;

	public ReportInfo(MVaaSComponent producer, Consumer consumer,
			Stream stream, Routing routing) {
		this.producer = producer;
		this.consumer = consumer;
		this.stream = stream;
		this.routing = routing;
	}

	public MVaaSComponent getProducer() {
		return producer;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public Stream getStream() {
		return stream;
	}

	public Routing getRouting() {
		return routing;
	}

	public String toString() {
		return producer.id + " schedudled to " + consumer.id + "  "
				+ "  route " + routing;
	}

}
