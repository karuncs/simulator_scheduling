/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author Karun & Paul
 */
public class BasicConsumerRing extends BasicConsumer {
	/**
	 * A BCR can form any topology. Current system considers it formed as a Ring
	 * form, so all the nodes can form in a Ring. In the Ring each node can
	 * comminute with both side immediate neighbours. This class calculates
	 * which node has max capacity in the Ring and total capacity of all nodes
	 * of the Ring.
	 */

	private RingNode startNode;
	private int ringSize;

	public BasicConsumerRing(String id, DataCenter parent, int ringSize) {
		super(id, parent);
		this.ringSize = ringSize;
		init();

	}

	// initializing bcs as ring
	private void init() {
		startNode = new RingNode(this);
		RingNode last = startNode;
		for (int i = 0; i < ringSize - 1; i++) {
			RingNode rs = new RingNode(this);
			// last.setRightNeighbor(rs);
			rs.setLeftNeighbor(last);
			last = rs;
		}
		// last.setRightNeighbor(startServer);
		startNode.setLeftNeighbor(last);

	}

	// Get Node with maximum free disk space for the streaming
	public RingNode getNodeWithMaxCapacity() {
		long maxfree = Long.MIN_VALUE;
		RingNode result = startNode;
		RingNode current = startNode;
		while (current.getLeftNeighbor() != startNode) {
			if (current.elements.nodeAvailableDiskSpace > maxfree) {
				maxfree = current.elements.nodeAvailableDiskSpace;
				result = current;
			}
			current = current.getLeftNeighbor();
		}
		if (current.elements.nodeAvailableDiskSpace > maxfree) {
			maxfree = current.elements.nodeAvailableDiskSpace;
			result = current;
		}
		return result;
	}

	// Get total disk space of bc
	public long getTotalDiskSpaceOfBC() {
		long totalfree = 0L;
		RingNode current = startNode;
		while (current.getLeftNeighbor() != startNode) {
			totalfree += current.elements.nodeAvailableDiskSpace;
			current = current.getLeftNeighbor();
		}
		totalfree += current.elements.nodeAvailableDiskSpace;
		return totalfree;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id + ",  topology: ring, size = " + ringSize);

		return sb.toString();

	}

}
