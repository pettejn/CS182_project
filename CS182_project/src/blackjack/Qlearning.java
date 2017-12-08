package blackjack;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	private int trainingIterations = 1000000;
	private int testIterations = 500000;
	private int i = 1000000;
	private double epsilon = 1;
	private double alpha = 0.01;
	private double gamma =0.05;
	private int rewards=0;
	private int totalRewards=0;
	private int amountRewards=0;
	private int victory=0;
	private int lVictory = 0;
	private int splits = 0;
	private int amountSplits = 0;
	private int lost=0;
	private HashMap<State,Integer> optimalPolicy;
	private HashMap<State,Integer> policy;
	private int doubleReward=0;
	private int doubles=0;
	
	public Qlearning(){
		this.qstates = new HashMap();
		for (int k=1; k<11;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if (i%2==0 && i<21 && i>3){
						this.qstates.put(new Qstate(new State(i, 0, k, 1), action), (double) 0);
					}
					if (i>12 && i<22 && action!=3){
						this.qstates.put(new Qstate(new State(i, 1, k, 0), action), (double) 0);
					}
					if (i>3 && i<22 && action!=3){
						this.qstates.put(new Qstate(new State(i, 0, k, 0), action), (double) 0);
					}
				}
				this.qstates.put(new Qstate(new State(12, 1, k, 1), action), (double) 0);
				if (action!=3){
					this.qstates.put(new Qstate(new State(12, 1, k, 0), action), (double) 0);
				}
			}
		}
		for (int k=1; k<14;k++){
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
	    		this.optimalPolicy.put(new State( Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3])), Integer.parseInt(tokens[4]));
	    		line = br.readLine();
	    }
	} finally {
	    br.close();
	}
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
		}

		return this.policy;
	}
	
   private int epsilonGreedy(State state){
		if (new Random().nextDouble() <= this.epsilon){
			if (state.isPair()==0){
				return (int)(Math.random()*3);
			}
			return (int)(Math.random()*4);
		}
		return this.greedy(state);
	}
   
   private int optimalGreedy(State state){
		return this.optimalPolicy.get(state);
	}
	   
	private int greedy(State state){
		double value = -100; 
		int tempAction =0;
		for (int action=0; action<4;action++){
			Qstate key = new Qstate(state,action);
			if (this.qstates.get(key)!=null){
				if (this.qstates.get(key)>value) {
					value = this.qstates.get(key);
					tempAction =action;
				}
			}
			}
		
		return tempAction;
	}
	
	public HashMap<Qstate,Double> finalQstate(){
		this.getOptimalPolicy();
		for(State state: this.policy.keySet()){
			List<Double> out = new ArrayList<Double>();
			//System.out.println(state);
			for(int action=0;action<4;action++){
				Qstate key = new Qstate(state,action);
				//System.out.println(key);
				if (this.qstates.get(key)!=null){
					out.add(this.qstates.get(key));
					//System.out.println("Qstate: " + key + this.qstates.get(key));
				}
			}
			if(out.size()==3){
				out.add(null);
			}
			System.out.println(state + "-" + out);
		}
		return this.qstates;
	
	}
	
	private void playSplit(int action, Blackjack blackjack, Qstate real){
		List<Integer> newList  = blackjack.makeMove(action); //can also get the blackjack objects here
		Blackjack blackjack1 = new Blackjack(newList.get(0),newList.get(1));
		Blackjack blackjack2 = new Blackjack(newList.get(0),newList.get(1));
		List<Integer> hand1 = blackjack1.getState();
		List<Integer> hand2 = blackjack2.getState();
		State state1 = new State(hand1.get(0),hand1.get(1),hand1.get(2),hand1.get(3));
		State state2 = new State(hand2.get(0),hand2.get(1),hand2.get(2),hand2.get(3));
		this.qstates.put(real, (this.qstates.get(real) + alpha*(gamma*(this.qstates.get(new Qstate(state1,this.greedy(state1)))+this.qstates.get(new Qstate(state2,this.greedy(state2))))-this.qstates.get(real))));
		this.play(blackjack1);
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
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.epsilonGreedy(state);
			Qstate real = new Qstate(state,action);
			if(action==3){
				this.playSplit(action, blackjack, real);
				this.splits++;
				break;
			}
			else{
				list = blackjack.makeMove(action);
				this.totalRewards+=list.get(4);
				this.amountRewards+=Math.abs(list.get(4));
				if (Math.abs(list.get(4))==2){
					this.doubleReward += list.get(4);
					this.doubles++;
				}
				//System.out.println("money???????: " + list);
				if(list.get(4)>0){
					//System.out.println("money: " + list);
					this.rewards+=list.get(4);
					this.victory++;
				} else if (list.get(4)<0){
					this.lost ++;
				}
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
				if( newstate.getSum()>21){ 
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real))));	
				} else{
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)+gamma* this.qstates.get(new Qstate(newstate,this.greedy(newstate)))-this.qstates.get(real))));
			}
		}
		}	
	}
	
	
	public static void main(String[] args) throws IOException {

		Qlearning qlearning = new Qlearning();
		qlearning.setOptimalPolicy();
		double prob1 = 0.4;
		double prob2 = 0.4;
		double learningReward = 0;
		System.out.println("TrainingIterations: " + qlearning.trainingIterations);
		while (qlearning.trainingIterations>0){
			//System.out.println(qlearning.trainingIterations);
			qlearning.trainingIterations--;
			//qlearning.alpha = (double) qlearning.trainingIterations/(double) qlearning.i*2;
			//qlearning.epsilon = qlearning.trainingIterations/qlearning.i;
			
//			if(qlearning.trainingIterations==0.7*qlearning.i){
//				qlearning.alpha=qlearning.alpha*0.90;
//				qlearning.epsilon=qlearning.epsilon*0.90;
//				//qlearning.lVictory=qlearning.victory;
//			}
//			if(qlearning.trainingIterations==0.3*qlearning.i){
//				qlearning.alpha=qlearning.alpha*0.10;
//				qlearning.epsilon=qlearning.epsilon*0.10;
//				//qlearning.lVictory=qlearning.victory;
//			}
//			if(qlearning.trainingIterations==0.1*qlearning.i){
//				qlearning.alpha=0.05;
//				qlearning.epsilon=0;
//				//qlearning.lVictory=qlearning.victory;
//			}
//			
//			if(qlearning.trainingIterations==qlearning.i){
//				learningReward = qlearning.rewards;
//			}
			
			qlearning.play();
			//qlearning.epsilon = qlearning.epsilon*0.9999;
			//qlearning.epsilon = qlearning.alpha*0.99999;
		}
		int handsPlayed = qlearning.lost + qlearning.victory;
		System.out.println("Hands played: " + handsPlayed);
		System.out.println("Hands won: " + qlearning.victory);
		System.out.println("Hands lost: " + qlearning.lost);
		System.out.println("Accumulated payoff: " + qlearning.totalRewards);
		System.out.println("total reward given: " + qlearning.amountRewards);
		System.out.println("Sum of positive rewards: " + qlearning.rewards);
		System.out.println("splits: " + qlearning.splits);
		System.out.println("Sum of rewards when played double: " + qlearning.doubleReward);
		System.out.println("Times doubled down: " + qlearning.doubles);
		
		qlearning.lost = 0;
		qlearning.victory = 0;
		qlearning.totalRewards = 0;
		qlearning.amountRewards = 0;
		qlearning.rewards = 0;
		qlearning.splits = 0;
		qlearning.doubleReward = 0;
		qlearning.doubles = 0;
		System.out.println();
		System.out.println("q-values:");
		qlearning.finalQstate();
		qlearning.epsilon = 0;
		qlearning.alpha = 0;
		
		System.out.println();
		System.out.println("Test data:");
		System.out.println("TestIterations: " + qlearning.testIterations);
		while (qlearning.testIterations>0){
			//System.out.println(qlearning.testIterations);
			qlearning.testIterations--;
			qlearning.play();
		
		}
		
		handsPlayed = qlearning.lost + qlearning.victory;
		System.out.println("Hands played: " + handsPlayed);
		System.out.println("Hands won: " + qlearning.victory);
		System.out.println("Hands lost: " + qlearning.lost);
		System.out.println("Accumulated payoff: " + qlearning.totalRewards);
		System.out.println("total reward given: " + qlearning.amountRewards);
		System.out.println("Sum of positive rewards: " + qlearning.rewards);
		System.out.println("splits: " + qlearning.splits);
		System.out.println("Sum of rewards when played double: " + qlearning.doubleReward);
		System.out.println("Times doubled down: " + qlearning.doubles);
		double win = (double) qlearning.victory/handsPlayed;
		System.out.println("Win percentage: " + win);
	}

}

