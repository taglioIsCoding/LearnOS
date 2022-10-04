import java.util.Random;


public class Gelataio extends Thread{

	private Gelateria gelateria;
	private int id;
	private Random random;
	
	public Gelataio(Gelateria gelateria, int id, Random random) {
		this.gelateria = gelateria;
		this.id = id;
		this.random = random;
	}
	
	public void run(){
		
		while(true){
			try {
				gelateria.entraGelataio(this);
				Thread.sleep(random.nextInt(1000));
				gelateria.esceGelataio(this);
				Thread.sleep(random.nextInt(100));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public long getId(){
		return this.id;
	}
	
	
}
