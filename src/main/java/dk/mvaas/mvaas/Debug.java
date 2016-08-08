/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author paul
 * 
 */
public class Debug {
	
	private static final int DEBUGLEVEL = 0;
	
	public static void debug(int level , String text){
		if (level <= DEBUGLEVEL) {
			System.out.println(text);
		}
		
	}

}
