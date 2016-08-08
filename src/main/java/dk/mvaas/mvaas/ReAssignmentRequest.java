/**
 * 
 */
package dk.mvaas.mvaas;

/**
 * @authors Karun and Paul
 * 
 */

public class ReAssignmentRequest extends Request {
	/**
	 * It re-assign a producer to higher level DC of current DC
	 * ReAssignmentRequest.java
	 */

	MVaaSComponent comp;
	DataCenter center;
	Consumer parentDC;
	private StorageRequest unHandledStoReq;

	public ReAssignmentRequest(MVaaSComponent comp, DataCenter center,
			Consumer parentDC) {
		super();
		this.comp = comp;
		this.center = center;
		this.parentDC = parentDC;
	}

	public MVaaSComponent getComp() {
		return comp;
	}

	public DataCenter getCenter() {
		return center;
	}

	public Consumer getParentDC() {
		return parentDC;
	}

	public StorageRequest getUnHandledStoReq() {
		return this.unHandledStoReq;
	}

	public void setUnHandledStoReq(StorageRequest unHandledStoReq) {
		this.unHandledStoReq = unHandledStoReq;
	}

}
