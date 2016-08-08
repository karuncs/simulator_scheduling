/**
 * 
 */
package dk.mvaas.mvaas;
import java.util.Random;

/**
 * @authors Karun and Paul
 * 
 */
public class FailuresDetection implements Runnable {

	/**
	 * This class detects failures of all the system components by running
	 * health check request.
	 * 
	 */

	long waitingTime = 0L;

	private DataCenter myDC;

	public FailuresDetection(DataCenter dc) {
		this.myDC = dc;
	}

	public void run() {
		boolean flag = true;
		while (flag) {
			waitingTime += 10;

			if (waitingTime == 15000 || waitingTime == 25000
					|| waitingTime == 150000 || waitingTime == 50000
					|| waitingTime == 75000 || waitingTime == 100000
					|| waitingTime == 65000) {
				// flag=false;
				HealthCheckRequest HCR = new HealthCheckRequest(myDC
						.getProducers().get(
								new Random()
										.nextInt(myDC.getProducers().size())),
						myDC);
				myDC.getManager().handleRequest(HCR);
				;
			} else {// System.out.println(" health check req --> " +myDC.getID()
					// + " value :" + waitingTime);
					//
			}
		}
	}
}
