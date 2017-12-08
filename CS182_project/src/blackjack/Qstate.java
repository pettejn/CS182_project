package blackjack;


/**
 * @author Ingrid E. Hermanrud, Petter J. Narvhus
 *
 */
public class Qstate {
	
	private State state;
	private int action;

	public Qstate(State state, int action){
		this.action = action;
		this.state = state;
	}
	
	public int getAction(){
		return this.action;
	}

	public State getState(){
		return this.state;
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
		String hashCodeString = String.valueOf(this.state.getSum())+String.valueOf(this.state.getAce())+String.valueOf(this.state.getDealer())+String.valueOf(this.state.isPair())+String.valueOf(this.action);
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
	public boolean equals( Object obj){
		boolean flag = false;
		Qstate state = (Qstate)obj;
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
	private int getCode(Qstate state){
		String string = String.valueOf(state.getState().getSum())+String.valueOf(state.getState().getAce())+String.valueOf(state.getState().getDealer())+String.valueOf(state.getState().isPair())+String.valueOf(state.getAction());
		int hCode = Integer.parseInt(string);
		return hCode;
	}
	
	@Override
	public String toString() {
		return "Qstate [state=" + state + ", action=" + action + "]";
	}


}
