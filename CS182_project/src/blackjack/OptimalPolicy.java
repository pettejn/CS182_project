package blackjack;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class OptimalPolicy {
	HashMap<State,Integer> policy;
	
	public OptimalPolicy() throws IOException{
		policy = new HashMap();
		final String dir = System.getProperty("user.dir");
	BufferedReader br = new BufferedReader(new FileReader(dir + "/src/blackjack/OptimalPolicyCorrect.txt"));
	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	    	String[] tokens = line.split("-");
	    	System.out.println(tokens[0]);
	    		this.policy.put(new State( Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3])), Integer.parseInt(tokens[4]));
	    		line = br.readLine();
	    }
	} finally {
	    br.close();
	}
	}
	
	public HashMap<State, Integer> getPolicy(){
		return this.policy;
	}
	public static void main(String[] args) throws IOException {
		OptimalPolicy plcy = new OptimalPolicy();
		for (State key : plcy.policy.keySet()){
			System.out.println(key +"value "+ plcy.policy.get(key));
		}
		final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
	}
}
