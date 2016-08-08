/**
 * 
 */
package dk.mvaas.mvaas;

/**
 * @author karun
 */
public class Routing {
	/**
	 * This class has routing information of a node which can be consumed
	 * stream. This node identified by a scheduling algorithm.
	 */
	private RingNode node;

	public Routing(RingNode node) {
		this.node = node;
	}

	public RingNode getnode() {
		return node;
	}

	public String toString() {
		return node.id;
	}

}
