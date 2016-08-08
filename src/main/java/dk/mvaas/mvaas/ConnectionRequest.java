/**
 * 
 */
package dk.mvaas.mvaas;

/**
 * @authors: Karun and Paul
 * 
 */
public class ConnectionRequest extends Request {
	/**
	 * It connects a producer and a node in a basic consumer of a DC based on
	 * scheduling report. This class always maintains four parameters to route
	 * the stream to scheduled node from associated producer. The parameters are
	 * MVaaSComponent, Consumer, Stream, Routing.
	 */

	private MVaaSComponent producer;
	private Consumer consumer;
	private Stream stream;
	private Routing routing;

	public ConnectionRequest(MVaaSComponent producer, Consumer consumer,
			Stream stream, Routing routing) {
		super();
		this.producer = producer;
		this.consumer = consumer;
		this.stream = stream;
		this.routing = routing;
	}

	public MVaaSComponent getProd() {
		return producer;
	}

	public Consumer getconsumer() {
		return consumer;
	}

	public Stream getStream() {
		return stream;
	}

	public Routing getRouting() {
		return routing;
	}
}
