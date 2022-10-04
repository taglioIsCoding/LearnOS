import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int capacitaForno = 10;
		int x = 500;
		Random random = new Random();
		
		Forno forno = new Forno(capacitaForno, x);
		
		for(int i = 0; i < 3; i++) {
			Fornaio fornaio = new Fornaio(i, forno, random);
			fornaio.start();
		}
		
		Inserviente inserviente = new Inserviente(forno, random);
		inserviente.start();

	}

}
