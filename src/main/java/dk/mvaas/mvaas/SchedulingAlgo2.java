package dk.mvaas.mvaas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @authors Karun and Paul
 * Greedy Scheduling Algorithm
 * 
 */

public class SchedulingAlgo2 {
	private Producer prod;
	private SchedulingReport sReport;

	private RingNode startNode;
	private ReportInfo ScheduledStoReportInfo;
	int i = 0;

	static int bcId = 0, BC_index = 0;
	public static ArrayList<StorageRequest> unScheduledReqSList = new ArrayList<StorageRequest>();
	public ArrayList<Consumer> selectedCons = new ArrayList<Consumer>();
	long Capacity;
	HashMap<Consumer, Long> spaceOfDC = new HashMap<Consumer, Long>();
	HashMap<Consumer, Long> childsOfDC = new HashMap<Consumer, Long>();

	public SchedulingAlgo2(Consumer cons, ArrayList<StorageRequest> stoReq) {
		sReport = new SchedulingReport();
		if (cons instanceof BasicConsumerRing) {
			this.startNode = ((BasicConsumerRing) cons)
					.getNodeWithMaxCapacity();
			loadSchedulingInRing(stoReq, startNode);
		} else if (cons instanceof DataCenter) {
			// calcRatiosOfeachDC(cons, stoReq);
			schedulingOnDC(cons, stoReq);

			// if bCons has many free, schedule there
			// otherwise go to the lower DC with max free (TODO)
			// Note this gives priority ti Basic consumers
			// This hat to be justified
		}

	}

	private void schedulingOnDC(Consumer cons, ArrayList<StorageRequest> stoReq) {
		Debug.debug(0, "loadScheduling on DC called " + cons.id);
		long totalFreeSpace = 0L;
		DataCenter currentDC = (DataCenter) cons;
		ArrayList<Consumer> children = currentDC.getConsumers();
		for (Consumer cn : children) {
			if (cn instanceof DataCenter) {
				spaceOfDC.put(cn, ((DataCenter) cn).calcFreeCapacityOfDC());
				totalFreeSpace += ((DataCenter) cn).calcFreeCapacityOfDC();
				Debug.debug(
						0,
						cn.id + " has total space :"
								+ ((DataCenter) cn).calcFreeCapacityOfDC());
			} else {// bc
				spaceOfDC.put(cn,
						((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
				totalFreeSpace += ((BasicConsumerRing) cn)
						.getTotalDiskSpaceOfBC();
				Debug.debug(0, cn.id + " has total capacity :"
						+ ((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
			}
		}
		long avg = totalFreeSpace / (long) children.size();
		// Debug.debug(4, " Avg capacity" + avg);
		// Greedy consumer selection for scheduling based on Avg Score
		for (Consumer cn : children) {
			if (avg <= spaceOfDC.get(cn)) {
				System.out.println(" Selected DCs : " + cn.id);
			} else {
				spaceOfDC.remove(cn);
			}
		}

		Map<Consumer, Long> map = sortByValues(spaceOfDC);
		System.out.println("the sorting oprder of DCS are " + map);
		Iterator iterator2 = map.entrySet().iterator();
		int i = 0;
		while (iterator2.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator2.next();
			Consumer cn = (Consumer) me2.getKey();
			Long val = (Long) me2.getValue();
			ArrayList<StorageRequest> stoReqToCon = new ArrayList<StorageRequest>();

			if (totalFreeSpace > 0 && val >= 0) {

				long tempDemand = 0L;

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
							loadSchedulingInRing(stoReqToCon, startNode);
						}

					}
				}
			} else {

				this.startNode = ((BasicConsumerRing) cn)
						.getNodeWithMaxCapacity();
				loadSchedulingInRing(stoReqToCon, startNode);
			}
		}
		// }

	}

	private void loadSchedulingInRing(ArrayList<StorageRequest> StoReq,
			RingNode currNode) {
		Debug.debug(0,
				"loadSchedulingInRing called " + currNode.getBasicConsumer().id
						+ "  " + currNode.id);
		ArrayList<StorageRequest> leftList = new ArrayList<StorageRequest>();
		// Constants.nodeSize[
		Capacity = currNode.elements.nodeAvailableCapacity;

		i = 0;
		long totalDemand = 0L;
		// Debug.debug(4, "------ i-----\n" + i + " StoReq size: " +
		// StoReq.size());

		while (i < StoReq.size() && Capacity > ((Producer) StoReq.get(i).comp).getdemand()) {
			prod = ((Producer) StoReq.get(i).comp);
			
				Debug.debug(4, "------ i-----\n" + i + " StoReq size: "
						+ StoReq.size());
				currNode.addReservedCapacity(((Producer) StoReq.get(i).comp)
						.getdemand());
				Capacity -= ((Producer) StoReq.get(i).comp).getdemand();
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
			loadSchedulingInRing(leftList, currNode.leftNeighbor);
		} else {
			if (leftList.size() > 0) {
				// return leftList to parent
				for (StorageRequest sr : leftList)
					sReport.addUnhandledReq(sr);
			}
		}

	}

	private ArrayList<Consumer> getConsFromDCForScheduling(Consumer cons) {
		ArrayList<Consumer> consu = new ArrayList<Consumer>();
		childsOfDC.clear();

		if (cons instanceof DataCenter) {
			DataCenter currentDC = (DataCenter) cons;
			ArrayList<Consumer> children = currentDC.getConsumers();
			long totalspace = 0L;
			for (Consumer cn : children) {
				if (cn instanceof DataCenter) {
					childsOfDC
							.put(cn, ((DataCenter) cn).calcFreeCapacityOfDC());
					totalspace += ((DataCenter) cn).calcFreeCapacityOfDC();
					Debug.debug(0, " ----- " + cn.id + " has total capacity :"
							+ ((DataCenter) cn).calcFreeCapacityOfDC());
				} else {// bc
					childsOfDC.put(cn,
							((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
					totalspace += ((BasicConsumerRing) cn)
							.getTotalDiskSpaceOfBC();
					Debug.debug(0, " ----- " + cn.id + " has total capacity :"
							+ ((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
				}
			}
			long avg = totalspace / (long) children.size();
			Debug.debug(4, "   ------------ From Sch Avg capacity" + avg
					+ " total childs " + childsOfDC.size());

			for (Consumer cn : children) {
				if (avg <= childsOfDC.get(cn)) {
					consu.add(cn);
					Debug.debug(4, "   ------------ Selected cns " + cn.id);
				} else {
					childsOfDC.remove(cn);

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

	// private Consumer getConWithMaxSpaceFromEachDC(Consumer cons) {
	// Consumer consu = null;
	// if (cons instanceof DataCenter) {
	// DataCenter currentDC = (DataCenter) cons;
	// Consumer cn = currentDC.getChildWithMaxFreeResources();
	// if (cn instanceof DataCenter) {
	// DataCenter currentDC1 = (DataCenter) consu;
	// getConWithMaxSpaceFromEachDC(consu);
	// } else {
	// consu = cn;
	// }
	// ArrayList<Consumer> children = currentDC.getConsumers();
	//
	// for (int i = 0; i < children.size(); i++) {
	// getConWithMaxSpaceFromEachDC(children.get(i));
	// }
	// } else {
	//
	// }
	// return consu;
	// }

	public SchedulingReport getReport() {
		return sReport;
	}

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
