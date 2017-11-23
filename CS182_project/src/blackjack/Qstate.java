package blackjack;

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

	@Override
	public String toString() {
		return "Qstate [state=" + state + ", action=" + action + "]";
	}
	
	
	

}
