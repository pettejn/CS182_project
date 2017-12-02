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

	private HashMap<Qstate,Double> qstates;
	private int states;
	private int iterations = 40000;
	private double epsilon = 0.8;
	private double alpha = 0.8;
	private double gamma =0.8;
	private int rewards=0;
	private int victory=0;
	private int lVictory=0;
	private int split = 0;
	private int amountSplits = 0;
	private HashMap<State,Integer> optimalPolicy;

	public Qlearning(){
		this.qstates = new HashMap();
		for (int k=1; k<11;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if(action == 2 && i<11){
						continue;
					}
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

	private int greedy(State state){
		double value = -100; 
		int action = 0;
		for (Qstate key : this.qstates.keySet()) {
			if (key.equals(this.findQstate(state, key.getAction())) && this.qstates.get(key)>value){
				if(key.getAction()==3 && key.getState().isPair()==0){
					continue;
				}
				value = this.qstates.get(key);
				action = key.getAction();
			}

		}

		return action;
	}

	private void playSplit(int action, Blackjack blackjack, Qstate real){
		List<Integer> newList  = blackjack.makeMove(action); //can also get the blackjack objects here
		Blackjack blackjack1 = new Blackjack(newList.get(0),newList.get(1));
		Blackjack blackjack2 = new Blackjack(newList.get(0),newList.get(1));
		this.play(blackjack2);
		this.play(blackjack1);

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
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.epsilonGreedy( state);
			Qstate real = this.findQstate(state, action);
			if(action==3){
				this.playSplit(action, blackjack, real);
				break;
			}
			else{
				list = blackjack.makeMove(action);
				if (list.get(4)>0){
					this.victory++;
				}
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
				if( newstate.getSum()>21){ //use gameover here?
					this.qstates.put(real, ((this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real)))));	
				} else{
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

	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		double prob1 = 0.4;
		double prob2 = 0.4;
		while (qlearning.iterations>0){
			System.out.println(qlearning.iterations);
			qlearning.iterations--;
						if( qlearning.iterations==20000){
							qlearning.alpha=0.05;
							qlearning.epsilon=0.05;
							qlearning.lVictory = qlearning.victory;
						}
			qlearning.play();

		}
		//qlearning.getOptimalPolicy();
//		for (Qstate key : qlearning.qstates.keySet()){
//			System.out.println(key + " value "+ qlearning.qstates.get(key));
//		}
//		System.out.println(qlearning.states);
		System.out.println(qlearning.victory);
		System.out.println(qlearning.lVictory);
		System.out.println(qlearning.victory - qlearning.lVictory);
	}
}

