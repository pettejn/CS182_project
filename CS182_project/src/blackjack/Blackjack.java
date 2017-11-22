package blackjack;

import java.util.Random;

public class Blackjack {
	
	private int playerSum = 0;
	private int dealerCard = 0;
	private boolean playerAce = false;
	private boolean dealerAce = false;
	private boolean PlayersTurn = true;
	private int dealerSum = 0;

	public Blackjack(){
		int card1 = getCard();
		int card2 = getCard();
		this.playerSum = getValue(card1) + getValue(card2);
		if(checkAce(card1)||checkAce(card2)){
			this.playerAce = true;
		}
		if(this.playerAce=true){
			this.playerSum += 10;
		}
		this.dealerCard = getCard();
		this.dealerSum = getValue(this.dealerCard);
		if(checkAce(this.dealerCard)){
			this.dealerAce = true;
		}
		if(this.dealerAce=true){
			this.dealerSum += 10;
		}
		
	}
	
	public int getCard() {
		Random rand = new Random(); 
		int cardValue = rand.nextInt(13) + 1;
		System.out.println("card is " + cardValue);
		return cardValue;
	}
	
	public void Hit(){
		if(!gameOver()){
			int card = getCard();
			this.playerSum += getValue(card);
			if(this.playerSum > 21 && this.playerAce == true){
				this.playerSum -= 10;
				this.playerAce = false;
			}
			
		}
	}
	
	public void Stand(){
		if(!gameOver()){
			this.PlayersTurn = false;
		}
	}
	
	public void DealerHit(){
		if(!gameOver()){
			int card = getCard();
			this.dealerSum += getValue(card);
			if(this.dealerSum > 21 && this.dealerAce == true){
				this.dealerSum -= 10;
				this.dealerAce = false;
			}
			
		}
	}
	
	public boolean gameOver(){
		if((this.PlayersTurn = false && this.dealerSum >= 17)||this.playerSum > 21){
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
	
	public boolean checkAce(int card){
		if(card==1){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		Blackjack game = new Blackjack();
		System.out.println("sum is:" + game.playerSum);
		// while(!game.gameOver()){
			
		//}
	}

}
	