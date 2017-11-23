package blackjack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Qlearning {
	
	private HashMap<Qstate,Double> states;
	private int iterations = 100000;
	private double epsilon = 0.1;
	private double alpha = 0.1;
	private double gamma = 0.9;
	
	public Qlearning(){
		this.states = new HashMap();
	 for(int i=2; i<22; i++){
		 for (int j=1; j<14; j++){
			 for (int action=0; action<2; action++){
				 for (int k=0; k<2;k++){
			 states.put(new Qstate(new State(i, k, j), action), (double) 0);
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
			if (key.equals(this.findQstate(new Qstate(state, key.getAction()))) && this.states.get(key)>value){
				value = this.states.get(key);
				action = key.getAction();
			}
				
		}
		
		return 1;
	}
	

	private void play(){
		Blackjack blackjack = new Blackjack();
		List<Integer> list = blackjack.getState();
		while (list.get(3)==(double) 0){
		list = blackjack.getState();
		State state = new State(list.get(0),list.get(1),list.get(2));
		System.out.println(state);
		int action = this.epsilonGreedy(this.epsilon, state);
		Qstate qstate = new Qstate(state, action);
		Qstate real = this.findQstate(qstate);
		System.out.println("q" +qstate);
		System.out.println("real" + real);
		if (list.get(3)!=0){
			this.states.put(real, ((this.states.get(real) + alpha*(list.get(3)-this.states.get(real)))));
			break;
		}
		List<Integer> newList = blackjack.makeMove(action);
		State newstate = new State(newList.get(0),newList.get(1),newList.get(2));
		
		this.states.put(real, (this.states.get(real) + alpha*(list.get(3) + gamma*Math.max(this.states.get(this.findQstate( new Qstate(newstate, 0))), this.states.get(this.findQstate(new Qstate(newstate, 1)))))-this.states.get(real)));
		}	
		}

	private Qstate findQstate(Qstate state){
		for (Qstate name: this.states.keySet()){
			if (name.getState().getSum()==state.getState().getSum() && name.getAction()==state.getAction() && name.getState().getAce()==state.getState().getAce() && name.getState().getDealer()==state.getState().getDealer()){
				return name;
			}
		}	
		return null;
		
	}
	
	

	
	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		while (qlearning.iterations>0){
			qlearning.iterations--;
		qlearning.play();
		for (Qstate name: qlearning.states.keySet()){

            String key =name.toString();
            String value = qlearning.states.get(name).toString(); 
            if(qlearning.states.get(name)!=0){
            System.out.println(key + " " + value);  
            }
		}

} }}

