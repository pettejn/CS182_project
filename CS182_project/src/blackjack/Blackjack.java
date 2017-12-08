package blackjack;
	

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * @author Ingrid E. Hermanrud, Petter J. Narvhus
 *
 */
public class Blackjack {
	
	private int playerSum;
	private int dealerCard;
	private int playerAce;
	private int dealerAce;
	private boolean PlayersTurn = true;
	private int dealerSum;
	private int doubleReward = 1;
	private int pair;
	private int playerCard;

	/**
	 * Constructor to initialize a blackjack-hand.
	 */ 
	public Blackjack(){
		this.playerCard = getCard();
		int card2 = getCard();
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
	
	/**
	 * Constructor to initialize a blackjack-hand after splitting
	 * @param card
	 * @param dealerCard
	 */ 
	public Blackjack(int card, int dealerCard){
		int card2 = getCard();
		this.pair = 0;
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
	
	public int getPlayerSum(){
		return this.playerSum;
	}
	
	/**
	 * Returns a list of the parameters of the current state
	 * @return the current state
	 */ 
	public List<Integer> getState(){
		int reward=getReward();
		ArrayList<Integer> output = new ArrayList<Integer>(Arrays.asList(this.playerSum, this.playerAce, Math.min(10, this.dealerCard),this.pair,reward));
		return output;
	}
	
	/**
	 * Checks whether the two cards are a pair or not.
	 * @param card1
	 * @param card2
	 * @return whether the cards are a pair or not
	 */ 
	private int checkPair(int card1, int card2){
		if (card1 == card2){
			return 1;
		}
		return 0;
	}
	 
	/**
	 * Gets a random card with values 1-13
	 * @return card
	 */ 
	public int getCard() {
		Random rand = new Random(); 
		int card = rand.nextInt(13) + 1;
		return card;
	}
	
	/**
	 * Method for splitting, such that you play two new hands with the given card and dealercard
	 * @return list of card your splitting and the dealer card
	 */ 
	private List<Integer> Split(){
		return new ArrayList<Integer>(Arrays.asList(this.playerCard,Math.min(10, this.dealerCard)));
	}
	
	/**
	 * Method for hitting for the player, such that the player get's an additional card
	 * Check if you it's still your turn(not chosen Stand/Double yet) or if the game is over
	 * If game's not over and your allowed to hit, the players gets an additional card
	 * If you bust and have an ace, the players sum decreases
	 */ 
	private void Hit(){
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
	
	/**
	 * Method for hitting for the dealer
	 */ 
	private void DealerHit(){
		this.PlayersTurn=false; 
		int card = getCard();
		this.dealerSum += getValue(card);
		if(this.dealerSum > 21 && this.dealerAce == 1){
			this.dealerSum -= 10;
			this.dealerAce = 0;
		}
	}
	
	/**
	 * Method for standing
	 * Makes sure it's the dealers turn to play
	 */ 
	private void Stand(){
		this.PlayersTurn = false;
	}
	
	/**
	 * Method for doubling down
	 * A double is always followed up by a hit and a stand, and doubles the reward
	 */ 
	private void Double(){
		this.Hit();
		this.Stand();
		this.doubleReward = 2;
	}
	
	/**
	 * Method for checking if the game is over
	 * Game is over if the player has busted or the dealer have a sum equal or greater than 17
	 * @return true if the game is over
	 */ 
	private boolean gameOver(){
		if((this.PlayersTurn == false && this.dealerSum >= 17)||this.playerSum > 21){
			return true;
		}
		return false;
	}
	
	/**
	 * Getting the reward of the game
	 * If the game is not over, the reward is 0.
	 * If the player won, the reward is 1 or 2 depending on if you doubled or not
	 * If the dealer won, the reward if -1 or -2 depending on if you doubled or not
	 * @return the re
	 */ 
	public int getReward(){
		if(gameOver()){
			return (this.playerSum < 22 ? (this.dealerSum < 22 ? (this.dealerSum >= this.playerSum ? -1*this.doubleReward : 1*this.doubleReward) : 1*this.doubleReward):-1*this.doubleReward);
		}
		return 0;
	}
	
	/**
	 * Converting the value of a card, since all cards above 10 are considered a 10, and the ace is either 1 or 11
	 * @param card
	 * @return value of the card
	 */ 
	public int getValue(int card){
		if (card==1){
			return 11;
		}
		return Math.min(card, 10);
	}
	
	/**
	 * Checks if a card is ace or not
	 * @param card
	 * @return if the card is ace or not
	 */ 
	public int checkAce(int card){
		if(card==1){
			return 1;
		}
		return 0;
	}
	
	/**
	 * Makes the actual move and returns a list of the state parameters
	 * @param action. 0=hit, 1=stand, 2=double, 3=split
	 * @return the state you get to by making the action
	 */ 
	public List<Integer> makeMove(int action){
		if(action == 0){
			this.Hit();
			return this.getState();
		}
		if(action == 1){
			this.Stand();
			while(this.dealerSum<17 && this.playerSum<=21){
				this.DealerHit();
			}
			return this.getState();
		}
		if(action == 2){ 
			this.Double();
			while(this.dealerSum<17 && this.playerSum<=21){
				this.DealerHit();
			}
			return this.getState();
		}
		else{
			return this.Split();
		}
	}
	
}
