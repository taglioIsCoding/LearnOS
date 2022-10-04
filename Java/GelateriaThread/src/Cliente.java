import java.util.Random;


public class Cliente extends Thread{

	private Gelateria gelateria;
	private int id;
	private Random random;
	private int gelataioCheLoServe;
	
	public Cliente(Gelateria gelateria, int id, Random random) {
		this.gelateria = gelateria;
		this.id = id;
		this.random = random;
	}
	
	public void run(){
		try {
			this.gelataioCheLoServe = gelateria.entraCliente(this);
			Thread.sleep(random.nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gelateria.esceCliente(this, this.gelataioCheLoServe);
		return;
	}

	public long getId() {
		return id;
	}
	

	
}
