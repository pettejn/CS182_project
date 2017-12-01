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
	private int iterations = 10;
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

		for (int k=1; k<14;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if(action == 2 && i<11){
						continue;
					}
					if (i%2==0){
						this.states.put(new Qstate(new State(i, 0, k, 1), action), (double) 0);

					}
					if (i>1 && i<22){
						this.states.put(new Qstate(new State(i, 1, k, 0), action), (double) 0);
					}
					if (i>4 && i<22){
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
		for (int k=1; k<14;k++){
			for (int action=0; action<4;action++ ){
				for (int i=1; i<22;i++){
					if(action == 2 && i<11){
						continue;
					}
					if (i%2==0){
						this.optimalPolicy.put(new State(i, 0, k, 1), (int) 0);

					}
					if (i>1 && i<22){
						this.optimalPolicy.put(new State(i, 1, k, 0), (int) 0);
					}
					if (i>4 && i<22){
						this.optimalPolicy.put(new State(i, 0, k, 0), (int) 0);
					}

				}
				this.optimalPolicy.put(new State(2, 1, k, 1), (int) 0);
			}
		}
		
		
		
		for (State key : this.optimalPolicy.keySet()) {
			int action = greedy(key);
			this.optimalPolicy.put(key, action);
			System.out.println("this is action to put on key: " + key + "with action "+ optimalPolicy.get(key));
		}

		return this.optimalPolicy;
	}
	

//	public HashMap<State,Integer> getOptimalPolicy(){
//		this.optimalPolicy = new HashMap();
//		for (int k=1; k<11;k++){
//			for (int i=4; i<22;i++){
//				if (i%2==0){
//					this.optimalPolicy.put(new State(i, 0, k, 1), (int) 0);
//				}
//				if (i>13 && i<22){
//					this.optimalPolicy.put(new State(i, 1, k, 0), (int) 0);
//				}
//				if (i>4 && i<22){
//					this.optimalPolicy.put(new State(i, 0, k, 0), (int) 0);
//				}
//			}
//			this.optimalPolicy.put(new State(2, 1, k, 1), (int) 0);
//		}
//		//System.out.println(states);
////
////		for(int i=2; i<22; i++){
//		
////			for (int j=1; j<14; j++){
////				for (int k=0; k<2;k++){
////					this.optimalPolicy.put(new State(i, k, j), (int) 0);
////				}
////			}
////		}
//		return this.optimalPolicy;
//	}
	
	//choose random action with probability epsilon, else argmax Q(s,a)
	private int epsilonGreedy(double epsilon, State state){
		if (new Random().nextDouble() <= epsilon){
			if(state.getSum()>=11 && state.isPair()>0 && this.split==0){//YOU CAN DO ALL ACTIONS
				//System.out.println("All acti");
				double y = new Random().nextDouble();
				//System.out.println("y"+ y);
				if (y <=0.25){ 
					return 0;
				}
				if(y > 0.25 && y <= 0.5){
					return 1;
				}
				if(y > 0.5 && y <= 0.75){
					return 2;
				}
				else{
					//System.out.println("her er vi 2");
					return 3;
				}
			}
			if(state.getSum()<11 && state.isPair()==0){//YOU CAN ONLY HIT OR STAND
				//System.out.println("only two acti");
				if (new Random().nextDouble() <=0.5){ 
					return 0;
				}
				else{
					return 1;
				}
			}
			if(state.getSum()>=11){//the case is that you can double but not split(or else the two above would work)
				//System.out.println("double good");
				double y = new Random().nextDouble();
				//System.out.println("y"+ y);
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
			if(state.isPair()>0 && this.split==0){ //you have a pair and can split
				//System.out.println("split good");
				double y = new Random().nextDouble();
				//System.out.println("y"+ y);
				if (y <=0.35){ 
					return 0;
				}
				if(y > 0.35 && y < 0.7){
					return 1;
				}
				else{
					//System.out.println("her er vi 1");
					return 3;
				}
			}
			if(state.isPair()>0 && this.split==1){
				//System.out.println("split no good");
				if (new Random().nextDouble() <=0.5){ 
					return 0;
				}
				else{
					return 1;
				}
			}
			else{
				//System.out.println("HER ER VI");
				if (new Random().nextDouble() <=0.5){ 
					return 0;
				}
				else{
					return 1;
				}
			}
		}
		//System.out.println("her er vi");
		return this.greedy(state);
	}
	
	private int greedy2(State state){
		return this.states.entrySet().stream().filter(map -> map.getKey().getState().equals(state)).
				max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey().getAction();

	}
	
	private int greedy(State state){
		//System.out.println("HER");
		double value = -100; 
		int action = 0;
		for (Qstate key : this.states.keySet()) {
			if (key.equals(this.findQstate(new Qstate(state, key.getAction()))) && this.states.get(key)>value){
				if(key.getAction()==3 && this.split==1){
					return action;
				}
				value = this.states.get(key);
				action = key.getAction();
				//System.out.println(action);
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
			//System.out.println("sum"+state.getSum());
			//System.out.println("pair"+state.isPair());
			//System.out.println("split"+this.split);
			int action = this.epsilonGreedy(this.epsilon, state);
			//System.out.println("action" + action);
			Qstate qstate = new Qstate(state, action);
			Qstate real = this.findQstate(qstate);
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
			this.states.put(real, (this.states.get(real) + alpha*(newList.get(4) + gamma*Math.max(this.states.get(this.findQstate( new Qstate(newstate, 0))), this.states.get(this.findQstate(new Qstate(newstate, 1)))))-this.states.get(real)));
			}
		}	
		return value;
	}

	private void play(){
		this.split=0;
		Blackjack blackjack = new Blackjack();
		List<Integer> list = blackjack.getState(); //first state
		//for(int i=1;i<3;i++)
		while (list.get(4)==(double)0 && this.split==0){	
			//System.out.println("REWARD " + list.get(4));
			//System.out.println("split top "+ this.split);
			list = blackjack.getState();
			//System.out.println("list " + list);
			this.rewards+=list.get(4);
			if(list.get(4)>0){
				this.victory++;
			}
			State state = new State(list.get(0),list.get(1),list.get(2),Math.min(list.get(3),1));
			//System.out.println("state" + state);
			//System.out.println(state);
			System.out.println("split: "+ this.split);
			int action = this.epsilonGreedy(this.epsilon, state);
			System.out.println("action: " + action);
			Qstate qstate = new Qstate(state, action);
			System.out.println("qstate here " + qstate);
			Qstate real = this.findQstate(qstate);//DEALERCARD CHANGES!!!
			//System.out.println("real" + real);
			if (real == null){
				System.out.println("you breaked because real==null ");
				break;
			}
			//System.out.println("real" + real);
			if (list.get(4)!=0){
				System.out.println("ended cause reward not 0");
				this.states.put(real, ((this.states.get(real) + alpha*(list.get(4)-this.states.get(real)))));
				break;
			}
			if(action==3){
				//trenger bare faa inn card and dealercard
				List<Integer> newList  = blackjack.makeMove(action); //can also get the blackjack objects here
				//List<Integer> hand1 = newList.subList(0, 5);
				//List<Integer> hand2 = newList.subList(5, newList.size());
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
				//System.out.println("reward" + list.get(4));
				State newstate = new State(list.get(0),list.get(1),list.get(2),Math.min(list.get(3),1)); 
				System.out.println("new state after action: " + newstate);
				System.out.println("dealerSum: " + blackjack.getDealerSum());
				System.out.println("full state: " + list);
				if( newstate.getSum()>21){ //use gameover here?
					//is it wrong to put list here? shouldn it be newlist?
					System.out.println("old value: " + this.states.get(real));
					System.out.println("updated value: " + this.states.get(real) + alpha*(list.get(4)-this.states.get(real)));
					this.states.put(real, ((this.states.get(real) + alpha*(list.get(4)-this.states.get(real)))));	
				} else{
					//System.out.println(newstate);
					//System.out.println(this.findQstate( new Qstate(newstate, 0)));
					System.out.println("old value: " + this.states.get(real));
					double y = (this.states.get(real) + alpha*(list.get(4) + gamma*Math.max(this.states.get(this.findQstate( new Qstate(newstate, 0))), this.states.get(this.findQstate(new Qstate(newstate, 1)))))-this.states.get(real));
					System.out.println("updated value: " + y);
				this.states.put(real, (this.states.get(real) + alpha*(list.get(4) + gamma*Math.max(this.states.get(this.findQstate( new Qstate(newstate, 0))), this.states.get(this.findQstate(new Qstate(newstate, 1)))))-this.states.get(real)));
				//have to modify this one to take action=2 into consideration
			}
		}
		}	
		}

	
	//Takes a state as input, returns corresponding qstate in our hashmap.
	//if it doesnt exist, return null.
	// Returns a Qstate-object
	private Qstate findQstate(Qstate state){
		for (Qstate name: this.states.keySet()){
			if (name.getState().isPair()==state.getState().isPair() && name.getState().getSum()==state.getState().getSum() && name.getAction()==state.getAction() && name.getState().getAce()==state.getState().getAce() && name.getState().getDealer()==state.getState().getDealer()){
				return name;
			}
		}	
		return null;
		
	}
	
	public static void main(String[] args) {
		Qlearning qlearning = new Qlearning();
		while (qlearning.iterations>0){
			qlearning.iterations--;
			if( qlearning.iterations==1000){
				qlearning.alpha=0.05;
				qlearning.epsilon=0.05;
			}
			qlearning.play();
			//System.out.println(qlearning.states.values());
//			for (Qstate name: qlearning.states.keySet()){
//
//            String key =name.toString();
//            String value = qlearning.states.get(name).toString(); 
//            if(qlearning.states.get(name)!=0){
//            System.out.println(key + " " + value);  
//            System.out.println(qlearning.rewards);
//            }
//		}
		//System.out.println(qlearning.rewards);
		//System.out.println(qlearning.states.size());
		//System.out.println(qlearning.victory);
		
		}
		//qlearning.getOptimalPolicy();
//		for (Qstate name: qlearning.states.keySet()){
//		    //String value = qlearning.states.get(name).toString(); 
//				if(name.getAction()==3){
//					System.out.println(name);
//					System.out.println("VALUE ON DOUBLE:" + qlearning.states.get(name));
//				}
//		}
		
	}

}

