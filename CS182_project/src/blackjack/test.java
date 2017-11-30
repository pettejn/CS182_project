package blackjack;

public class test {
	public static void main(String[] args) {
		String hei = "";
		for (int j=1; j<5; j++){
			for(int i=1;i<3;i++){
				if(i==2 && j<4){
					break;
				}
				System.out.println("j"+j);
				System.out.println("i"+i);
			}
		}
	}
}
