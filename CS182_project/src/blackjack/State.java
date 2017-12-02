package blackjack;

public class State {
	
		private int sum;
		private int ace;
		private int dealerCard;
		private int isPair;

		public State(int sum, int ace, int dealerCard, int isPair){
			this.sum = sum;
			this.ace = ace;
			this.dealerCard = dealerCard;
			this.isPair = isPair; 
			
			
		}
		
		
		public int getSum(){
			return this.sum;
		}
		
		public int getAce(){
			return this.ace;
		}
		
		public int getDealer(){
			return this.ace;
		}
		
		public int isPair(){
			return this.isPair;
		}


		@Override
		public String toString() {
			return "State [sum=" + sum + ", ace=" + ace + ", dealerCard=" + dealerCard + "]";
		}
		
		
}
