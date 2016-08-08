package dk.mvaas.mvaas;

import java.util.ArrayList;
import java.util.LinkedList;
/**
 * @authors: Paul and Karun 
 * 
 */
public class RingNode extends Node {
	/**
	 * It gives neighbours each nodes information to detect each node free disk space
	 * 
	 */

	public RingNode leftNeighbor, rightNeighbor;
	private BasicConsumerRing BCR;

	public RingNode(BasicConsumerRing BCR) {
		super();
		this.BCR = BCR;
	}

	public BasicConsumerRing getBasicConsumer() {
		return BCR;
	}

	public RingNode getLeftNeighbor() {

		return leftNeighbor;
	}

	public void setLeftNeighbor(RingNode leftNeighbor) {
		this.leftNeighbor = leftNeighbor;
	}

	public RingNode getRightNeighbor() {
		return rightNeighbor;
	}

	public void setRightNeighbor(RingNode rightNeighbor) {
		this.rightNeighbor = rightNeighbor;
	}

	public String getInfo() {
		return "Node -" + this.id + "  free disk space = "
				+ elements.getNodeTotalDiskSpace();
	}

	public long getUsedFreeResources() {
		return elements.getnodeUsedDiskSpace();
	}

	public NodeElements getElements() {
		return elements;
	}

	public String toString() {
		return super.toStringX();
	}

}
