package dk.mvaas.mvaas;

import java.util.LinkedList;

/**
 * @authors Paul and Karun
 * 
 */
public class BasicConsumer extends Consumer {
	/**
	 * BCR does not have a CM. It is a real storage facility in the system. This
	 * class maintains same unique id which it got from Super class
	 * (BasicConsumer.java <-- Consumer.java <-- MVaaSComponent.java).
	 */

	public BasicConsumer(String id, DataCenter parent) {
		super(id, parent);
	}
}
