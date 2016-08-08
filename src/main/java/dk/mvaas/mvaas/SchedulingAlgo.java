package dk.mvaas.mvaas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

/**
 * @authors Karun and Paul
 * 
 */
public class SchedulingAlgo {
	/**
	 * It's triggered initial scheduling from Simdriver. The CM of each DC
	 * collects a batch of requests and executes the scheduling algorithm to
	 * schedule the batch. A batch can have a single storage request. This class
	 * can decide where the storage requests can be scheduled inside a DC
	 * because the DC can have no.of children and the children might be DCs
	 * and/or BCRs. The algorithm works for both cases, for instance, scheduling
	 * on DC with schedulingOnDC() and scheduling on BC with
	 * loadSchedulingInRing() methods. This class throws scheduling report with
	 * handled batches and un-handled batches, if any.
	 */
	private Producer prod;
	private SchedulingReport sReport;

	private RingNode startNode;
	private ReportInfo ScheduledStoReportInfo;
	int i = 0;
	static int bcId = 0, BC_index = 0;
	public static ArrayList<StorageRequest> unScheduledReqSList = new ArrayList<StorageRequest>();
	public ArrayList<Consumer> selectedCons = new ArrayList<Consumer>();
	long capacityNow;
	HashMap<Consumer, Long> spaceOfDC = new HashMap<Consumer, Long>();
	HashMap<Consumer, Long> childsOfDC = new HashMap<Consumer, Long>();

	public SchedulingAlgo(Consumer cons, ArrayList<StorageRequest> stoReq) {
		// runs scheduling based on consumer type
		Debug.debug(0, "-------- SA start -------");
		sReport = new SchedulingReport();
		if (cons instanceof BasicConsumerRing) {// if consumer is BC
			this.startNode = ((BasicConsumerRing) cons)
					.getNodeWithMaxCapacity();
			loadSchedulingInRing(stoReq, startNode, (BasicConsumerRing) cons);
		} else if (cons instanceof DataCenter) {// if consumer is DC
			schedulingOnDC(cons, stoReq);
			// if bCons has many free, schedule there
			// otherwise go to the lower DC with max free (TODO)
			// Note this gives priority ti Basic consumers
			// This has to be justified
		}
		Debug.debug(0, "-------- SA end -------");

	}

