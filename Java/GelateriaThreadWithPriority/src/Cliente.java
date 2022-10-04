import java.util.Random;


public class Cliente extends Thread{

	private Gelateria gelateria;
	private int id;
	private Random random;
	private int gelataioCheLoServe;
	private boolean bimbo;
	
	public Cliente(Gelateria gelateria, int id, Random random) {
		this.gelateria = gelateria;
		this.id = id;
		this.random = random;
		
		if(id%3 == 0) {
			bimbo = true;
		}
	}
	
	public void run(){
		try {
			if (bimbo) {
				this.gelataioCheLoServe = gelateria.entraBimbo(this);
			}else {
				this.gelataioCheLoServe = gelateria.entraCliente(this);
			}
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
