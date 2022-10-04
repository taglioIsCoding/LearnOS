import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int maxVisitatori = 100;
		int visitatoriStagione=100;
		int entratiNord = 75;
		int entratiSud = 25;
		
		Parco parcoDivertimenti = new Parco(maxVisitatori, visitatoriStagione, entratiNord, entratiSud);
		Random random = new Random();
		
		for(int i = 0; i < 25; i++ ) {
			Visitatore visitatore = new Visitatore(i, random.nextBoolean(), parcoDivertimenti, random);
			visitatore.start();
		}
		
	}

}
