package blackjack;

public class State {
	
		private int playerSum = 0;
		private boolean playerAce = false;
		private int dealerCard = 0;
		private int dealerSum = 0;
		private boolean dealerAce = false;
		
		public State(int playerSum, boolean playerAce, int dealerCard,int dealerSum, boolean dealerAce){
			this.playerSum = playerSum;
			this.playerAce = playerAce;
			this.dealerCard = dealerCard;
			this.dealerSum = dealerSum;
			this.dealerAce = dealerAce;
		}
		
		
		public int getPlayerSum(){
			return this.playerSum;
		}
		
		public int getDealerSum(){
			return this.dealerSum;
		}
		
		public int getDealerCard(){
			return this.dealerCard;
		}
		
		public boolean getPlayerAce(){
			return this.playerAce;
		}
		
		public boolean getDealerAce(){
			return this.dealerAce;
		}
		
		public void setPlayerSum(int sum){
			this.playerSum = sum;
		}
		
		public void setDealerSum(int sum){
			this.dealerSum = sum;
		}
		
		public void setPlayerAce(boolean ace){
			this.playerAce = ace;
		}
		
		public void setDealerAce(boolean ace){
			this.dealerAce = ace;
		}
}
