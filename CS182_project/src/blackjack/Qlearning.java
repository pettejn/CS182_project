package blackjack;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Qlearning {
	
	// NOTE TO PETTER! We now have the possbility to split if our sum is an even number... problem
	
	private HashMap<Qstate,Double> qstates;
	private int states;
	private int iterations = 10000;
	private double epsilon = 0.9;
	private double alpha = 0.8;
	private double gamma =1;
	private int rewards=0;
	private int victory=0;
	private int lVictory = 0;
	private int split = 0;
	private int amountSplits = 0;
	private HashMap<State,Integer> optimalPolicy;
	
	public Qlearning(){
		this.qstates = new HashMap();
		for (int k=1; k<11;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if (i%2==0 && i<21 && i>3){
						this.qstates.put(new Qstate(new State(i, 0, k, 1), action), (double) 0);
					}
					if (i>12 && i<22){
						this.qstates.put(new Qstate(new State(i, 1, k, 0), action), (double) 0);
					}
					if (i>3 && i<22){
						this.qstates.put(new Qstate(new State(i, 0, k, 0), action), (double) 0);
					}
				}
				this.qstates.put(new Qstate(new State(12, 1, k, 1), action), (double) 0);
				this.qstates.put(new Qstate(new State(12, 1, k, 0), action), (double) 0);
			}
		}
//		for (int k=1; k<14;k++){
//				for (int i=1; i<22;i++){
//					if (i%2==0 && i<21 && i>3){
//						this.states++;
//						
//					}
//					if (i>12 && i<22){
//						this.states++;
//					}
//					if (i>3 && i<22){
//						this.states++;
//					}
//				}
//				this.states++;
//			}
		}
	
	public HashMap<State,Integer> getOptimalPolicy(){
		this.optimalPolicy = new HashMap();
		for (int k=1; k<11;k++){
			for (int i=1; i<22;i++){
				if (i%2==0 && i<21 && i>3){
					this.optimalPolicy.put(new State(i, 0, k, 1), 0);
				}
				if (i>12 && i<22){
					this.optimalPolicy.put(new State(i, 1, k, 0), 0);
				}
				if (i>3 && i<22){
				this.optimalPolicy.put(new State(i, 0, k, 0), 0);
				}
			}
		this.optimalPolicy.put(new State(12, 1, k, 1), 0);
		this.optimalPolicy.put(new State(12, 1, k, 0), 0);
		}
		for (State key : this.optimalPolicy.keySet()) {
			int action = greedy(key);
			this.optimalPolicy.put(key, action);
			System.out.println(key +"  " + this.optimalPolicy.get(key));
		}

		return this.optimalPolicy;
	}
	
	//choose random action with probability epsilon, else argmax Q(s,a)
   private int epsilonGreedy(State state){
		if (new Random().nextDouble() <= this.epsilon){
			if (state.isPair()==0){
				return (int)(Math.random()*3);
			}
			return (int)(Math.random()*4);
		}
		return this.greedy(state);
	}
	   
	private int greedy(State state){
		double value = -100; 
		int tempAction =0;
		System.out.println("State greedy" + state);
		for (int action=0; action<4;action++){
			Qstate key = new Qstate(state,action);
			System.out.println("KEY IS: " + key);
			System.out.println("GETTING: " + this.qstates.get(key));
			System.out.println("Value: "+ value);
			if (this.qstates.get(key)!=null){
				if (this.qstates.get(key)>value) {
					value = this.qstates.get(key);
					tempAction =action;
				}
			}
			}
		
		return tempAction;
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
		//double y = (this.qstates.get(real) + alpha*(gamma*(this.qstates.get(this.findQstate(state1,this.greedy(state1)))+this.qstates.get(this.findQstate(state2,this.greedy(state2))))-this.qstates.get(real)));
	    //System.out.println("updated value: " + real + "is:"+ y); 
		this.qstates.put(real, (this.qstates.get(real) + alpha*(gamma*(this.qstates.get(new Qstate(state1,this.greedy(state1)))+this.qstates.get(new Qstate(state2,this.greedy(state2))))-this.qstates.get(real))));
		//System.out.println("playing first splitted hand");
		this.play(blackjack1);
		//System.out.println("playing second splitted hand");
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
			//System.out.println("Current reward is: " + list.get(4));
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			System.out.println("State is: "+ state);
			int action = this.epsilonGreedy( state);
			Qstate real = new Qstate(state,action);
			System.out.println("Hascode is:" + real.hashCode());
//			System.out.println("ingrid try?: "+ this.qstates.get(ingrid));
//			System.out.println("uten lookup:" + ingrid.hashCode());
//			Qstate real = this.findQstate(state, action);
//			System.out.println("Qstate: " + real);
//			System.out.println("real try?: "+ this.qstates.get(real));
//			System.out.println("real hashcode:" +real.hashCode());
//			System.out.println("try again: "+ this.qstates.get(1701002));
			if(action==3){
				this.playSplit(action, blackjack, real);
				break;
			}
			else{
				list = blackjack.makeMove(action);
				if(list.get(4)>0){
					this.victory++;
				}
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
				//System.out.println("New state after move: " + newstate + ", reward is: " + list.get(4));
				if( newstate.getSum()>21){ 
					//System.out.println("old value for: " + real + "is:"+ this.qstates.get(real)); 
			        //double y = (this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real))); 
			        //System.out.println("updated value: " + real + "is:"+ y); 
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real))));	
				} else{
					//System.out.println("old value for: "  + real + "is:"+ this.qstates.get(real)); 
			        //double y = (this.qstates.get(real) + alpha*(list.get(4)+gamma* this.qstates.get(this.findQstate(newstate,this.greedy(newstate)))-this.qstates.get(real)));
			        //System.out.println("updated value: " + real + "is:"+ y); 
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)+gamma* this.qstates.get(new Qstate(newstate,this.greedy(newstate)))-this.qstates.get(real))));
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
	
	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		double prob1 = 0.4;
		double prob2 = 0.4;
		while (qlearning.iterations>0){
			System.out.println(qlearning.iterations);
			qlearning.iterations--;
			if(qlearning.iterations==0.9*25000){
				System.out.println("Chagned alpha&epsilon");
				qlearning.alpha=qlearning.alpha*0.90;
				qlearning.epsilon=qlearning.epsilon*0.90;
				//qlearning.lVictory=qlearning.victory;
			}
			if(qlearning.iterations==0.7*25000){
				System.out.println("Chagned alpha&epsilon");
				qlearning.alpha=qlearning.alpha*0.10;
				qlearning.epsilon=qlearning.epsilon*0.10;
				//qlearning.lVictory=qlearning.victory;
			}
			if(qlearning.iterations==0.3*25000){
				System.out.println("Chagned alpha&epsilon");
				qlearning.alpha=0.05;
				qlearning.epsilon=0;
				//qlearning.lVictory=qlearning.victory;
			}
			
			qlearning.play();
		
		}
		//qlearning.getOptimalPolicy();
		//System.out.println(qlearning.qstates.size());
		//System.out.println(qlearning.states);
		System.out.println(qlearning.victory);
		System.out.println(qlearning.lVictory);
		System.out.println(qlearning.victory-qlearning.lVictory);
		//qlearning.getOptimalPolicy();
	}

}

