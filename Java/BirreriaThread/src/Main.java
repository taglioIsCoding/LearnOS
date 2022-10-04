import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
		int max_piene = 30;
		int max_vuote = 30;
		
		int num_piene = 10;
		int num_vuote = 5;
		
		Birreria birreria = new Birreria(max_piene, max_vuote, num_piene, num_vuote);
		
		Random random = new Random(); 
		
		for(int i = 0; i < 3; i++){
			Fornitore fornitore = new Fornitore(birreria, i,random);
			fornitore.start();
		}
		
		for(int i = 0; i < 10; i++){
			Acquirente acquirente = new Acquirente(birreria, i,random);
			acquirente.start();
		}
		
	}

}
