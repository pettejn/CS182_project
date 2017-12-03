package blackjack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Qlearning {

	private HashMap<List<Integer>,Double> qstates;
	private HashMap<List<Integer>,Integer> testMap;
	private int states;
	private int iterations = 200;
	private double epsilon = 0.8;
	private double alpha = 0.8;
	private double gamma =0.8;
	private int rewards=0;
	private int victory=0;
	private int lVictory=0;
	private int split = 0;
	private int amountSplits = 0;
	private HashMap<State,Integer> policy;
	private HashMap<State,Integer> optimalPolicy;

	public Qlearning(){
		this.qstates = new HashMap();
		for (int k=1; k<11;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if (i%2==0 && i<21 && i>3){
						this.qstates.put(Arrays.asList(i, 0, k, 1, action), (double) 0);
					}
					if (i>12 && i<22){
						this.qstates.put(Arrays.asList(i, 1, k, 0, action), (double) 0);
					}
					if (i>3 && i<22){
						this.qstates.put(Arrays.asList(i, 0, k, 0, action), (double) 0);
					}
				}
				this.qstates.put(Arrays.asList(12, 1, k, 1, action), (double) 0);
				this.qstates.put(Arrays.asList(12, 1, k, 0, action), (double) 0);
			}
		}
		for (int k=1; k<11;k++){
			for (int i=1; i<22;i++){
				if (i%2==0 && i<21 && i>3){
					this.states++;

				}
				if (i>12 && i<22){
					this.states++;
				}
				if (i>3 && i<22){
					this.states++;
				}
			}
			this.states++;
		}
	}
	
	private void setOptimalPolicy() throws IOException{
		this.optimalPolicy = new HashMap<>();
		final String dir = System.getProperty("user.dir");
	BufferedReader br = new BufferedReader(new FileReader(dir + "/src/blackjack/OptimalPolicyCorrect.txt"));
	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	    	String[] tokens = line.split("-");
	    	//System.out.println(tokens[0]);
	    		this.optimalPolicy.put(new State( Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3])), Integer.parseInt(tokens[4]));
	    		line = br.readLine();
	    }
	} finally {
	    br.close();
	}
	}
	
	public HashMap<State, Integer> getPolicy(){
		return this.policy;
	}

	public HashMap<State,Integer> getOptimalPolicy(){
		this.policy = new HashMap();
		for (int k=1; k<11;k++){
			for (int i=1; i<22;i++){
				if (i%2==0 && i<21 && i>3){
					this.policy.put(new State(i, 0, k, 1), 0);
				}
				if (i>12 && i<22){
					this.policy.put(new State(i, 1, k, 0), 0);
				}
				if (i>3 && i<22){
				this.policy.put(new State(i, 0, k, 0), 0);
				}
			}
		this.policy.put(new State(12, 1, k, 1), 0);
		this.policy.put(new State(12, 1, k, 0), 0);
		}
		for (State key : this.policy.keySet()) {
			int action = greedy(key);
			this.policy.put(key, action);
			//System.out.println(key +"  " + this.policy.get(key));
		}

		return this.policy;
	}

	private int epsilonGreedy(List<Integer> state){
		if (new Random().nextDouble() <= this.epsilon){
			if (state.get(3)==0){
				return (int)(Math.random()*3);
			}
			return (int)(Math.random()*4);
		}
		return this.greedy(state);
	}

	private int greedy(List<Integer> state){
		double value = -100; 
		int action = 0;
		for (int i=0;i<4;i++){
			List<Integer> state1 = state;
			state1.add(i);
			if (this.qstates.get(state1)>value){
				value = this.qstates.get(state1);
				action = state1.get(4);
			}

		}

		return action;
	}
	
	private int optimalGreedy(State state){
		//System.out.println(state);
		//System.out.println(this.findState(state));
		return this.optimalPolicy.get(this.findState(state));
	}

	private void playSplit(int action, Blackjack blackjack, Qstate real){
		List<Integer> newList  = blackjack.makeMove(action); //can also get the blackjack objects here
		Blackjack blackjack1 = new Blackjack(newList.get(0),newList.get(1));
		Blackjack blackjack2 = new Blackjack(newList.get(0),newList.get(1));
		List<Integer> hand1 = blackjack1.getState();
		List<Integer> hand2 = blackjack2.getState();
		State state1 = new State(hand1.get(0),hand1.get(1),hand1.get(2),hand1.get(3));
		State state2 = new State(hand2.get(0),hand2.get(1),hand2.get(2),hand2.get(3));
		//System.out.println("old value for: " + real + "is:"+ this.qstates.get(real)); 
		double y = (this.qstates.get(real) + alpha*(gamma*(this.qstates.get(this.findQstate(state1,this.greedy(state1)))+this.qstates.get(this.findQstate(state2,this.greedy(state2))))-this.qstates.get(real)));
	   // System.out.println("updated value: " + real + "is:"+ y); 
		this.qstates.put(real, (this.qstates.get(real) + alpha*(gamma*(this.qstates.get(this.findQstate(state1,this.greedy(state1)))+this.qstates.get(this.findQstate(state2,this.greedy(state2))))-this.qstates.get(real))));
		//System.out.println("playing first splitted hand");
		this.play(blackjack1);
		System.out.println("playing second splitted hand");
		this.play(blackjack2);	

	}

	private void play(){
		play(null);
	}

	private void play(Blackjack blackjack){
		if (blackjack==null){
			blackjack = new Blackjack();
		}
		List<Integer> list = blackjack.getState(); //first state
		while (list.get(4)==(double)0){	
		    System.out.println("Current reward is: " + list.get(4));
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.optimalGreedy(state);
			Qstate real = this.findQstate(state, action);
			list.add(action);
		    System.out.println("Qstate: " + real);
			if(list.get(4)==3){
				this.playSplit(action, blackjack, real);
				break;
			}
			else{
				list = blackjack.makeMove(action);
				this.rewards += list.get(4);
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
			    System.out.println("New state after move: " + newstate + ", reward is: " + list.get(4));
				if( newstate.getSum()>21){ //use gameover here?
					System.out.println("old value for: " + real + "is:"+ this.qstates.get(real)); 
			        double y = (this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real))); 
			        System.out.println("updated value: " + real + "is:"+ y); 
					this.qstates.put(real, ((this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real)))));	
				} else{
					System.out.println("old value for: "  + real + "is:"+ this.qstates.get(real)); 
			        double y = (this.qstates.get(real) + alpha*(list.get(4)+gamma* this.qstates.get(this.findQstate(newstate,this.greedy(newstate)))-this.qstates.get(real)));
			        System.out.println("updated value: " + real + "is:"+ y); 
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4) + gamma*this.qstates.get(this.findQstate( newstate, this.greedy(newstate)))))-this.qstates.get(real));
				}
			}
		}	
	}


	//Takes a state as input, returns corresponding qstate in our hashmap.
	//if it doesnt exist, return null.
	// Returns a Qstate-object
	private Qstate findQstate(State state, int action){
		for (Qstate name: this.qstates.keySet()){
			if (name.getState().isPair()==state.isPair() && name.getState().getSum()==state.getSum() && name.getAction()==action && name.getState().getAce()==state.getAce() && name.getState().getDealer()==state.getDealer()){
				return name;
			}
		}	
		return null;
	}
	private State findState(State state){
		for (State name : this.optimalPolicy.keySet()){
			//System.out.println(name);
			if (name.isPair()==state.isPair() && name.getSum()==state.getSum() && name.getAce()==state.getAce() && name.getDealer()==state.getDealer()){
				return name;
			}
		}	
		return null;
	}
	

	public static void main(String[] args) throws IOException {
		Qlearning qlearning = new Qlearning();
		qlearning.setOptimalPolicy();
		qlearning.testMap = new HashMap<>();
		List<Integer> list = new ArrayList<>(Arrays.asList(12,0,1,1));
		qlearning.testMap.put(list, 1);
		List<Integer> liste = list;
		System.out.println(list);
		liste.add(1);
		System.out.println(list);
		 System.out.println(qlearning.testMap.get(new ArrayList<>(Arrays.asList(12,0,1,1))));
		 System.out.println(qlearning.qstates.get(new Qstate(new State(12,0,1,0),1)));
		 
		//System.out.println(qlearning.optimalPolicy);
//		double prob1 = 0.4;
//		double prob2 = 0.4;
//		while (qlearning.iterations>0){
//			System.out.println(qlearning.iterations);
//			qlearning.iterations--;
//						if( qlearning.iterations==999){
//							qlearning.alpha=0.05;
//							qlearning.epsilon=0.05;
//							qlearning.lVictory = qlearning.victory;
//						}
//			qlearning.play();
//
//		}
//		//qlearning.getOptimalPolicy();
////		for (Qstate key : qlearning.qstates.keySet()){
////			System.out.println(key + " value "+ qlearning.qstates.get(key));
////		}
////		System.out.println(qlearning.states);
//		System.out.println(qlearning.victory);
//		System.out.println(qlearning.lVictory);
//		System.out.println(qlearning.victory - qlearning.lVictory);
//		System.out.println(qlearning.rewards);
	}
}

