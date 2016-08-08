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
 * Proportional scheduling Algorithm
 */
public class SchedulingAlgo3 {

	private Producer prod;
	private SchedulingReport sReport;
	// private ArrayList<StorageRequest> ScheduledReqS = new
	// ArrayList<StorageRequest>();
	private RingNode startNode;
	private ReportInfo ScheduledStoReportInfo;
	int i = 0;
	// private String = "";// leftNode_id = 0;
	static int bcId = 0, BC_index = 0;
	public static ArrayList<StorageRequest> unScheduledReqSList = new ArrayList<StorageRequest>();
	public ArrayList<Consumer> selectedCons = new ArrayList<Consumer>();
	// RingNode leftNode;
	long Capacity;
	HashMap<Consumer, Long> spaceOfDC = new HashMap<Consumer, Long>();
	HashMap<Consumer, Long> childsOfDC = new HashMap<Consumer, Long>();

	public SchedulingAlgo3(Consumer cons,
			ArrayList<StorageRequest> stoReq) {
		sReport = new SchedulingReport();
		if (cons instanceof BasicConsumerRing) {
			this.startNode = ((BasicConsumerRing) cons)
					.getNodeWithMaxCapacity();
			loadSchedulingInRing(stoReq, startNode);
		} else if (cons instanceof DataCenter) {
			calcRatiosOfeachDC(cons, stoReq);

			// if bCons has many free, schedule there
			// otherwise go to the lower DC with max free (TODO)
			// Note this gives priority ti Basic consumers
			// This hat to be justified
		}

	}

	private void calcRatiosOfeachDC(Consumer cons,
			ArrayList<StorageRequest> stoReq) {
		Debug.debug(0, "loadScheduling on DC called " + cons.id);
		long totalFreeSpace = 0L;
		long totaldemand = 0L;

		for (int i = 0; i < stoReq.size(); i++) {
			totaldemand += ((Producer) stoReq.get(i).getComp()).getdemand();
		}
		// System.out.println("jobs totaldemand " + totaldemand + ", tot jobs: "
		// + stoReq.size());
		if (cons instanceof DataCenter) {
			DataCenter currentDC = (DataCenter) cons;
			// spaceOfDC.put(currentDC.id, currentDC.getTotalFreeSpaceOfDC());
			ArrayList<Consumer> children = currentDC.getConsumers();
			for (Consumer cn : children) {
				if (cn instanceof DataCenter) {
					spaceOfDC.put(cn, ((DataCenter) cn).calcFreeCapacityOfDC());
					totalFreeSpace += ((DataCenter) cn).calcFreeCapacityOfDC();
					// System.out.println(cn.id + " has total space :"
					// + ((DataCenter) cn).calcFreeCapacityOfDC());
				} else {// bc
					spaceOfDC.put(cn,
							((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
					totalFreeSpace += ((BasicConsumerRing) cn)
							.getTotalDiskSpaceOfBC();
					// System.out.println(cn.id + " has total space :"
					// + ((BasicConsumerRing) cn).getTotalDiskSpaceOfBC());
				}
			}
			int j = 0;
			System.out.println("total space of all childs:" + totalFreeSpace);

			Map<Consumer, Long> map = sortByValues(spaceOfDC);
			System.out.println("the sorting oprder of DCS are " + map);
			Iterator iterator2 = map.entrySet().iterator();
			while (iterator2.hasNext()) {

				Map.Entry me2 = (Map.Entry) iterator2.next();
				Consumer cn = (Consumer) me2.getKey();
				Long val = (Long) me2.getValue();
				ArrayList<StorageRequest> stoReqToCon = new ArrayList<StorageRequest>();
				if (totalFreeSpace > 0 && val >= 0) {

					int percent = (int) (val * 100 / (int) totalFreeSpace);
					long jobsTobeAssign = ((long) (totaldemand / 100) * percent);
					// System.out.println(cn.id + " has total ratio :" + percent
					// + " req Job demand : " + jobsTobeAssign);

					long tempDemand = 0L;

					for (int i = j; i < stoReq.size(); i++) {
						stoReqToCon.add(stoReq.get(i));
						tempDemand += ((Producer) stoReq.get(i).getComp())
								.getdemand();
						// System.out.println(" temp Job demand : " +
						// tempDemand);
						if ((tempDemand - jobsTobeAssign) >= 0
								|| i == stoReq.size() - 1) {
							// System.out.println(" req Job demand : "
							// + jobsTobeAssign);
							// stoReqToCon.add(stoReq.get(i));
							i = stoReq.size();
							// System.out.println(cn.id + " has received :"
							// + tempDemand);
						}
						j++;
					}
					// if (stoReqToCon.size() > 0) {
					if (cn instanceof DataCenter) {
						selectedCons = getConsFromDCForScheduling(cn);
						for (int index = 0; index < selectedCons.size(); index++) {
							if (selectedCons.get(index) instanceof BasicConsumer) {
								BasicConsumerRing bCons = (BasicConsumerRing) selectedCons
										.get(index);
								if (bCons != null) {
									this.startNode = bCons
											.getNodeWithMaxCapacity();
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
			}
			// }

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
			long avg = totalspace / (long) children.size();
			System.out.println("   ------------ From Sch Avg Space" + avg
					+ " total childs " + childsOfDC.size());

			for (Consumer cn : children) {
				if (avg <= childsOfDC.get(cn)) {
					consu.add(cn);
					System.out.println("   ------------ Selected cns " + cn.id);
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
		//Debug.debug(2, "------ i-----\n" + i + " StoReq size: " + StoReq.size());

		while (i < StoReq.size() && Capacity > ((Producer) StoReq.get(i).comp).getdemand()) {
			prod = ((Producer) StoReq.get(i).comp);
		 
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
