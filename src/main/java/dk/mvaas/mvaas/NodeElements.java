package dk.mvaas.mvaas;

/**
 * @authors  Karun and Paul 
 * 
 */

public class NodeElements {
	/**
	 * This class calculates all parameters of nodes including 
	 * each node capacity and disk space.
	 * 
	 */
	public long nodeTotalCapacity, nodeReservedCapacity, nodeAvailableCapacity,
			nodeUsedDiskSpace, nodeTotalDiskSpace, nodeAvailableDiskSpace,
			bitsPerSecond;

	public NodeElements(long nodeTotalCapacity, long nodeUsedDiskSpace,
			long bitsPerSecond) {
		this.nodeTotalCapacity = nodeTotalCapacity;
		this.nodeTotalDiskSpace = (long) ((1.0 - Constants.SPACE_SLAG) * nodeTotalCapacity);
		// Disk space for servers and is required diskspace for storage requests
		this.nodeUsedDiskSpace = nodeUsedDiskSpace; // for servers
		this.nodeAvailableDiskSpace = nodeTotalDiskSpace - nodeUsedDiskSpace; // for servers
		if (nodeTotalCapacity < 0 || nodeTotalDiskSpace < 0
				|| nodeUsedDiskSpace < 0 || nodeReservedCapacity < 0) {
			// "* Parameterset: negative value of some space parameter.");
			System.exit(0);
		}
		this.nodeReservedCapacity = 0; // No space reserved at construction
										// time (this is space for which there
										// are storage requests in the queue)
		this.nodeAvailableDiskSpace = nodeTotalDiskSpace - nodeUsedDiskSpace;
		this.nodeAvailableCapacity = nodeTotalCapacity - nodeReservedCapacity;
		// Space that can be assigned to new storage requests
		this.bitsPerSecond = bitsPerSecond;
	}

	// parameters for storage

	/**
	 * 
	 * @param defaultParamset
	 */
	public NodeElements(NodeElements other) {
		this.nodeTotalCapacity = other.nodeTotalCapacity;
		this.nodeReservedCapacity = other.nodeReservedCapacity;
		this.nodeAvailableCapacity = other.nodeAvailableCapacity;
		this.nodeUsedDiskSpace = other.nodeUsedDiskSpace;
		this.nodeTotalDiskSpace = other.nodeTotalDiskSpace;
		this.nodeAvailableDiskSpace = other.nodeAvailableDiskSpace;
		this.bitsPerSecond = other.bitsPerSecond;
	}

	public long getBitsPerSecond() {
		return bitsPerSecond;
	}

	public void setBitsPerSecond(long bitsPerSecond) {
		this.bitsPerSecond = bitsPerSecond;
	}

	public long getNodeTotalDiskSpace() {
		return nodeTotalDiskSpace;
	}

	public void setTotalNodeDiskSpace(long nodeTotalDiskSpace) {
		this.nodeTotalDiskSpace = nodeTotalDiskSpace;
		this.nodeAvailableDiskSpace = nodeTotalDiskSpace - nodeUsedDiskSpace;
	}

	public long getnodeCapacity() {
		return nodeTotalCapacity;
	}

	public long getnodeUsedDiskSpace() {
		return nodeUsedDiskSpace;
	}

	public void setnodeUsedDiskSpace(long nodeUsedDiskSpace) {
		this.nodeUsedDiskSpace = nodeUsedDiskSpace;
		this.nodeAvailableDiskSpace = nodeTotalDiskSpace - nodeUsedDiskSpace;

	}

	public void useReservedNodeCapacity(long CapacityToBeUsed) {
		if (CapacityToBeUsed > this.nodeReservedCapacity) {
			// Debug.debug(4,"ERROR useReservedDiskSpace: not enough space to use");
			// Debug.debug(4," free="+nodeCapacity+"  tobeused="+spaceToBeUsed);

		} else {
			this.nodeUsedDiskSpace += CapacityToBeUsed;
			this.nodeAvailableDiskSpace = this.nodeTotalDiskSpace
					- this.nodeUsedDiskSpace;
			this.nodeReservedCapacity -= CapacityToBeUsed;
			this.nodeAvailableCapacity = this.nodeTotalCapacity
					- this.nodeReservedCapacity;
		}
	}

	public long getNodeReservedCapacity() {
		return nodeReservedCapacity;
	}

	public long getNodeAvailableDiskSpace() {
		return nodeAvailableDiskSpace;
	}
}
