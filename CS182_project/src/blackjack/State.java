package blackjack;

public class State {
	
		private int sum;
		private boolean ace;
		private int dealerCard;

		public State(int sum, boolean ace, int dealerCard){
			this.sum = sum;
			this.ace = ace;
			this.dealerCard = dealerCard;
			
		}
		
		
		public int getSum(){
			return this.sum;
		}
		
		public boolean getAce(){
			return this.ace;
		}
}
