package montagneRusse;

public class Visitatore extends Thread{
	
	private Stazione stazione;
	public int id;
	
	public Visitatore(Stazione stazione, int id) {
		this.stazione = stazione;
		this.id = id;
	}
	
	public void run() {
		stazione.accesso(this);
		stazione.uscita();
	}

}
