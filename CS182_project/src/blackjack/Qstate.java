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
	
	public String intToString(int num){
		String value = String.valueOf(num);
		return value;
	}
	
	public int hashCode(){
		String streng = intToString(this.state.getSum())+intToString(this.state.getAce())+intToString(this.state.getDealer())+intToString(this.state.isPair())+intToString(this.action);
		int foo = Integer.parseInt(streng);
		return foo;
	}
	
	public int getCode(Qstate state){
		String streng = intToString(state.getState().getSum())+intToString(state.getState().getAce())+intToString(state.getState().getDealer())+intToString(state.getState().isPair())+intToString(state.getAction());
		int foo = Integer.parseInt(streng);
		return foo;
	}
	
	public boolean equals( Object obj){
		boolean flag = false;
		Qstate state = (Qstate)obj;
		int code1 = getCode(state);
		int code2 = getCode(this);
		if( code1 == code2)
			flag = true;
		return flag;
	}

	@Override
	public String toString() {
		return "Qstate [state=" + state + ", action=" + action + "]";
	}
	
//	public static void main(String[] args) {
//		Qstate state = new Qstate(new State(7,2,5,2),1);
//		System.out.println(state.intToString(51));
//	}
//	
//	
	
	

}
