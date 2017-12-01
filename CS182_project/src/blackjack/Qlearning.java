package blackjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Qlearning {

	private HashMap<Qstate,Double> states;
	private int iterations = 50000;
	private double epsilon = 0.8;
	private double alpha = 0.8;
	private double gamma =0.8;
	private int rewards=0;
	private int victory=0;
	private HashMap<State,Integer> optimalPolicy;

	public Qlearning(){
		this.states = new HashMap();
		//	 for(int i=2; i<22; i++){
		//		 for (int j=1; j<14; j++){
		//			 for (int action=0; action<3; action++){
		//				 if(action == 2 && i<11){
		//					 break;
		//				 }
		//				 for (int k=0; k<2;k++){
		//					 states.put(new Qstate(new State(i, k, j), action), (double) 0);
		//			 //her må vi legge til action "2" for alle par som er like
		//			 
		//				}
		//			 }
		//		 }
		//	 }

		for (int k=1; k<11;k++){

			for (int action=0; action<4;action++ ){
				for (int i=4; i<21;i++){
					if(action == 2 && i<11){
						break;
					}
					if (i%2==0){
						this.states.put(new Qstate(new State(i, 0, k, 1), action), (double) 0);

					}
					if (i>13 && i<21){
						this.states.put(new Qstate(new State(i, 1, k, 0), action), (double) 0);
					}
					if (i>4 && i<19){
						this.states.put(new Qstate(new State(i, 0, k, 0), action), (double) 0);
					}

				}
				this.states.put(new Qstate(new State(2, 1, k, 1), action), (double) 0);

			}
		}
		//System.out.println(states);
	}

	public HashMap<State,Integer> getOptimalPolicy(){
		this.optimalPolicy = new HashMap();
		for (int k=1; k<11;k++){

				for (int i=4; i<21;i++){
					if (i%2==0){
						this.optimalPolicy.put(new State(i, 0, k, 1), (int) 0);

					}
					if (i>13 && i<21){
						this.optimalPolicy.put(new State(i, 1, k, 0), (int) 0);
					}
					if (i>4 && i<19){
						this.optimalPolicy.put(new State(i, 0, k, 0), (int) 0);
					}

				}
				this.optimalPolicy.put(new State(2, 1, k, 1), (int) 0);

		}
		//System.out.println(states);
//
//		for(int i=2; i<22; i++){
		
//			for (int j=1; j<14; j++){
//				for (int k=0; k<2;k++){
//					this.optimalPolicy.put(new State(i, k, j), (int) 0);
//				}
//			}
//		}
		for (State key : this.optimalPolicy.keySet()) {
			int action = greedy(key);
			this.optimalPolicy.put(key, action);
			if (key.getSum()==15){
			System.out.println("this is action to put on key: " + key + "with action "+ optimalPolicy.get(key));
			}
		}

		return this.optimalPolicy;
	}

	//choose random action with probability epsilon, else argmax Q(s,a)
	private int epsilonGreedy(double epsilon, State state){
		if (new Random().nextDouble() <= epsilon){
			if(state.getSum()<11){
				if (new Random().nextDouble() <=0.5){ 
					return 0;
				}
				else{
					return 1;
				}
			}
			else{
				double y = new Random().nextDouble();
				if (y <=0.35){ 
					return 0;
				}
				if(y > 0.35 && y < 0.7){
					return 1;
				}
				else{
					return 2;
				}
			}
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
				//System.out.println(action);
			}

		}

		return action;
	}

	private void play(){
		Blackjack blackjack = new Blackjack();
		List<Integer> list = blackjack.getState(); //first state
		while (list.get(3)==(double) 0){	
			list = blackjack.getState();
			this.rewards+=list.get(3);
			if(list.get(3)==1 || list.get(3)==2){
				this.victory++;
			}
			State state = new State(list.get(0),list.get(1),list.get(2));
			//System.out.println(state);
			int action = this.epsilonGreedy(this.epsilon, state);
			Qstate qstate = new Qstate(state, action);
			Qstate real = this.findQstate(qstate);
			if (real == null){
				break;
			}
			//System.out.println("q" +qstate);
			//System.out.println("real" + real);
			if (list.get(3)!=0){
				this.states.put(real, ((this.states.get(real) + alpha*(list.get(3)-this.states.get(real)))));
				break;
			}
			List<Integer> newList = blackjack.makeMove(action);
			State newstate = new State(newList.get(0),newList.get(1),newList.get(2)); 
			//System.out.println("new " + newstate);
			if( newstate.getSum()>21){ //use gameover here?
				//is it wrong to put list here? shouldn it be newlist?
				this.states.put(real, ((this.states.get(real) + alpha*(newList.get(3)-this.states.get(real)))));	
			} else{
				this.states.put(real, (this.states.get(real) + alpha*(newList.get(3) + gamma*Math.max(this.states.get(this.findQstate( new Qstate(newstate, 0))), this.states.get(this.findQstate(new Qstate(newstate, 1)))))-this.states.get(real)));
				//have to modify this one to take action=2 into consideration
			}
		}	
	}


	//Takes a state as input, returns corresponding qstate in our hashmap.
	//if it doesnt exist, return null.
	// Returns a Qstate-object
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
		
//		while (qlearning.iterations>0){
//			qlearning.iterations--;
//			if( qlearning.iterations==20000){
//				qlearning.alpha=0.01;
//				qlearning.epsilon=0.01;
//			}
//			qlearning.play();
//			//System.out.println(qlearning.states.values());
//			//			for (Qstate name: qlearning.states.keySet()){
//			//
//			//            String key =name.toString();
//			//            String value = qlearning.states.get(name).toString(); 
//			//            if(qlearning.states.get(name)!=0){
//			//            System.out.println(key + " " + value);  
//			//            System.out.println(qlearning.rewards);
//			//            }
//			//		}
//			System.out.println(qlearning.rewards);
//			//System.out.println(qlearning.states.size());
//			System.out.println(qlearning.victory);
//
//		}
		qlearning.getOptimalPolicy();
	}

}

