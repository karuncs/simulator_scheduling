/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author Paul
 * 
 */
public class AssignmentRequest extends Request {

	/**
	 * This class assigns the component to a datacenter. The component could be
	 * a producer or consumer or user or out source. This class always maintains
	 * two parameters MVaaSComponent and DataCenter.
	 */

	MVaaSComponent comp;
	DataCenter center;

	public AssignmentRequest(MVaaSComponent comp, DataCenter center) {
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
