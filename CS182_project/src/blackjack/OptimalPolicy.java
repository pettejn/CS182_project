package blackjack;
import java.util.HashMap;
public class OptimalPolicy {
	HashMap<Qstate,Integer> policy;
	
	public OptimalPolicy(){
		
	}
	BufferedReader br = new BufferedReader(new FileReader("file.txt"));
	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	        sb.append(line);
	        sb.append(System.lineSeparator());
	        line = br.readLine();
	    }
	    String everything = sb.toString();
	} finally {
	    br.close();
	}

}
