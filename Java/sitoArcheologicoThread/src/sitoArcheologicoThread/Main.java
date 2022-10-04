package sitoArcheologicoThread;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
		int maxPersoneInSala = 5;
		int maxPersoneCunicolo = 3;
		Random random = new Random();
		
		Stanza stanza = new Stanza(maxPersoneInSala, maxPersoneCunicolo);
		
		for(int i = 0; i < 3; i++) {
			Guida guida = new Guida(stanza, i, random);
			guida.start();
		}
		
		for(int i = 0; i < 3; i++) {
			Coppia coppia = new Coppia(stanza, i, random);
			coppia.start();
		}
		
		for(int i = 0; i < 7; i++) {
			Singolo singolo = new Singolo(stanza, i, random);
			singolo.start();
		}

	}

}
