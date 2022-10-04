package sitoArcheologicoThread;

import java.util.Random;

public class Guida extends Thread{
	
	private Stanza sitoArcheologico;
	private int id;
	private Random random;
	
	
	public Guida(Stanza sitoArcheologico, int id, Random random) {
		this.sitoArcheologico = sitoArcheologico;
		this.id = id;
		this.random = random;
	}
	
	public void run() {
		try {
			//La guida entra ed esce piu volte
			for(int i = 0; i < 30; i++) {
				sitoArcheologico.guidaEntraCunicolo(this);
				Thread.sleep(random.nextInt(3*1000));
				sitoArcheologico.guidaEntraStanza(this);
				Thread.sleep(random.nextInt(10*1000));
				sitoArcheologico.guidaEntraCunicoloOut(this);
				Thread.sleep(random.nextInt(3*1000));
				sitoArcheologico.guidaEsceCunicoloOut(this);
				Thread.sleep(random.nextInt(4*1000));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long getId() {
		return this.id;
	}


}
