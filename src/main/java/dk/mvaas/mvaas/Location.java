/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author paul & Karun
 * 
 */
public class Location {
	/**
	 * It gives the location of a producer and a consumer 
	 * 
	 */

	private String loc;

	public Location(String loc) {
		this.loc = loc;
	}

	public String getLocation() {
		return this.loc;
	}
}
