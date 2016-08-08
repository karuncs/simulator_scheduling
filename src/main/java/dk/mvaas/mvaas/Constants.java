/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author paul & Karun
 */
public class Constants {
	/**
	 * Design the system here like how many DCs or BCS in each DC
	 * Simulation duration is also can be decided here
	 * DC,0,300; Name of the DC, id of the DC and number of associated producers in DC
	 * BCR,13,12;  Name of the BC, id of the BC and no.of nodes in BC
	 */
	public static final String systemSmall = "DC,0,300;(DC,1,200;(BCR,5,1;BCR,6,10;)BCR,13,13;);";
	public static final String systemSmall2 = "DC,0,3;(DC,1,200;(BCR,5,1;BCR,6,0;)BCR,13,23;);";
	public static final String systemMainDoc = "DC,0,1;(DC,1,100;(BCR,5,8;BCR,6,8;DC,7,100;(BCR,13,12;BCR,14,10;)BCR,8,10;)DC,2,100;(BCR,9,5;BCR,10,5;)BCR,3,8;DC,4,100;(DC,11,100;(BCR,15,8;BCR,16,4;)DC,12,100;(BCR,17,10;)))";
	public static final double SPACE_SLAG = 0; // 2% slag on node free
													// resources
	public static final long CHUNKINTETVAL = 1000L;
	public static final long TIMETICKLENGTH = 300L;
	public static long simulationDuration = 4000;// 
	public static long nodeSize =(long) (((1.0 - 0.02) * 1500));
	public static NodeElements DEFAULT_Node_PARAMSET = new NodeElements(
			nodeSize, 10L, 10000L);
	 public static long minsize = 100L, maxsize = 1200L;
	 public static enum LBFLAG { Start, Process };
}
