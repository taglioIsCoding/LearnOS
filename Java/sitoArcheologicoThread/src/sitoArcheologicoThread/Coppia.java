package sitoArcheologicoThread;

import java.util.Random;

public class Coppia extends Thread{
	private Stanza sitoArcheologico;
	private int id;
	private Random random;
	
	
	public Coppia(Stanza sitoArcheologico, int id, Random random) {
		this.sitoArcheologico = sitoArcheologico;
		this.id = id;
		this.random = random;
	}
	
	public void run() {
		try {
			sitoArcheologico.coppiaEntraCunicolo(this);
			Thread.sleep(random.nextInt(3*1000));
			sitoArcheologico.coppiaEntraStanza(this);
			Thread.sleep(random.nextInt(10*1000));
			sitoArcheologico.coppiaEntraCunicoloOut(this);
			Thread.sleep(random.nextInt(3*1000));
			sitoArcheologico.coppiaEsceCunicoloOut(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long getId() {
		return this.id;
	}
	
	
	
}
