package montagneRusse;

import java.util.Random;

public class Trenino extends Thread{
	
	private Stazione stazione;
	private Random random;
	
	public Trenino(Stazione stazione, Random random) {
		this.stazione = stazione;
		this.random = random;
	}
	
	public void run() {
		int durataGiro = random.nextInt(1000);
		System.out.println("Il giro sul trenino durera " +durataGiro+" millisecondi");
		while(true) {
			//System.out.println("Sono il treno e vorrei iniziare il giro");
			stazione.inizio_giro();
			try {
				Thread.sleep(durataGiro);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println("Sono il treno e vorrei finire il giro");
			stazione.fine_giro();
		}
		
		
	}
	
}
