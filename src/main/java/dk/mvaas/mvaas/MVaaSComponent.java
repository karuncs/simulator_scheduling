/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author Paul
 */
public class MVaaSComponent {

	/**
	 * A component could be a producer or a consumer. Every component has Unique
	 * identification. This class is the master class of Producer.java and
	 * Consumer.java.
	 * 
	 */
	private static int NEXTID = 0;

	protected String id;
	protected Location location;

	public MVaaSComponent() {
		id = "" + NEXTID++;
	}

	public String getID() {
		return id;
	}

}
