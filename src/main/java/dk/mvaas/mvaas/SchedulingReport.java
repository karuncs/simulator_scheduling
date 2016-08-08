/**
 * 
 */
package dk.mvaas.mvaas;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @authors Paul and Karun
 * 
 */

public class SchedulingReport {
	/**
	 * It comes from scheduling algorithm and contains the unscheduled and
	 * scheduled storage requests.
	 * 
	 */

	private ArrayList<ReportInfo> schdulingInfo;
	private ArrayList<StorageRequest> unhandledReqS;

	public SchedulingReport() {
		schdulingInfo = new ArrayList<ReportInfo>();
		unhandledReqS = new ArrayList<StorageRequest>();
	}

	public void addInfo(ReportInfo schedulInfo) {
		schdulingInfo.add(schedulInfo);
	}

	public void addUnhandledReq(StorageRequest unHanReqS) {
		unhandledReqS.add(unHanReqS);
	}

	public ArrayList<ReportInfo> getSchdulingInfo() {
		return schdulingInfo;
	}

	public ArrayList<StorageRequest> getUnhandledReqS() {
		return unhandledReqS;
	}

	/**
	 * @return
	 */
	public int size() {
		// TODO Auto-generated method stub
		return schdulingInfo.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("---- Scheduling report ---\n");
		for (int i = 0; i < schdulingInfo.size(); i++) {
			sb.append(schdulingInfo.get(i) + "\n");
		}

		sb.append("--------------------------\n");
		return sb.toString();

	}

}
