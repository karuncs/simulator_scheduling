package dk.mvaas.mvaas;

/**
 * @author Paul
 *
 */

import java.util.ArrayList;

public class Consumer extends MVaaSComponent {
	/**
	 * A consumer can be a DC or BCR, so this class maintains the ids of them
	 * where it got from super class (MVaaSComponent.java)
	 * 
	 */

	protected DataCenter parent;

	/**
	 * @param id
	 */
	public Consumer(String id, DataCenter parent) {
		this.id = id;
		this.parent = parent;

	}

	/**
	 * @return
	 */
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public DataCenter getParent() {
		return parent;
	}

}
