import java.util.Random;

public class Cliente extends Thread {
	
	private int id;
	private boolean abbonato;
	private Random random;
	private Lavanderia lavanderia;
	
	public Cliente(int id, Random random, Lavanderia lavanderia) {
		this.id = id;
		this.random = random;
		this.lavanderia = lavanderia;
		this.abbonato = random.nextBoolean();
	}
	
	public void run() {
		if(abbonato) {
			runAbbonato();
		}else {
			runNonAbbonato();
		}
	}
	
	private void runAbbonato() {
		try {
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.prendiLavatriceAbbonato(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.lasciaLavatrice(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.prendiAsciugatriceAbbonato(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.lasciaAsciugatrice(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void runNonAbbonato() {
		try {
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.prendiLavatriceNormale(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.lasciaLavatrice(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.prendiAsciugatriceNormale(this);
			Thread.sleep(random.nextInt(3)*1000);
			lavanderia.lasciaAsciugatrice(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public long getId() {
		return this.id;
	}
	
	
	
}
