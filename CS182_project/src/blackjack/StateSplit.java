package blackjack;

public class StateSplit {
	
		private int sum;
		private int ace;
		private int dealerCard;
		private int card1;
		private int card2;
		private int pair = 0;

		public StateSplit(int sum, int ace, int dealerCard, int card1, int card2){
			this.sum = sum;
			this.ace = ace;
			this.dealerCard = dealerCard;
			this.card1 = card1;
			this.card2 =card2;
			if(card1==card2){
				this.pair=1;
			}
			
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
		
		public int getCard1(){
			return this.card1;
		}
		
		public int isPair(){
			return this.pair;
		}
		
		public int getCard2(){
			return this.card2;
		}



		@Override
		public String toString() {
			return "State [sum=" + sum + ", ace=" + ace + ", dealerCard=" + dealerCard + "]";
		}
		
		
}

