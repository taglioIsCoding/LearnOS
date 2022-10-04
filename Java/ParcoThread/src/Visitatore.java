import java.util.Random;

public class Visitatore extends Thread {
	
	private int id;
	//se true entra esce in giornata
	private boolean tipo;
	private Parco parco;
	private Random random;
	
	public Visitatore(int id, boolean tipo, Parco parco, Random random) {
		this.id = id;
		this.tipo = tipo;
		this.parco = parco;
		this.random = random;
	}
	
	public long getId() {
		return this.id;
	}
	
	public  void run() {
		try {
			if(random.nextInt() % 2 == 0 ) {
				if(tipo) {
						parco.entraNordNormale(this);
					Thread.sleep(random.nextInt(100));
					parco.esci(this);
				}else {
					parco.entraNordPriorita(this);
				}
				return;
			}else {
				if(tipo) {
					parco.entraSudNormale(this);
					Thread.sleep(random.nextInt(100));
					parco.esci(this);
				}else {
					parco.entraSudPriorita(this);
				}
				return;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
