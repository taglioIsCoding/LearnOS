import java.util.Random;

public class Main {

	public static void main(String[] args) {
		int potenzaMassima = 100;
		int potenzaLavatrice = 20;
		int potenzaAsciugatrice = 30;
		
		int numLavatrici = 7;
		int numAsciugatrici = 4;
		
		Random random = new Random();
		
		Lavanderia lavanderia = new Lavanderia(potenzaMassima, numLavatrici, numAsciugatrici, potenzaLavatrice, potenzaAsciugatrice);
		
		for(int i = 0; i < 10; i++) {
			Cliente cliente = new Cliente(i, random, lavanderia);
			cliente.start();
		}

	}

}
