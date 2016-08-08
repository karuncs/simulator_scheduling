/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author paul & Karun
 * 
 */
public class Stream {
	/**
	 * This is the real video stream which can be produced by a producer and
	 * assigned to a scheduled node of the consumer inside BCR. This class has
	 * stream spead and quality variables.
	 * 
	 */
	private double bitsPerSec;

	public Stream(double bitsPerSec) {
		this.bitsPerSec = bitsPerSec;
	}

	public double getBitsPerSec() {
		return bitsPerSec;
	}

	public void setBitsPerSec(double bitsPerSec) {
		this.bitsPerSec = bitsPerSec;
	}

}
