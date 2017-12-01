package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.Iterator;

public class ListLearning {
	
	private HashMap<Qstate,Double> states;
	private List<Double> rewards;
	private List<Qstate> qstates;
	private int iterations = 10000;
	private double epsilon = 0.1;
	private double alpha = 0.1;
	private double gamma = 0.9;
	
	public ListLearning(){
		this.qstates = new ArrayList();
		this.rewards = new ArrayList();
	 for(int i=2; i<21; i++){
		 for (int j=2; j<11; j++){
			 for (int action=0; action<2; action++){
				 for (int k=0; k<2;k++){
			 this.qstates.add(new Qstate(new State(i, k, j), action));
			 this.rewards.add((double) 0);
				 }
			 }
		 }
	 }
	}
	
	private int epsilonGreedy(double epsilon, State state){
		if (new Random().nextDouble() <= epsilon){
			if (new Random().nextDouble() <=0.5){
				return 0;
			}
			return 1;
		}
		return this.greedy(state);
		
	}
	
	private int greedy2(State state){
		return this.states.entrySet().stream().filter(map -> map.getKey().getState().equals(state)).
				max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey().getAction();

	}
	
	private int greedy(State state){
		double value = -100; 
		int action = 0;
		for (Qstate key : this.states.keySet()) {
			if (key.equals(state) && this.states.get(key)>value){
				value = this.states.get(key);
				action = key.getAction();
			}
				
		}
		
		return 1;
	}
	

	private void play(){
		Blackjack blackjack = new Blackjack();
		List<Integer> list = blackjack.getState();
		State state = new State(list.get(0),list.get(1),list.get(2));
		int action = this.epsilonGreedy(this.epsilon, state);
		Qstate qstate = new Qstate(state, action);
		System.out.println(this.states.get(qstate));
		List<Integer> newList = blackjack.makeMove(action);
		State newstate = new State(newList.get(0),newList.get(1),newList.get(2));
		this.states.put(qstate, (this.states.get(qstate) + alpha*(list.get(3) + gamma*Math.max(this.states.get(new Qstate(newstate, 0)), this.states.get(new Qstate(newstate, 1))))-this.states.get(qstate)));
	}
	
	

	
	public static void main(String[] args) {
		ListLearning qlearning = new ListLearning();
		System.out.println(qlearning.states.size());
		qlearning.play();
		for (Qstate name: qlearning.states.keySet()){

            String key =name.toString();
            String value = qlearning.states.get(name).toString();  
            System.out.println(key + " " + value);  


} 
	}
}

