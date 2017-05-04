package dk.mvaas.mvaas;

/**
 * @author karun & paul
 *
 */

public class SystemBuilder {

	private String topology;
	private int index;
	private DataCenter root;
	private String currentToken;

	public SystemBuilder(String topology) {

		this.topology = topology;
		currentToken = "";
		buildSystem();
		// NOW THE SYSTEM STRUCTURE IS BUILD AND PRODUCERS ARE ASSIGNED

	}

	public void buildSystem() {
		index = 0;
		String token = getToken();
		// System.out.println(token);
		root = (DataCenter) consumerFromToken(token, null);
		// Consumer.consumers.add(root); // adding root to consumers list
		token = getToken(); // move current token
		addChildren(root);

	}

	/**
	 * @param root
	 */
	private void addChildren(DataCenter parent) {
		// at this point currentToken != )
		/*
		 * Healthc check request calling at the time of system building try {
		 * 
		 * FailuresDetection fd = new FailuresDetection(parent); Thread th = new
		 * Thread(fd); th.start();
		 * 
		 * th.yield(); th.sleep(2000); } catch (Exception ex) { }
		 */
		if (index < topology.length() - 1) {
			String token = currentToken;
			// System.out.println("    "+token);

			if (token.equals("(")) {
				token = getToken();
			}
			Consumer con = parent;
			while (!token.equals(")") && !token.equals("(")) {
				// Parse token
				con = consumerFromToken(token, parent);
				parent.addConsumer(con);
				token = getToken();
			}

			// at this point c == ) or c == (

			if (token.equals("(")) {
				if (con instanceof DataCenter) {
					addChildren((DataCenter) con);
				} else {
					System.out.println("*** ERROR");
				}
			} else {
				while (index < topology.length() - 1 && token.equals(")")) {
					token = getToken();
					parent = parent.getParent();
				}
				addChildren(parent);
			}
		}
	}

	/**
	 * @param token
	 * @return
	 */
	private Consumer consumerFromToken(String token, DataCenter parent) {
		String[] subtokens = token.split(",|;");
		// System.out.println(Arrays.toString(subtokens));
		String type = subtokens[0];
		if (type.equals("DC")) {
			String id = "DC" + subtokens[1];
			int noOfProducers = Integer.parseInt(subtokens[2]);
			return new DataCenter(id, parent, noOfProducers);
		} else if (type.equals("BCR")) {
			String id = "BCR" + subtokens[1];
			int ringSize = Integer.parseInt(subtokens[2]);
			return new BasicConsumerRing(id, parent, ringSize);
		} else {
			return null;
		}
	}

	private String getToken() {
		String result = "";
		char c = topology.charAt(index);
		if (c == '(' || c == ')') {
			result += c;
			index++;
		} else {
			int end = topology.indexOf(';', index) + 1;
			result = topology.substring(index, end);
			index = end;
		}
		// System.out.println("      " + result);
		currentToken = result;
		return result;
	}

	public Consumer getRoot() {
		return root;
	}
}
