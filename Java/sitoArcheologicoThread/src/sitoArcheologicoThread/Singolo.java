package sitoArcheologicoThread;

import java.util.Random;

public class Singolo extends Thread{
	
	private Stanza sitoArcheologico;
	private int id;
	private Random random;
	
	public Singolo(Stanza sitoArcheologico, int id, Random random) {
		this.sitoArcheologico = sitoArcheologico;
		this.id = id;
		this.random = random;
	}
	
	public void run() {
		try {
			sitoArcheologico.singoloEntraCunicolo(this);
			Thread.sleep(random.nextInt(3*1000));
			sitoArcheologico.singoloEntraStanza(this);
			Thread.sleep(random.nextInt(10*1000));
			sitoArcheologico.singoloEntraCunicoloOut(this);
			Thread.sleep(random.nextInt(3*1000));
			sitoArcheologico.singoloEsceCunicoloOut(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long getId() {
		return this.id;
	}
	
	
	

}
