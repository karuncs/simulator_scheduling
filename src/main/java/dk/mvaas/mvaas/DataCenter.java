package dk.mvaas.mvaas;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @authors: Paul & Karun
 *
 */
public class DataCenter extends Consumer {
	/**
	 * A DataCenter class has the following elements: a DC or a BCR or both,
	 * Center manager, associated producers and users. This class can
	 * create/add producers, consumers(DCs or BCRs), no of BCRs in DCS and users.
	 */

	private ArrayList<Consumer> consumers;
	private ArrayList<Producer> producers;
	private ArrayList<User> users;
	private CenterManager manager;
	private BasicConsumer bc;
	private long freeSpace = 0L;
	Long freeResource = 0L;

	public DataCenter(String id, DataCenter parent) {
		super(id, parent);
		consumers = new ArrayList<Consumer>();
		producers = new ArrayList<Producer>();
		users = new ArrayList<User>();
		manager = new CenterManager(this);

	}

	public DataCenter(String id, DataCenter parent, int noOfProducers) {
		super(id, parent);
		consumers = new ArrayList<Consumer>();
		producers = new ArrayList<Producer>();
		users = new ArrayList<User>();
		manager = new CenterManager(this);
		createProducers(noOfProducers);

	}

	public ArrayList<Producer> getProducers() {
		return producers;
	}

	/**
	 * @param noOfProducers
	 */
	private void createProducers(int noOfProducers) {
		for (int i = 0; i < noOfProducers; i++) {
			Producer prod = new Producer(this);
			producers.add(prod);
			AssignmentRequest assReq = new AssignmentRequest(prod, this);
			this.manager.handleRequest(assReq);
			// createStoReq(prod);

		}
	}

	public void addProducer(Producer prod) {
		producers.add(prod);
		AssignmentRequest assReq = new AssignmentRequest(prod, this);
		this.manager.handleRequest(assReq);
	}

	public void addUser(User user) {
		users.add(user);
	}

	/**
	 * @param dc
	 */
	public void addConsumer(Consumer dc) {
		consumers.add(dc);
		AssignmentRequest assReq = new AssignmentRequest(dc, this);
		this.manager.handleRequest(assReq);
		// System.out.println(" adding "+dc.id+ " to "+id);

	}

	public ArrayList<Consumer> getConsumers() {
		return consumers;
	}

	public int getNoOfBCsinDC() {
		int count = 0;
		for (Consumer con : this.getConsumers()) {
			if (con instanceof BasicConsumer) {
				count += 1;
			}
		}
		return count;
	}

	public long calcFreeCapacityOfDC() {
		freeSpace = 0L;
		ArrayList<Consumer> childrens = this.getConsumers();
		// System.out.println("Childrens Size : " + childrens.size());
		for (Consumer child : childrens) {
			// System.out.println("freeSpace : " + freeSpace);
			if (child instanceof BasicConsumer) {
				freeSpace += ((BasicConsumerRing) child)
						.getTotalDiskSpaceOfBC();
				// System.out.println("DC " + freeSpace);
			} else if (child instanceof DataCenter) {
				freeSpace += ((DataCenter) child).calcFreeCapacityOfDC();
			}
		}

		return freeSpace;
	}

	public String toString() {
		return (id + "  children = " + consumers.size() + " prod = "
				+ producers.size() + " users = " + users.size());
	}

	public CenterManager getManager() {
		return manager;
	}

}
