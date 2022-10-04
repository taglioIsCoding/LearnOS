import java.util.Random;

public class Fornaio extends Thread{
	
	private int id;
	private Forno forno;
	private Random random;
	
	public Fornaio(int id, Forno forno, Random random) {
		this.id = id;
		this.forno = forno;
		this.random = random;
	}
	
	public void run() {
		try {
			Thread.sleep(random.nextInt(1000));
		for(int i = 0; i < 30; i++) {
			int T = random.nextInt(1000);
			boolean isSalato = random.nextBoolean();
			forno.infornaProdotto(this, isSalato, T);
			Thread.sleep(T);
			forno.estraiProdotto(this, isSalato);
			Thread.sleep(random.nextInt(1000));
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getId() {
		return id;
	}
	
	
}
