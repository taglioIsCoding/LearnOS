import java.util.Random;

public class Fornitore extends Thread{

	private Birreria birreria;
	private int id;
	private Random random;
	
	
	
	public Fornitore(Birreria birreria, int id, Random random) {
		this.birreria = birreria;
		this.id = id;
		this.random = random;
	}
	
	public void run(){
		
		while(true){
			try {
				Thread.sleep(random.nextInt(100));
				birreria.consegna(this, random.nextInt(20));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}



	public long getId() {
		return id;
	}
	
	
}
