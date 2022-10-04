import java.util.Random;

public class Inserviente extends Thread{
	
	private Forno forno;
	private Random random;
	
	public Inserviente(Forno forno, Random random) {
		this.forno = forno;
		this.random = random;
	}
	
	public void run() {
		try {
			while(true) {
				Thread.sleep(random.nextInt(1000));
				if(random.nextInt(1000) % 25 == 0) {
					forno.iniziaPulisciForno();
					Thread.sleep(random.nextInt(1000));
					forno.finePulisciForno();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
