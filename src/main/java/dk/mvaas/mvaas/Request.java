/**
 *  MVaaSSim
 */
package dk.mvaas.mvaas;

/**
 * @author karun & Paul
 * 
 */

public class Request {
	/**
	 * Its super class to all requests
	 * The system itself a request driven system 
	 * 
	 */
	   public static int nextRequestID = 0;
	   
	   
	   protected int id;
	   
       Request(){
    	   id = nextRequestID++;
       }
       
       public int getID(){
    	   return id;
       }
}
