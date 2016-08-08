/**
 * 
 */
package dk.mvaas.mvaas;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author paul & Karun
 *
 */

public class SimDriver {
	/**
	 * This is kind of external component The whole system build and simulations
	 * starts from here
	 * 
	 */

	public static void main(String[] args) {
		final String systemSmall = "DC,0,300;(DC,1,200;(BCR,5,1;BCR,6,10;)BCR,13,13;);";
		final String systemSmall2 = "DC,0,3;(DC,1,200;(BCR,5,1;BCR,6,0;)BCR,13,23;);";
		final String systemMainDoc = "DC,0,1;(DC,1,100;(BCR,5,8;BCR,6,8;DC,7,100;(BCR,13,12;BCR,14,10;)BCR,8,10;)DC,2,100;(BCR,9,5;BCR,10,5;)BCR,3,8;DC,4,100;(DC,11,100;(BCR,15,8;BCR,16,4;)DC,12,100;(BCR,17,10;)))";
		
		// String topo = Constants.systemSmall2;//
		// systemMainDoc//systemSmall2//systemSmall
		SystemBuilder sb = new SystemBuilder(systemSmall2);

		Debug.debug(0, "------ System build -----\n" + listSystem(sb.getRoot())
				+ "-----------------");
		// at this point the system is build
		// Start simulation
		// 1. do initial scheduling

		Debug.debug(0, "<----------------------------->");
		initScheduling(sb.getRoot());
		// all connections are made

		// 2. Start the actual simulation
		// System.exit(0);
	}

	/**
	 * @param root
	 * @param string
	 * @return
	 */

	// Returning System structure
	private static String listSystem(Consumer cons) {
		StringBuilder sb = new StringBuilder("");
		listSystemRec(cons, " ", sb);
		return sb.toString();
	}

	/**
	 * @param root
	 */
	private static void listSystemRec(Consumer cons, String space,
			StringBuilder sb) {
		sb.append(space + cons.toString() + " \n");
		if (cons instanceof DataCenter) {
			ArrayList<Consumer> children = ((DataCenter) cons).getConsumers();
			for (int i = 0; i < children.size(); i++) {
				listSystemRec(children.get(i), space + "  ", sb);
			}
		}
	}

	/**
	 * @param root
	 */
	// Initial Scheduling
	private static void initScheduling(Consumer cons) {
		if (cons instanceof DataCenter) {
			// ((DataCenter) cons).getManager().initializeStorage();
			Producer currentProd;
			DataCenter currentDC = (DataCenter) cons;
			for (int i = 0; i < ((DataCenter) cons).getProducers().size(); i++) {
				// Generate Storage request demand
				long demand = ((long) (Math.random() * 12) + 1) * 100L;
				currentProd = currentDC.getProducers().get(i);
				// Create Storage Request
				StorageRequest sReq = new StorageRequest(currentProd, currentDC);
				currentProd.setDemand(demand);
				currentDC.getManager().scheAlgo(sReq);// Passing Sto Req to CM
			}
			ArrayList<Consumer> children = currentDC.getConsumers();
			for (int i = 0; i < children.size(); i++) {
				initScheduling(children.get(i));
			}

		}

	}

}
