package blackjack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Qlearning {
	
	private HashMap<Qstate,Double> states;
	
	public Qlearning(){
		this.states = new HashMap();
	 for(int i=2; i<21; i++){
		 for (int j=2; j<11; j++){
			 for (int action=1; action<4; action++){
			 states.put(new Qstate(new State(i, true, j), action), (double) 0);
			 states.put(new Qstate(new State(i, false, j), action), (double) 0);
			 System.out.println(i);
			 }
		 }
	 }
	}
	

	
	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		System.out.println(qlearning.states);
	}
}

