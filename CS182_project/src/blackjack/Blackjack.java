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

	public Blackjack(){
		int card1 = getCard();
		int card2 = getCard();
		// System.out.println("card1 is " + card1);
		// System.out.println("card2 is " + card2);
		this.playerSum = getValue(card1) + getValue(card2);
		// System.out.println("player sum first is:" + playerSum);
		if(checkAce(card1)==1||checkAce(card2)==1){
			this.playerAce = 1;
		}
		if(this.playerAce==1){
			this.playerSum += 10;
		}
		System.out.println("player sum ace is:" + playerSum);
		this.dealerCard = getCard();
		System.out.println("dealercard is " + dealerCard);
		this.dealerSum = getValue(this.dealerCard);
		System.out.println("dealer sum first is:" + dealerSum);
		if(checkAce(this.dealerCard)==1){
			this.dealerAce = 1;
		}
		if(this.dealerAce==1){
			this.dealerSum += 10;
		}
		System.out.println("dealer sum ace is:" + dealerSum);
		
	}
	
	public List<Integer> getState(){
		int reward=getReward();
		ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,reward));
		return output;
	}
	
	public int getCard() {
		Random rand = new Random(); 
		int cardValue = rand.nextInt(13) + 1;
		return cardValue;
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
		}
	}
	
	public void Stand(){
		// if(!gameOver()){
		this.PlayersTurn = false;
	}
	
	public void DealerHit(){
		// if(!gameOver()){
		int card = getCard();
		this.dealerSum += getValue(card);
		if(this.dealerSum > 21 && this.dealerAce == 1){
			this.dealerSum -= 10;
			this.dealerAce = 0;
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
				return -1;
			}
			if(this.playerSum<= 21 && this.dealerSum>21){
				return 1;
			}
			if((this.dealerSum>=this.playerSum)&&(this.dealerSum<=21)&&(this.playerSum<=21)){
				return -1;
			}
			if((this.playerSum>=this.dealerSum)&&(this.playerSum<=21)&&(this.dealerSum<=21)){
				return 1;
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
	
	public int returnReward(int action){
		if(this.PlayersTurn == true){
			if(action == 0){
				this.Hit();
				int reward = this.getReward();
				if(reward != 0){
					return reward;
				}
				return (this.playerSum);
			}
			if(action == 1){
				this.Stand();
			}
		}
		while(this.dealerSum<17 && this.playerSum<=21){
			this.DealerHit();
					// System.out.println("dealersum afer hit: " + this.dealerSum);
		}
		int reward = this.getReward();
		return reward;
	}
	
	//public State getState(int action){
		
	//}
	
	public List<Integer> makeMove(int action){
		if(action == 0){
			this.Hit();
			int reward=getReward();
			ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,reward));
			return output;
		}
		else{
			this.Stand();
			while(this.dealerSum<17 && this.playerSum<=21){
				this.DealerHit();
			}
			int reward=getReward();
			ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(playerSum, playerAce, dealerCard,reward));
			return output;
			
		}
	}
	
	public static void main(String[] args) {
		Blackjack game = new Blackjack();
		while(game.PlayersTurn == true){
			int action = game.getRandomAction();
			if(action == 0){
				System.out.println("hitting");
				game.Hit();
				
				System.out.println("new player sum:" + game.playerSum);
			}
			if(action == 1){
				System.out.println("standing");
				game.Stand();
			}
		}
		System.out.println("Dealers turn");
		while(game.dealerSum<17 && game.playerSum<=21){
			game.DealerHit();
			System.out.println("dealersum afer hit: " + game.dealerSum);
		}
		int reward = game.getReward();
		System.out.println("Reward:" + reward);
	}

}
	
	