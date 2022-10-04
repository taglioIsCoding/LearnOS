import java.util.Random;

public class Acquirente extends Thread{
	
	private Birreria birreria;
	private int id;
	private Random random;
	
	public Acquirente(Birreria birreria, int id, Random random) {
		this.birreria = birreria;
		this.id = id;
		this.random = random;
	}
	
	public void run(){
		try {
			Thread.sleep(random.nextInt(1000));
			this.birreria.acquisto(this, random.nextInt(10));
			Thread.sleep(random.nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
	}

	public long getId() {
		return id;
	}
	

}
