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
public class SchedulingAlgo_PF {
	
	private SchedulingReport sReport;

	
	public SchedulingAlgo_PF(Consumer cons, ArrayList<StorageRequest> stoReq) {
		// runs scheduling based on consumer type
		Debug.debug(0,"-------- SA PF start -------");
		sReport = new SchedulingReport();
		if (cons instanceof BasicConsumerRing) {// if consumer is BC
			RingNode startNode = ((BasicConsumerRing) cons)
					.getNodeWithMaxCapacity();
			loadSchedulingInRing(stoReq, startNode,startNode,(BasicConsumerRing) cons);
		} else if (cons instanceof DataCenter) {// if consumer is DC
			schedulingOnDC(cons, stoReq);
			
		}
		Debug.debug(0,"-------- SA PF end -------");
		
	}

	// Scheduling on DC
	private void schedulingOnDC(Consumer cons, ArrayList<StorageRequest> stoReq) {
		Debug.debug(0, "loadScheduling on DC called " + cons.id);
		DataCenter currentDC = (DataCenter) cons;
		ArrayList<Consumer> children = currentDC.getConsumers();
		System.out.print("Children ");
		for (int i = 0; i < children.size(); i++) {
			System.out.print(children.get(i).id+"  ");
		}
		System.out.println();
		// SORT children by free space decreasingly
		for (Consumer cn : children) {
			if (cn instanceof BasicConsumerRing) {// if consumer is BC
				RingNode startNode = ((BasicConsumerRing) cn)
						.getNodeWithMaxCapacity();
				loadSchedulingInRing(stoReq, startNode,startNode,(BasicConsumerRing) cn);
			} else if (cons instanceof DataCenter) {// if consumer is DC
				//if(cons==null)
				//System.exit(0);
				schedulingOnDC(cn, stoReq);
				
				
			}
		}
	 }

	private void loadSchedulingInRing(ArrayList<StorageRequest> StoReq,
			RingNode currNode, RingNode startNode, BasicConsumerRing bc) {
           
		Debug.debug(0, "loadSchedulingInRing called "+ bc.id+"  "+ currNode.id);
		ArrayList<StorageRequest> leftList = new ArrayList<StorageRequest>();
		// Constants.nodeSize[
		long capacityNow = currNode.elements.nodeAvailableCapacity;
		int i = 0;
		long totalDemand = 0L;
		
		Producer prod = ((Producer) StoReq.get(i).comp);
		while (i < StoReq.size() && capacityNow > prod.getdemand()) {
			//Debug.debug(2, "------ i-----\n" + i + " StoReq size: " + StoReq.size());
			currNode.addReservedCapacity(((Producer) StoReq.get(i).comp)
					.getdemand());
			capacityNow -= ((Producer) StoReq.get(i).comp).getdemand();
			totalDemand += ((Producer) StoReq.get(i).comp).getdemand();
			Routing route = new Routing(currNode);
		    ReportInfo rinf = new ReportInfo(prod,
					currNode.getBasicConsumer(), new Stream(
							((Math.random() * 12) + 1) * 1.1), route);
			sReport.addInfo(rinf);
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
			loadSchedulingInRing(leftList, currNode.leftNeighbor,
					startNode, bc);
			if (leftList.size() > 0) {
				// return leftList to parent
				for (StorageRequest sr : leftList)
					sReport.addUnhandledReq(sr);
			}
		}
	}

	/**
	 * @return
	 */
	public SchedulingReport getReport() {
		// TODO Auto-generated method stub
		return this.sReport;
	}

	// recursive function to get childs of DC
/*	private ArrayList<Consumer> getConsFromDCForScheduling(Consumer cons) {
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
	}*/

}
