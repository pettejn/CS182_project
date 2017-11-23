package blackjack;

public class State {
	
		private int sum;
		private int ace;
		private int dealerCard;

		public State(int sum, int ace, int dealerCard){
			this.sum = sum;
			this.ace = ace;
			this.dealerCard = dealerCard;
			
		}
		
		
		public int getSum(){
			return this.sum;
		}
		
		public int getAce(){
			return this.ace;
		}
		
		public int getDealer(){
			return this.dealerCard;
		}


		@Override
		public String toString() {
			return "State [sum=" + sum + ", ace=" + ace + ", dealerCard=" + dealerCard + "]";
		}
		
		
}
