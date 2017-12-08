package blackjack;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ingrid E. Hermanrud, Petter J. Narvhus
 *
 */
public class Qlearning {
	
	private HashMap<Qstate,Double> qstates;
	private HashMap<State,Integer> optimalPolicy;
	private HashMap<State,Integer> finalPolicy;
	private int trainingIterations = 1000000;
	private int testIterations = 500000;
	private double epsilon = 1;
	private double alpha = 0.01;
	private double gamma =0.05;
	private int totalRewards;
	private int victory;
	private int lost;
	
	/**
	 * Constructor to initialize learning.
	 * Makes a hash-map with all the Q-states and their possible associated actions
	 */ 
	public Qlearning(){
		this.qstates = new HashMap<Qstate,Double>();
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
	}
	
	/**
	 * Constructs the hash-map for the optimal policy as in the figure provided in our report.
	 * Reads from file OptimalPolicy.txt
	 * Make sure to have this text file in this folder!
	 */ 
	private void setOptimalPolicy() throws IOException{
		this.optimalPolicy = new HashMap<>();
		final String dir = System.getProperty("user.dir");
		BufferedReader br = new BufferedReader(new FileReader(dir + "/src/blackjack/OptimalPolicy.txt"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		    	String[] tokens = line.split("-");
		    	this.optimalPolicy.put(new State( Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3])), Integer.parseInt(tokens[4]));
		    	line = br.readLine();
		    }	
		} 
		finally {
			br.close();
		}
	}
	
	/**
	 * Makes and returns the final policy of all states(after learning by us).
	 * Printed in an excel-friendly format
	 * Used to check how close our policy is to the optimal policy. 
	 * Also used to check the "5% slack" we're mentioning in our report
	 * @return hash-map of policy at each state
	 */ 
	public HashMap<State,Integer> getFinalPolicy(){
		this.finalPolicy = new HashMap<State,Integer>();
		for (int k=1; k<11;k++){
			for (int i=1; i<22;i++){
				if (i%2==0 && i<21 && i>3){
					this.finalPolicy.put(new State(i, 0, k, 1), 0);
				}
				if (i>12 && i<22){
					this.finalPolicy.put(new State(i, 1, k, 0), 0);
				}
				if (i>3 && i<22){
				this.finalPolicy.put(new State(i, 0, k, 0), 0);
				}
			}
		this.finalPolicy.put(new State(12, 1, k, 1), 0);
		this.finalPolicy.put(new State(12, 1, k, 0), 0);
		}
		for (State key : this.finalPolicy.keySet()) {
			int action = greedy(key);
			this.finalPolicy.put(key, action);
		}

		return this.finalPolicy;
	}

	/**
	 * Implementation of the epsilon greedy algorithm
	 * Chooses a action at random by probability epsilon
	 * By probability (1-epsilon) it calls greedy and returns the action with the associated maximum Q-value
	 * @param state
	 * @return random policy by probability epsilon, action with highest Q-value by probability (1-epsilon)
	 */ 
	private int epsilonGreedy(State state){
		if (new Random().nextDouble() <= this.epsilon){
			if (state.isPair()==0){
				return (int)(Math.random()*3);
			}
			return (int)(Math.random()*4);
		}
		return this.greedy(state);
	}
	
	/**
	 * Implementation of the greedy algorithm
	 * Chooses the action with the maximum Q-value
	 * @param state
	 * @return action with highest Q-value
	 */ 
	private int greedy(State state){
		double value = -1000000; 
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
	
	/**
	 * Returns the optimal policy from the given state
	 * @param state
	 * @return action from optimal policy
	 */
	private int optimalGreedy(State state){
		return this.optimalPolicy.get(state);
	}
	
	/**
	 * Prints out all the different Q-values for each possible action at each state.
	 * Printed in an excel-friendly format
	 * Used to check how close our policy is to the optimal policy. 
	 * Also used to check the "5% slack" we're mentioning in our report
	 * @return the hash-map of the Q-states
	 */ 
	public HashMap<Qstate,Double> getFinalQstates(){
		this.getFinalPolicy();
		for(State state: this.finalPolicy.keySet()){
			List<Double> out = new ArrayList<Double>();
			for(int action=0;action<4;action++){
				Qstate key = new Qstate(state,action);
				if (this.qstates.get(key)!=null){
					out.add(this.qstates.get(key));
				}
			}
			if(out.size()==3){
				out.add(null);
			}
			System.out.println(state + "-" + out);
		}
		return this.qstates;
	}
	
	/**
	 * Starts a blackjack-hand by initializing a blackjack-object.
	 * Chooses action by epsilonGreedy and perform action by using method makeMove in blackjack class
	 * If action is split, it calls the playSplit method to continue with two hands instead of one
	 * After a move is made, it updates the Q-value using the Q-learning algorithm.
	 * Also counts rewards, splits, doubling downs, victories and losses for analysis
	 * @param blackjack object(a hand)
	 */ 
	private void play(Blackjack blackjack){
		if (blackjack==null){
			blackjack = new Blackjack();
		}
		List<Integer> list = blackjack.getState(); 
		while (list.get(4)==(double)0){	
			State state = new State(list.get(0),list.get(1),list.get(2),list.get(3));
			int action = this.epsilonGreedy(state); //change this to "this.optimalGreedy(state) to run optimal agent
			Qstate real = new Qstate(state,action);
			if(action==3){
				this.playSplit(action, blackjack, real);
				break;
			}
			else{
				list = blackjack.makeMove(action);
				this.totalRewards+=list.get(4);
				if(list.get(4)>0){
					this.victory++;
				} 
				else if (list.get(4)<0){
					this.lost ++;
				}
				State newstate = new State(list.get(0),list.get(1),list.get(2),list.get(3)); 
				if(newstate.getSum()>21){ 
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)-this.qstates.get(real))));	
				} 
				else{
					this.qstates.put(real, (this.qstates.get(real) + alpha*(list.get(4)+gamma* this.qstates.get(new Qstate(newstate,this.greedy(newstate)))-this.qstates.get(real))));
				}
			}
		}	
	}
	
	/**
	 * Method used if first step of a hand. 
	 * Else the play-method is called with a parameter after splitting
	 */ 
	private void play(){
		play(null);
	}
	
	/**
	 * Starts two new blackjack hands after splitting a hand
	 * Calls the play-method and plays the two hands separately
	 * Also updates the Q-value for the previous step when choosing the action split
	 * @param action
	 * @param blackjack object(a hand)
	 * @param Q-state
	 */ 
	private void playSplit(int action, Blackjack blackjack, Qstate real){
		List<Integer> newList  = blackjack.makeMove(action);
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

	
	/**
	 * Main method for running the game.
	 * First we train the agent for an amount of iterations
	 * Then we test the agent
	 * When testing the agent, we set epsilon to zero to only choose the action with maximum Q-value.
	 * We also set alpha to zero to stop the learning process
	 */ 
	public static void main(String[] args) throws IOException {
		Qlearning qlearning = new Qlearning();
		qlearning.setOptimalPolicy();
		System.out.println("Amount of training iterations: " + qlearning.trainingIterations);
		System.out.println("Training....");
		while (qlearning.trainingIterations>0){
			qlearning.trainingIterations--;
			qlearning.play();	
		}
		System.out.println("Done training");
		int handsPlayed = qlearning.lost + qlearning.victory;
		qlearning.lost = 0;
		qlearning.victory = 0;
		qlearning.totalRewards = 0;
		System.out.println();

		//IF YOU WANT TO CHECK HOW OUR POLICY DOES COMPARED TO THE OPTIMAL, PLEASE COMMENT THIS OUT AND FOLLOW THE INSTRUCTIONS IN THE REPORT
		//System.out.println("Please copy all the data down until it says Test data");
		//qlearning.getFinalQstates();
		
		qlearning.epsilon = 0; //Please toggle this comment to run a random agent
		qlearning.alpha = 0;
		System.out.println();
		System.out.println("Test data:");
		System.out.println("Amount of test iterations: " + qlearning.testIterations);
		System.out.println("Testing....");
		while (qlearning.testIterations>0){
			qlearning.testIterations--;
			qlearning.play();
		}
		handsPlayed = qlearning.lost + qlearning.victory;
		System.out.println("Total hands played, including splitted hands: " + handsPlayed);
		System.out.println("Hands won: " + qlearning.victory);
		System.out.println("Hands lost: " + qlearning.lost);
		System.out.println("Total pay off on test hands:" + qlearning.totalRewards);
		double win = (double) qlearning.victory/handsPlayed;
		System.out.println("Win percentage(percentage of hands won): " + win * 100);
	}

}

