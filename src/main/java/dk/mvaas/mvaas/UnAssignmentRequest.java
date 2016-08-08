/**
 * 
 */
package dk.mvaas.mvaas;

/**
 * UnAssignmentRequest.java
 */
public class UnAssignmentRequest extends Request {
	MVaaSComponent comp;
	DataCenter center;

	public UnAssignmentRequest(MVaaSComponent comp, DataCenter center) {
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

}
