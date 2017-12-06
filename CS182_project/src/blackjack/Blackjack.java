package blackjack;
	

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.Random;
	

	public class Blackjack {
		
		private int playerSum = 0; //nb, player sum never bigger than 18
		private int dealerCard = 0;
		private int playerAce = 0;
		private int dealerAce = 0;
		private boolean PlayersTurn = true;
		private int dealerSum = 0;
		private int doubleReward = 1;
		private int pair = 0; //value of card in pair.
		private int playerCard;
		private int split = 0;
	

		//normal constructor 
		public Blackjack(){
			this.playerCard = getCard();
			int card2 = getCard();
			//System.out.println("card 1: " + this.playerCard);
			//System.out.println("card 2: " + card2);
			this.pair = checkPair(this.playerCard,card2);
			this.playerSum = getValue(this.playerCard) + getValue(card2);
			if( this.playerSum==22){
				this.playerSum = 12;
			}
			if(checkAce(this.playerCard)==1||checkAce(card2)==1){
				this.playerAce = 1;
			}
			this.dealerCard = getCard();
			this.dealerSum = getValue(this.dealerCard);
			if(checkAce(this.dealerCard)==1){
				this.dealerAce = 1;
			}
		}
		
		//constructor 2 for splitting.
		public Blackjack(int card, int dealerCard){
			int card2 = getCard();
			//System.out.println("card 1: " + card);
			//System.out.println("card 2: " + card2);
			this.pair = 0;
			this.split = 1;
			this.playerSum = getValue(card) + getValue(card2);
			if( this.playerSum==22){
				this.playerSum = 12;
			}
			if(checkAce(card)==1||checkAce(card2)==1){
				this.playerAce = 1;
			}
			this.dealerCard = dealerCard;
			this.dealerSum = this.getValue(dealerCard);
			if(checkAce(this.dealerCard)==1){
				this.dealerAce = 1;
			}
		}
		
		public int getDealerSum(){
			return this.dealerSum;
		}
		
		
		public List<Integer> getState(){
			int reward=getReward();
			ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(this.playerSum, this.playerAce, Math.min(10, this.dealerCard) ,this.pair,reward));
			return output;
		}
		
		public int checkPair(int card1, int card2){
			if (card1 == card2){
				return 1;
			}
			return 0;
		}
		
		public int getCard() {
			Random rand = new Random(); 
			int cardValue = rand.nextInt(13) + 1;
			return cardValue;
		}
		
		
		public List<Integer> Split(){
			return new ArrayList<Integer>(Arrays.asList(this.playerCard,Math.min(10, this.dealerCard)));
		}
		
		public void Hit(){
			this.pair = 0;
			if(!gameOver() && this.PlayersTurn==true){
				int card = getCard();
				if(this.playerAce==0){
					this.playerAce = checkAce(card);
				}
				this.playerSum += getValue(card);
				if(this.playerSum > 21 && this.playerAce == 1){
					this.playerSum -= 10;
					this.playerAce = 0;
				}
				if(this.playerSum>21){
					this.PlayersTurn=false;
				}
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
			
				this.Hit();
				this.Stand();
				this.doubleReward = 2;
		}
		
		public boolean gameOver(){
			if((this.PlayersTurn == false && this.dealerSum >= 17)||this.playerSum > 21){
				return true;
			}
			return false;
		}
		
		public int getReward(){
			if(gameOver()){
				return (this.playerSum < 22 ? (this.dealerSum < 22 ? (this.dealerSum >= this.playerSum ? -1*this.doubleReward : 1*this.doubleReward) : 1*this.doubleReward):-1*this.doubleReward);
			}
			return 0;
		}
		
		public int getValue(int card){
			if (card==1){
				return 11;
			}
			return Math.min(card, 10);
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
				return this.getState();
			}
			if(action == 1){
				//System.out.println("standing");
				this.Stand();
				while(this.dealerSum<17 && this.playerSum<=21){
					this.DealerHit();
				}
				//System.out.println("Dealersum: " + this.dealerSum);
				return this.getState();
			}
			if(action == 2){ //double. Have to figure out how to count in rewards here
				this.Double();
				while(this.dealerSum<17 && this.playerSum<=21){
					this.DealerHit();
				}
				//System.out.println("Dealersum: " + this.dealerSum);
				return this.getState();
			}
			else{//if action = 3 aka SPLIT
				return this.Split();
			
			}
		}
		
			

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
			//System.out.println("Dealers turn");
			while(game.dealerSum<17 && game.playerSum<=21){
				game.DealerHit();
				//System.out.println("dealersum afer hit: " + game.dealerSum);
			}
			int reward = game.getReward();
			//System.out.println("Reward:" + reward);
		}
	

	}
