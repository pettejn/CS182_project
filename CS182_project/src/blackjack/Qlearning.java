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
	
	private HashMap<Qstate,Double> states;
	private int iterations = 20000;
	private double epsilon = 0.8;
	private double alpha = 0.8;
	private double gamma =0.8;
	private int rewards=0;
	private int victory=0;
	private int split = 0;
	private int amountSplits = 0;
	private HashMap<State,Integer> optimalPolicy;
	
	public Qlearning(){
		this.states = new HashMap();
		for (int k=1; k<14;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if(action == 2 && i<11){
						continue;
					}
					if (i%2==0 && i<21 && i>3){
						this.states.put(new Qstate(new State(i, 0, k, 1), action), (double) 0);
					}
					if (i>12 && i<22){
						this.states.put(new Qstate(new State(i, 1, k, 0), action), (double) 0);
					}
					if (i>4 && i<22){
						this.states.put(new Qstate(new State(i, 0, k, 0), action), (double) 0);
					}
				}
				this.states.put(new Qstate(new State(12, 1, k, 1), action), (double) 0);
			}
		}
	}
	
	public HashMap<State,Integer> getOptimalPolicy(){
		this.optimalPolicy = new HashMap();
		for (Qstate key : this.states.keySet()) {
			int action = greedy(key.getState());
			this.optimalPolicy.put(key.getState(), action);
			System.out.println(key +"  " + this.optimalPolicy.get(key.getState()));
		}

		return this.optimalPolicy;
	}
	
	//choose random action with probability epsilon, else argmax Q(s,a)
	private int epsilonGreedy(State state){
		if (new Random().nextDouble() <= this.epsilon){
			double prob1 = new Random().nextDouble();
			double prob2 = new Random().nextDouble();
			if (state.isPair()==0){
				if(state.getSum()<11){
						return prob1 < 0.5 ? 0 : 1;
				}
				return (int)(Math.random()*3);
			}
			if (state.getSum()<11 && state.isPair()==1){
				return prob1 < 0.66? (prob2 < 0.5 ? 0 : 1) : 3;
				
			}
			return prob1 < 0.5 ? (prob2 < 0.5 ? 0 : 1) : (prob2 < 0.5 ? 2 : 3);
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
			if (key.equals(this.findQstate(state, key.getAction())) && this.states.get(key)>value){
				if(key.getAction()==3 && key.getState().isPair()==0){
					continue;
				}
				value = this.states.get(key);
				action = key.getAction();
			}
				
		}
		
		return action;
	}
	
	private double playSplit(Blackjack blackjack){
		this.split=1;
		int counter = 0;
		double value = 0;
		List<Integer> list = blackjack.getState(); //first state
		while (list.get(4)==(double) 0){	
			list = blackjack.getState();
			this.rewards+=list.get(4);
			if(list.get(4)==1 || list.get(4)==2){
				this.victory++;
			}
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.epsilonGreedy(state);
			Qstate real = this.findQstate(state, action);
			if (real == null){
				break;
			}
			if (counter==0){
				value = this.states.get(real);
				counter++;
			}
			
			if (list.get(4)!=0){
				this.states.put(real, ((this.states.get(real) + alpha*(list.get(4)-this.states.get(real)))));
				break;
			}
			List<Integer> newList = blackjack.makeMove(action);
			State newstate = new State(newList.get(0),newList.get(1),newList.get(2),newList.get(3)); 
			if( newstate.getSum()>21){ 
				this.states.put(real, ((this.states.get(real) + alpha*(newList.get(4)-this.states.get(real)))));	
			} 
			else{
			this.states.put(real, (this.states.get(real) + alpha*(newList.get(4) + gamma*this.states.get(this.findQstate( newstate, this.greedy(newstate)))))-this.states.get(real));
			}
		}	
		return value;
	}
//Arrays.asList(playerSum, playerAce, dealerCard,pair,reward));
	private void play(){
		Blackjack blackjack = new Blackjack();
		List<Integer> list = blackjack.getState(); //first state
		while (list.get(4)==(double)0){	
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.epsilonGreedy( state);
			Qstate real = this.findQstate(state, action);
			if(action==3){
				//trenger bare faa inn card and dealercard
				List<Integer> newList  = blackjack.makeMove(action); //can also get the blackjack objects here
				Blackjack blackjack1 = new Blackjack(newList.get(0),newList.get(1));
				Blackjack blackjack2 = new Blackjack(newList.get(0),newList.get(1));
				this.split = 1;
				this.amountSplits++;
				double reward1 = playSplit(blackjack1);
				double reward2 = playSplit(blackjack2);
				this.states.put(real, ((this.states.get(real) + alpha*((reward1+reward2)-this.states.get(real)))));
			}
			else{
				list = blackjack.makeMove(action);
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
				if( newstate.getSum()>21){ //use gameover here?
					this.states.put(real, ((this.states.get(real) + alpha*(list.get(4)-this.states.get(real)))));	
				} else{
					this.states.put(real, (this.states.get(real) + alpha*(list.get(4) + gamma*this.states.get(this.findQstate( newstate, this.greedy(newstate)))))-this.states.get(real));
			}
		}
		}	
		}

	
	//Takes a state as input, returns corresponding qstate in our hashmap.
	//if it doesnt exist, return null.
	// Returns a Qstate-object
	private Qstate findQstate(State state, int action){
		for (Qstate name: this.states.keySet()){
			if (name.getState().isPair()==state.isPair() && name.getState().getSum()==state.getSum() && name.getAction()==action && name.getState().getAce()==state.getAce() && name.getState().getDealer()==state.getDealer()){
				return name;
			}
		}	
		return null;
		
	}
	
	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		System.out.println(qlearning.findQstate(new State(20,0,11,1), 1));
		double prob1 = 0.4;
		double prob2 = 0.4;
		System.out.println( prob1 < 0.5 ? (prob2 < 0.5 ? 0 : 1) : (prob2 < 0.5 ? 2 : 3));
		while (qlearning.iterations>0){
			System.out.println(qlearning.iterations);
			qlearning.iterations--;
//			if( qlearning.iterations==2000){
//				qlearning.alpha=0.05;
//				qlearning.epsilon=0.05;
//			}
			qlearning.play();
		
		}
		qlearning.getOptimalPolicy();
		System.out.println(qlearning.states.size());
	}

}