	// Scheduling on DC
	private void schedulingOnDC(Consumer cons, ArrayList<StorageRequest> stoReq) {
		Debug.debug(0, "loadScheduling on DC called " + cons.id);
		long totalFreeSpace = 0L;
		DataCenter currentDC = (DataCenter) cons;
		ArrayList<Consumer> children = currentDC.getConsumers();
		System.out.print("Children ");
		for (int i = 0; i < children.size(); i++) {
			System.out.print(children.get(i).id + "  ");
		}
		System.out.println();
		for (Consumer cn : children) {
			if (cn instanceof DataCenter) {
				spaceOfDC.put(cn, ((DataCenter) cn).calcFreeCapacityOfDC());
				totalFreeSpace += ((DataCenter) cn).calcFreeCapacityOfDC();
				// Debug.debug(
				// 0,
				// cn.id + " has total space :"
				// + ((DataCenter) cn).calcFreeCapacityOfDC());
			} else {// bc
				spaceOfDC.put(cn,
						((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
				totalFreeSpace += ((BasicConsumerRing) cn)
						.getTotalDiskSpaceOfBC();
				// Debug.debug(0, cn.id + " has total space :"
				// + ((BasicConsumerRing) cn).getTotalFreeResourcesOfBC());
			}
		}
		// Sort childs of DC in descending order
		Map<Consumer, Long> map = sortByValues(spaceOfDC);
		// System.out.println("the sorting oprder of DCS are " + map);
		// System.out.println("the sorting oprder of DCS Size " +
		// stoReq.size());
		Iterator iterator2 = map.entrySet().iterator();
		int i = 0;
		while (iterator2.hasNext()) {
			long tempDemand = 0L;
			Map.Entry me2 = (Map.Entry) iterator2.next();
			Consumer cn = (Consumer) me2.getKey();
			Long val = (Long) me2.getValue();
			ArrayList<StorageRequest> stoReqToCon = new ArrayList<StorageRequest>();
			if (totalFreeSpace > 0 && val > 0) {

				for (int j = i; j < stoReq.size(); j++) {
					tempDemand += ((Producer) stoReq.get(j).getComp())
							.getdemand();
					// Partition the collected sto req and send to the DC
					if (tempDemand <= val) {
						stoReqToCon.add(stoReq.get(j));
						i++;
					} else {
						j = stoReq.size();
					}
					// System.out.println(" temp Job demand : " +
					// tempDemand);

				}

			}
			// if (stoReqToCon.size() > 0) {
			if (cn instanceof DataCenter) {
				selectedCons = getConsFromDCForScheduling(cn);
				for (int index = 0; index < selectedCons.size(); index++) {

					if (selectedCons.get(index) instanceof BasicConsumer) {
						BasicConsumerRing bCons = (BasicConsumerRing) selectedCons
								.get(index);
						if (bCons != null) {

							this.startNode = bCons.getNodeWithMaxCapacity();
							loadSchedulingInRing(stoReqToCon,
									startNode.leftNeighbor,
									(BasicConsumerRing) bCons);
						}

					}
				}

			} else {

				this.startNode = ((BasicConsumerRing) cn)
						.getNodeWithMaxCapacity();
				loadSchedulingInRing(stoReqToCon, startNode,
						(BasicConsumerRing) cn);
			}
			// }
		}
	}

	private void loadSchedulingInRing(ArrayList<StorageRequest> StoReq,
			RingNode currNode, BasicConsumerRing bc) {

		Debug.debug(0, "loadSchedulingInRing called " + bc.id + "  "
				+ currNode.id);
		ArrayList<StorageRequest> leftList = new ArrayList<StorageRequest>();
		// Constants.nodeSize[
		capacityNow = currNode.elements.nodeAvailableCapacity;
		i = 0;
		long totalDemand = 0L;

		System.out.println(" sto size :" + StoReq.size());
		while (i < StoReq.size()
				&& capacityNow > ((Producer) StoReq.get(i).comp).getdemand()) {
			prod = ((Producer) StoReq.get(i).comp);

			// Debug.debug(2, "------ i-----\n" + i + " StoReq size: " +
			// StoReq.size());
			currNode.addReservedCapacity(((Producer) StoReq.get(i).comp)
					.getdemand());
			capacityNow -= ((Producer) StoReq.get(i).comp).getdemand();
			totalDemand += ((Producer) StoReq.get(i).comp).getdemand();
			Routing route = new Routing(currNode);
			ScheduledStoReportInfo = new ReportInfo(prod,
					currNode.getBasicConsumer(), new Stream(
							((Math.random() * 12) + 1) * 1.1), route);
			sReport.addInfo(ScheduledStoReportInfo);
			i++;

		}
		while (i < StoReq.size() - 1) {
			leftList.add(StoReq.get(i));
			i++;
		}
		if (i == StoReq.size() - 1) {
			leftList.add(StoReq.get(i));
		}

		if (leftList.size() > 0 && !currNode.leftNeighbor.equals(startNode)) {
			loadSchedulingInRing(leftList, currNode.leftNeighbor, bc);
			if (leftList.size() > 0) {
				// return leftList to parent
				for (StorageRequest sr : leftList)
					sReport.addUnhandledReq(sr);
			}
		}
	}

	// recursive function to get childs of DC
	private ArrayList<Consumer> getConsFromDCForScheduling(Consumer cons) {
		ArrayList<Consumer> consu = null;// new ArrayList<Consumer>();
		childsOfDC.clear();
		if (cons instanceof DataCenter) {
			DataCenter currentDC = (DataCenter) cons;
			consu = currentDC.getConsumers();
			long totalspace = 0L;
			for (Consumer cn : consu) {
				if (cn instanceof DataCenter) {
					childsOfDC
							.put(cn, ((DataCenter) cn).calcFreeCapacityOfDC());
					totalspace += ((DataCenter) cn).calcFreeCapacityOfDC();
					Debug.debug(0, " ----- " + cn.id + " has total space :"
							+ ((DataCenter) cn).calcFreeCapacityOfDC());
				} else {// bc
					childsOfDC.put(cn,
							((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
					totalspace += ((BasicConsumerRing) cn)
							.getTotalDiskSpaceOfBC();
					Debug.debug(0, " ----- " + cn.id + " has total space :"
							+ ((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
				}
			}

		}
		for (Consumer cn : consu) {
			if (cn instanceof DataCenter) {
				getConsFromDCForScheduling(cn);
			}
		}
		return consu;
	}

	// get scheduling report
	public SchedulingReport getReport() {
		return sReport;
	}

	// Sort Hashmap in descending order
	private HashMap sortByValues(HashMap map) {
		LinkedList list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

}
