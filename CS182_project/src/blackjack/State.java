package blackjack;


/**
 * @author Ingrid E. Hermanrud, Petter J. Narvhus
 *
 */
public class State {
	
	private int playerSum;
	private int ace;
	private int dealerCard;
	private int isPair;

	/**
	 * Constructor to make a state
	 * @param sum
	 * @param ace
	 * @param dealerCard
	 * @param isPair
	 */ 
	public State(int playerSum, int ace, int dealerCard, int isPair){
		this.playerSum = playerSum;
		this.ace = ace;
		this.dealerCard = dealerCard;
		this.isPair = isPair;
	}
	
	public int getSum(){
		return this.playerSum;
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
	
	/**
	 * Overriding the automatically implemented hashCode-function in java
	 * Intention: easily look-up on Q-states as key in a hash-map
	 * Normally the hashCode is a memory location
	 * Looking up on the string of combination of parameters in the Q-state makes it easy to look up
	 * @return hashcode
	 */ 
	@Override
	public int hashCode(){
		String hashCodeString = String.valueOf(this.getSum())+String.valueOf(this.getAce())+String.valueOf(this.getDealer())+String.valueOf(this.isPair());
		int hashCode = Integer.parseInt(hashCodeString);
		return hashCode;
	}
	
	/**
	 * Overriding the automatically implemented equals-function in java
	 * Normally equals is true if the objects have the same memory location
	 * Saying that two objects are equal if they have the same hashcode
	 * @param obj
	 * @return hashcode
	 */ 
	@Override
	public boolean equals(Object obj){
		boolean flag = false;
		State state = (State)obj;
		int code1 = getCode(state);
		int code2 = getCode(this);
		if( code1 == code2)
			flag = true;
		return flag;
	}
	
	/**
	 * Help method which returns the hash-code
	 * Returns string of parameters as the unique hashcode
	 * @return hCode
	 */ 
	private int getCode(State state){
		String string = String.valueOf((state.getSum())+String.valueOf(state.getAce())+String.valueOf(state.getDealer())+String.valueOf(state.isPair()));
		int hCode = Integer.parseInt(string);
		return hCode;
	}

	@Override
	public String toString() {
		return "State [sum=" + playerSum + ", ace=" + ace + ", dealerCard=" + dealerCard + ", Pair=" + this.isPair() + "]";
	}
	
		
}