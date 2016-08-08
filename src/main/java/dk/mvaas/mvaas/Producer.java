package dk.mvaas.mvaas;
/**
 * @authors Paul and Karun  
 * 
 */
public class Producer extends MVaaSComponent {
	/**
	 * A producer has to produce video data and send it to consumers 
	 * 
	 */
	public static int nextProducerID = 0;
	DataCenter dc;
	// Demand data
	private long demand;

	public Producer(DataCenter dc) {
		super();
		this.dc = dc;
		id = "P" + nextProducerID;
		nextProducerID++;
	}

	public DataCenter getDC() {
		return this.dc;
	}

	public void setDemand(long demand) {
		this.demand = demand;
	}

	public long getdemand() {
		return demand;
	}


}
