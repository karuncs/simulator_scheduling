package dk.mvaas.mvaas;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * @authors Paul and Karun
 * 
 */

public class Node implements Runnable {
	/**
	 * A node consumes real video stream. To get the video stream from producer
	 * a connection is established between a producer and a node by
	 * corresponding CM. Every Node maintains id and list of video streams which
	 * are consuming its storage space. The Node class implements Runnable
	 * threads, so it has run method. The run method works like daemon. This
	 * class can print streaming data in local computer. The node class
	 * maintains each node consumed storage space and rest.
	 * 
	 * If a node has no storage space to consume streams then the node stops
	 * consuming and throws a message like Node has no storage space. In this
	 * case the streams which are on storage full node have redirect to its
	 * neighbours.i.e., re-scheduling inside BCRs. The Node is a super class of
	 * RingNode class
	 * 
	 */

	public static int nextId = 0;
	private static DecimalFormat df3 = new DecimalFormat("000");
	NodeElements elements;
	private Timer timer = new Timer(); // TimerTask processStreams;
	double sumOfSteamValues = 0;
	protected Vector<Stream> streams;
	protected String id;
	private long uptime;
	int noOfTasks = 0;
	private RingNode currentNode, startNode;

	public Node() {
		super();
		this.elements = new NodeElements(Constants.DEFAULT_Node_PARAMSET);
		this.id = "N" + nextId++;
		this.uptime = 0L;
	}

	public Node(NodeElements elements) {
		super();
		this.elements = elements;
		this.id = "N" + nextId++;
		this.uptime = 0L;
	}

	// Start Streaming
	public void addStream(Stream stream) {
		if (this.streams == null) {
			streams = new Vector<Stream>();
		}
		this.streams.addElement(stream);
		this.startNode = ((RingNode) this);
		currentNode = ((RingNode) this);
	}

	// Start Streaming
	public void addStream(RingNode rNode, Vector<Stream> stream) {
		if (this.streams == null) {
			streams = new Vector<Stream>();
		}
		this.streams = stream;
		this.startNode = rNode;
		// this.start();
		currentNode = ((RingNode) this);
	}

	public void run() {
		boolean flag = true;
		long usedSpaceOnNode = 0L;
		while (flag) {
			double availableResourcesOfNode = this.elements.nodeAvailableDiskSpace;
			writingStreamingDataInReport(id, "Streaming received on Node :"
					+ id + " , initial available space  :"
					+ availableResourcesOfNode);

			for (Stream value : streams) {
				sumOfSteamValues += value.getBitsPerSec();
			}

			writingStreamingDataInReport(id, "Streaming Speed on Node " + id
					+ " : " + sumOfSteamValues);
			uptime = 5L;// interval time

			writingStreamingDataInReport(id, "Node " + this.id + " free space "
					+ availableResourcesOfNode);
			noOfTasks++;
			writingStreamingDataInReport(id, "Node " + this.id
					+ " no of tasks " + noOfTasks);
			long bufferData = (long) (uptime * sumOfSteamValues);
			if (bufferData > elements.nodeAvailableDiskSpace
					|| usedSpaceOnNode >= availableResourcesOfNode) {
				flag = false;
				writingStreamingDataInReport(id, "Streaming Stopped on Node "
						+ id + "  because node is full");
				Debug.debug(0, "Streaming Stopped on Node " + id
						+ "  because node is full");
				// disk space is full..then node has no capacity
this.elements.nodeAvailableCapacity=0L;
				// rescheduling inside BCR.. send to bcr
				if (this.startNode != currentNode) {
					RingNode leftNode = currentNode.getLeftNeighbor();// .addStream(streams.get(0));
					leftNode.addStream(this.startNode, streams);
					Thread th = new Thread(leftNode);
					th.start();
					th.yield();
				} else {
					flag = false;
//					Debug.debug(0, currentNode.getBasicConsumer().id
//							+ "   is full");
				}
			}

			else {
				addUsedDiskSpace(bufferData);
				usedSpaceOnNode += bufferData;
				writingStreamingDataInReport(id, "usedSpaceOnNode on Node  "
						+ id + " : " + usedSpaceOnNode + " ("
						+ sumOfSteamValues + ")");
				writingStreamingDataInReport(id, "space available on Node  "
						+ id + " : " + elements.nodeAvailableDiskSpace);
			}

		}
	}

	public void addStream(StorageRequest sReq) {
		this.streams.add(new Stream(sReq.getElements().getBitsPerSecond()));
	}

	public void addUsedDiskSpace(long used) {
		if (this.elements.nodeAvailableDiskSpace - used >= 0) {
			this.elements.nodeAvailableDiskSpace -= used;
			this.elements.nodeUsedDiskSpace += used;
		} else {
			System.out.println(" dif " + this.elements.nodeAvailableDiskSpace);
		}

	}

	public void addReservedCapacity(long reserved) {
		if (this.elements.nodeAvailableCapacity - reserved >= 0) {
			this.elements.nodeAvailableCapacity -= reserved;
			this.elements.nodeReservedCapacity += reserved;
		} else {
			System.out.println(" dif " + this.elements.nodeAvailableDiskSpace);
		}
	}

	// stop streaming
	public void stopNode() {
		// stop timer
		timer.cancel();
		timer.purge();

	}

	public String toStringX() {

		return "N-" + id + df3.format(this.id) + "  totalDisk = "
				+ this.elements.nodeTotalDiskSpace + "  freeDisk = "
				+ this.elements.nodeAvailableDiskSpace;
	}

	public void writingStreamingDataInReport(String NodeID, String info) {
		try {
			File fil;

			fil = new File("StreamingData/" + NodeID + ".txt");
			FileWriter fw = new FileWriter(fil, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(info);
			bw.newLine();

			bw.flush();
			bw.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
