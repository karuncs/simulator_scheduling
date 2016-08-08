/**
 * 
 */
package dk.mvaas.mvaas;

import java.text.DecimalFormat;

/**
 ** @authors: Karun & Paul
 */
public class StorageRequest extends Request {
	/**
	 * This class activates by SimDriver.java class to MVaaSComponent and
	 * DataCenter details. The storage requests can be generated any source, so
	 * this class maintains MVaaSComponent.
	 * 
	 */
	MVaaSComponent comp;
	DataCenter center;
	private static DecimalFormat df3 = new DecimalFormat("000");
	private NodeElements elements;
	private long totalDuration, restDuration;

	public StorageRequest(MVaaSComponent comp, DataCenter center) {
		super();
		this.comp = comp;
		this.center = center;
	}

	public MVaaSComponent getComp() {
		return comp;
	}

	public DataCenter getCenter() {
		return center;
	}
	//Use it in ReAssignment Req
	public void setCenter( DataCenter dc) {
		this.center=dc;
	}
	public NodeElements getElements() {
		return elements;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public long getRestDuration() {
		return restDuration;
	}

	public boolean reduceDuration(long rediction) {
		restDuration -= rediction;
		if (restDuration < 0L) {
			restDuration = 0L;
		}
		return (restDuration == 0L);
	}

	public boolean isRunning() {
		return (restDuration > 0L);
	}

	public void reduceDiskDemand(long dd) {
		this.elements.nodeTotalDiskSpace -= dd;
	}

	public String toString() {
		return "[[ReqS -" + df3.format(this.id) + " comp = " + comp
				+ "  target = " + center + " ]]";

	}

}
