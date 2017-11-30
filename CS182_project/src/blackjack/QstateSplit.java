package blackjack;

public class QstateSplit {
	
	private StateSplit state;
	private int action;

	public QstateSplit(StateSplit state, int action){
		this.action = action;
		this.state = state;
	}
	
	public int getAction(){
		return this.action;
	}
	
	public StateSplit getState(){
		return this.state;
	}

	@Override
	public String toString() {
		return "Qstate [state=" + state + ", action=" + action + "]";
	}
	
	
	

}
