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
			return this.dealerCard;
		}
		
		public int isPair(){
			return this.isPair;
		}
		
		public void setPair(){
			this.isPair=0;
		}
		
		public String intToString(int num){
			String value = String.valueOf(num);
			return value;
		}
		
		public int hashCode(){
			String streng = intToString(this.getSum())+intToString(this.getAce())+intToString(this.getDealer())+intToString(this.isPair());
			int foo = Integer.parseInt(streng);
			return foo;
		}
		
		public int getCode(State state){
			String streng = intToString(state.getSum())+intToString(state.getAce())+intToString(state.getDealer())+intToString(state.isPair());
			int foo = Integer.parseInt(streng);
			return foo;
		}
		
		public boolean equals( Object obj){
			boolean flag = false;
			State state = (State)obj;
			int code1 = getCode(state);
			int code2 = getCode(this);
			if( code1 == code2)
				flag = true;
			return flag;
		}


		@Override
		public String toString() {
			return "State [sum=" + sum + ", ace=" + ace + ", dealerCard=" + dealerCard + ", Pair=" + this.isPair() + "]";
		}
		
		
}