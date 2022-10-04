package montagneRusse;

import java.util.Iterator;
import java.util.Random;

public class GiroSulleMontagneRusse {
	
	/**
	 * 
	 * README 
	 * 
	 * Putroppo ho avuto problemi nel gestire l'entrata e l'uscita dopo il primo giro
	 * 
	 */

	public static void main(String[] args) {
		int Tmax = 4;
		Random random = new Random();
		Stazione stazione = new Stazione(Tmax);
		Trenino trenino = new Trenino(stazione, random);
		trenino.start();
		
		for (int i = 0; i < 11; i++) {
			Visitatore visitatore = new Visitatore(stazione, i);
			visitatore.start();
		}
		

	}

}
