package dk.mvaas.mvaas;

import java.util.Random;
import java.util.Vector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

/**
 * @authors Paul and Karun
 * 
 */
public class CenterManager {
	/**
	 * The Center manager is the heart of a DC, i.e., every DC has own Center
	 * manager to manager all tasks of the DC
	 * 
	 */

	DataCenter myDC;
	private ArrayList<StorageRequest> stoReqs;
	public ArrayList<StorageRequest> unScheduledStoReqList;
	private String ScheduleReportFileName = "";
	private int noOfStoReqInBatch = -1;
	private int StoReqIndex = 0;

	public CenterManager(DataCenter dc) {
		// System.out.println("*********   CM created " + NO++);
		this.myDC = dc;
		unScheduledStoReqList = new ArrayList<StorageRequest>();
		// sReport = new SchedulingReport(dc);
		// this.ScheduleReportFileName="";

	}

	// Handling all types of requests
	public void handleRequest(Request request) {
		if (request instanceof AssignmentRequest) {
			MVaaSComponent comp = ((AssignmentRequest) request).getComp();
			DataCenter dc = ((AssignmentRequest) request).getCenter();
			// DataCenter dc = ((AssignmentRequest) request).getCenter();
			// TODO check that it is the right DC
			// if (dc.equals(myDC)) {
			// // do something.
			// }
		} else if (request instanceof StorageRequest) {
			MVaaSComponent comp = ((StorageRequest) request).getComp();
			DataCenter dc = ((StorageRequest) request).getCenter();
			// TODO check that it is the right DC
			if (comp instanceof Producer) {
				// ***** Schedule and connect (request) Each request
				// (Possobility 1)
				if (dc.getConsumers().size() > 0) {// getNoOfBCsinDC() > 0) {//
													// if DC contains min 1 BC

					if (this.stoReqs == null) {
						this.stoReqs = new ArrayList<StorageRequest>();
					} else if (this.stoReqs.size() == 0) {
						this.noOfStoReqInBatch = -1;
					}
					if (this.noOfStoReqInBatch == -1) {
						this.noOfStoReqInBatch = new Random().nextInt(20) + 1;
					}

					stoReqs.add((StorageRequest) request);
					Debug.debug(
							0,"random no : " + noOfStoReqInBatch + " sto req size : " + stoReqs.size());
		
					if (stoReqs.size() == this.noOfStoReqInBatch
							|| stoReqs.size() == dc.getProducers().size()) {
						
						
						// Calling Scheduling Algorithm
						SchedulingAlgo scheduleAlgo = new SchedulingAlgo(dc,
								stoReqs);

						stoReqs.clear();
						// get scheduling report
						SchedulingReport sReport = scheduleAlgo.getReport();

						for (ReportInfo schReportInfo : sReport
								.getSchdulingInfo()) {
							ConnectionRequest conReq = new ConnectionRequest(
									schReportInfo.getProducer(),
									schReportInfo.getConsumer(),
									schReportInfo.getStream(),
									schReportInfo.getRouting());
							handleRequest(conReq);
						}
						// Report contains unsheduled storage reqs
						if (sReport.getUnhandledReqS().size() > 0) {
							DataCenter parent;
							ReAssignmentRequest reAssign;
							parent = this.myDC.getParent();
							if (parent != null) {
								for (StorageRequest stoReq : sReport
										.getUnhandledReqS()) {
									stoReq.setCenter(parent);
									reAssign = new ReAssignmentRequest(
											stoReq.getComp(), this.myDC, parent);
									reAssign.setUnHandledStoReq(stoReq);
									parent.getManager().handleRequest(reAssign);

									// parent.getManager().handleRequest(stoReq);
									Debug.debug(
											0,
											"<-------------Consumer is FUll.  Re-assignement to immediate parent --------------->");
								}
							} else {
								Debug.debug(0, "THE WHOLE SYSTEM IS FULL");
								// System.exit(0);
							}

							// Debug.debug(4," There are unhandled request "+sReport.getUnhandledReqS().size());
						}
					}
				}

			} else {
			}

		} else if (request instanceof ConnectionRequest) {
			ConnectionRequest conReq = (ConnectionRequest) request;
			Routing route = conReq.getRouting();
			RingNode node = route.getnode();
			Producer prod = (Producer) conReq.getProd();
			Stream stream = conReq.getStream();

			try {
				// Start Streaming
				node.addStream(stream);
				Thread th = new Thread(node);
				th.start();
				th.yield();
				th.sleep(2000);
			} catch (Exception ex) {
			}
		} else if (request instanceof ReAssignmentRequest) {
			DataCenter parentDC = (DataCenter) ((ReAssignmentRequest) request)
					.getParentDC();
			StorageRequest unHandledStoReq = ((ReAssignmentRequest) request)
					.getUnHandledStoReq();

			parentDC.getManager().handleRequest(unHandledStoReq);
		} else if (request instanceof HealthCheckRequest) {
			HealthCheckRequest hcReq = (HealthCheckRequest) request;
			if (hcReq.getProd() instanceof Producer) {
				System.out.println(hcReq.getProd().id + "Producer failure");
			}
			// .getParentDC();
			// StorageRequest unHandledStoReq = ((ReAssignmentRequest) request)
			// .getUnHandledStoReq();
			//
			// parentDC.getManager().handleRequest(unHandledStoReq);

		}
		// Schedule and connect (reqs) All requests as a batch (Possobility 1)
	}

	/*
	 * public void initializeStorage() { Debug.debug(5,
	 * "initializeStorage called on    " + myDC.id); stoReqs = new
	 * ArrayList<StorageRequest>(); // generate storage requests in a batch //
	 * for all existing producers for (int i = 0; i <
	 * myDC.getProducers().size(); i++) { long demand = ((long) (Math.random() *
	 * 12) + 1) * 100L;
	 * 
	 * StorageRequest sReq = new StorageRequest( myDC.getProducers().get(i),
	 * this.myDC); myDC.getProducers().get(i).setDemand(demand);
	 * stoReqs.add(sReq); //this.handleRequest(sReq);
	 * 
	 * } myDC.setStoReq(stoReqs); //System.out.println("p :" +
	 * myDC.getStoReq().size());
	 * 
	 * System.out.println("DC :" + myDC.getId() + " StoReq : " +
	 * stoReqs.size());
	 * 
	 * }
	 */
	/*
	 * public void initializeStorage(Producer prod) {
	 * 
	 * StorageRequest sReq = new StorageRequest(prod, this.myDC);
	 * this.handleRequest(sReq); }
	 */

	public void addUnScheduledStoReq(StorageRequest unStoReq) {
		this.unScheduledStoReqList.add(unStoReq);
	}

	public ArrayList<StorageRequest> getUnScheduledStoReq() {
		return unScheduledStoReqList;
	}

	public void setScheduleReportFileName(String reportName) {
		this.ScheduleReportFileName = reportName;
	}

	public String getScheduleReportFileName() {
		return this.ScheduleReportFileName;
	}

	public void scheAlgo(StorageRequest StrReq) {
		this.handleRequest(StrReq);

	}
}
