package blackjack;
	

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.Random;
	

	public class Blackjack {
		
		private int playerSum = 0;
		private int dealerCard = 0;
		private int playerAce = 0;
		private int dealerAce = 0;
		private boolean PlayersTurn = true;
		private int dealerSum = 0;
		private int doubleReward = 1;
		private int pair = 0; //value of card in pair
	

		//normal constructor 
		public Blackjack(){
			int card1 = getCard();
			int card2 = getCard();
			this.pair = checkPair(card1,card2);
			// System.out.println("card1 is " + card1);
			// System.out.println("card2 is " + card2);
			this.playerSum = Math.min(getValue(card1) + getValue(card2), 18);
			// System.out.println("player sum first is:" + playerSum);
			if(checkAce(card1)==1||checkAce(card2)==1){
				this.playerAce = 1;
			}
			if(this.playerAce==1){
				this.playerSum += 10;
			}
			//System.out.println("player sum ace is:" + playerSum);
			this.dealerCard = getCard();
			//System.out.println("dealercard is " + dealerCard);
			this.dealerSum = getValue(this.dealerCard);
			//System.out.println("dealer sum first is:" + dealerSum);
			if(checkAce(this.dealerCard)==1){
				this.dealerAce = 1;
			}
			if(this.dealerAce==1){
				this.dealerSum += 10;
			}
			//System.out.println("dealer sum ace is:" + dealerSum);
			
		}
		
		//constructor 2 for splitting.
		public Blackjack(int card, int dealerCard){
			int card2 = getCard();
			this.pair = checkPair(card,card2);
			this.playerSum = Math.min(18, getValue(card) + getValue(card2));
			if(checkAce(card)==1||checkAce(card2)==1){
				this.playerAce = 1;
			}
			if(this.playerAce==1){
				this.playerSum += 10;
			}
			this.dealerCard = dealerCard;
			this.dealerSum = getValue(dealerCard);
			if(checkAce(this.dealerCard)==1){
				this.dealerAce = 1;
			}
			if(this.dealerAce==1){
				this.dealerSum += 10;
			}
		}
		
		
		public List<Integer> getState(){
			int reward=getReward();
			ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,pair,reward));
			return output;
		}
		
		public int checkPair(int card1, int card2){
			if (card1 == card2){
				return card1;
			}
			return 0;
		}
		
		public int getCard() {
			Random rand = new Random(); 
			int cardValue = rand.nextInt(13) + 1;
			return cardValue;
		}
		
		//return list of two new objects. 
		//if not possible to split,return a list with the this blackjack object
		public List<Integer> Split(){
			if(this.pair>0){
				//Blackjack split1 = new Blackjack(this.pair, this.dealerCard);
				//Blackjack split2 = new Blackjack(this.pair, this.dealerCard);
				//ArrayList<Blackjack> output = new ArrayList<Blackjack>(Arrays.asList(split1,split2));
				//return output;
				ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(this.pair,this.dealerCard));
				return output;
			}
			ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,pair,this.getReward()));
			return output;
		}
		
		public void Hit(){
			if(!gameOver() && this.PlayersTurn==true){
				int card = getCard();
				this.playerSum += getValue(card);
				if(this.playerSum > 21 && this.playerAce == 1){
					this.playerSum -= 10;
					this.playerAce = 0;
				}
				if(this.playerSum>21){
					this.PlayersTurn=false;
				}
				this.playerSum=Math.min(playerSum, 18);
			}
		}
		
		public void Stand(){
			this.PlayersTurn = false;
		}
		
		public void DealerHit(){
			this.PlayersTurn=false; //if petter choose to hit on dealer, means player are done.
			int card = getCard();
			this.dealerSum += getValue(card);
			if(this.dealerSum > 21 && this.dealerAce == 1){
				this.dealerSum -= 10;
				this.dealerAce = 0;
			}
		}
		
		public void Double(){
			if(this.playerSum>=11){
				this.Hit();
				this.Stand();
				this.doubleReward = 2;
			}
		}
		
		public boolean gameOver(){
			if((this.PlayersTurn == false && this.dealerSum >= 17)||this.playerSum > 21){
				return true;
			}
			return false;
		}
		
		public int getReward(){
			if(gameOver()){
				if(this.playerSum>21){
					return -1*this.doubleReward;
				}
				if(this.playerSum<= 21 && this.dealerSum>21){
					return 1*this.doubleReward;
				}
				if((this.dealerSum>=this.playerSum)&&(this.dealerSum<=21)&&(this.playerSum<=21)){
					return -1*this.doubleReward;
				}
				if((this.playerSum>=this.dealerSum)&&(this.playerSum<=21)&&(this.dealerSum<=21)){
					return 1*this.doubleReward;
				}
			}
			return 0;
		}
		
		public int getValue(int card){
			if(card >= 10 && card <=13){
				return 10;
			}
			else{
				return card;
			}
		}
		
		public int checkAce(int card){
			if(card==1){
				return 1;
			}
			return 0;
		}
		
		public int getRandomAction(){
			Random rand = new Random(); 
			int action = rand.nextInt(2) + 0;
			return action;
		}
	

		public List<Integer> makeMove(int action){
			if(action == 0){
				this.Hit();
				int reward=getReward();
				ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,pair,reward));
				//System.out.println("you hit. Your card were "+ this.playerSum + "your reward were " + reward);
				return output;
			}
			if(action == 1){
				this.Stand();
				while(this.dealerSum<17 && this.playerSum<=21){
					this.DealerHit();
				}
				int reward=getReward();
				ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,pair,reward));
				//System.out.println("you stand. Your card were "+ this.playerSum + "your reward were " + reward);
				return output;
			}
			if(action ==2){ //double. Have to figure out how to count in rewards here
				this.Double();
				while(this.dealerSum<17 && this.playerSum<=21){
					this.DealerHit();
				}
				int reward=getReward();
				ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,pair,reward));
				//System.out.println("you doubled. Your card were "+ this.playerSum + "your reward were " + reward);
				return output;
			}
			
			else{//if action = 3 aka SPLIT
				List<Integer> splitList = this.Split();
				return splitList;
				//if valid: get a list with two objects(split1 , split2)
				//if not valid, returns the state you were in
				//if(splitList.size()==2){ //if you didnt manageto split
				//	return splitList;
				//}
				//Blackjack hand1 = splitList.get(0); //
				//Blackjack hand2 = splitList.get(1); //
				//List<Integer> state1 = hand1.getState();
				//List<Integer> state2 = hand2.getState();
				//int reward1= hand1.getReward();
				//int reward2= hand2.getReward();
				//state1.add(reward1);
				//state1.addAll(state2);
				//state1.add(reward2);
				//return state1; //(playerSum,playerAce,dealerCard,pair,reward1,playerSum,playerAce,dealerCard,pair,reward2), size=10
			
			}
		}
		
			
			//else{
			//	List<Blackjack> splitList = this.Split();
			//	if(splitList.size()==1){
			//		ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,reward));
			//		return output;
			//	}
			//}

		public static void main(String[] args) {
			Blackjack game = new Blackjack();
			while(game.PlayersTurn == true){
				int action = game.getRandomAction();
				if(action == 0){
					//System.out.println("hitting");
					game.Hit();
					//System.out.println("new player sum:" + game.playerSum);
				}
				if(action == 1){
					//System.out.println("standing");
					game.Stand();
				}
			}
			System.out.println("Dealers turn");
			while(game.dealerSum<17 && game.playerSum<=21){
				game.DealerHit();
				//System.out.println("dealersum afer hit: " + game.dealerSum);
			}
			int reward = game.getReward();
			//System.out.println("Reward:" + reward);
		}
	

	}