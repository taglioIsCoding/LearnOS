import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Birreria {
	
	private int max_piene;
	private int max_vuote;
	
	private int num_piene;
	private int num_vuote;
	
	private int num_clientiInAttesa;
	private int num_fornitoriInAttesa;
	
	private Lock lock;
	private Condition fornitoriInAttesa;
	private Condition clientiInAttesa;
	
	
	public Birreria(int max_piene, int max_vuote, int num_piene, int num_vuote) {
		this.max_piene = max_piene;
		this.max_vuote = max_vuote;
		this.num_piene = num_piene;
		this.num_vuote = num_vuote;
		
		num_clientiInAttesa = 0;
		num_fornitoriInAttesa = 0;
		
		lock = new ReentrantLock();
		fornitoriInAttesa = lock.newCondition();
		clientiInAttesa = lock.newCondition();
		
		
	}

	public void acquisto(Acquirente acquirenti, int numeroBottiglie) throws InterruptedException {
		lock.lock();
		try {
			while(num_piene - numeroBottiglie < 0 || num_vuote + numeroBottiglie > max_vuote ||
					(num_vuote > num_piene && num_fornitoriInAttesa > 0)) {
				num_clientiInAttesa++;
				if (num_vuote > num_piene && num_fornitoriInAttesa > 0) {
					fornitoriInAttesa.signalAll();
				}
				System.out.println("Sono Cliente "+ acquirenti.getId()+ " mi sono sospeso volevo " + numeroBottiglie);
				clientiInAttesa.await();
				num_clientiInAttesa--;
			}
			
			System.out.println("Sono Cliente "+ acquirenti.getId()+ " ho fatto l'acquisto di " + numeroBottiglie);
			this.num_piene -= numeroBottiglie;
			this.num_vuote += numeroBottiglie;
			System.err.println("Birreria Piene:[" + num_piene +"/" +max_piene + "] Vuote:[" + num_vuote +"/"+ max_vuote +"] Clienti in attesa " + num_clientiInAttesa + "] "+" Fornitori in attesa [" + num_fornitoriInAttesa + "]");
			
			if (num_vuote > num_piene && num_fornitoriInAttesa > 0) {
				fornitoriInAttesa.signalAll();
			}
			
		} finally {
			lock.unlock();
		}
		
	}

	public void consegna(Fornitore fornitore, int numeroBottiglie) throws InterruptedException {
		lock.lock();
		try {
			while(num_piene + numeroBottiglie > max_piene || num_vuote - numeroBottiglie < 0 ||
					(num_vuote < num_piene && num_clientiInAttesa > 0)) {
				num_fornitoriInAttesa++;
				if (num_vuote < num_piene && num_clientiInAttesa > 0) {
					clientiInAttesa.signalAll();
				}
				System.out.println("Sono Fornitore "+ fornitore.getId()+ " mi sono sospeso volevo " + numeroBottiglie);
				fornitoriInAttesa.await();
				num_fornitoriInAttesa--;
			}
			
			System.out.println("Sono Fornitore "+ fornitore.getId()+ " ho fatto consegnato " + numeroBottiglie);
			this.num_piene += numeroBottiglie;
			this.num_vuote -= numeroBottiglie;
			System.err.println("Birreria Piene:[" + num_piene +"/" +max_piene + "] Vuote:[" + num_vuote +"/"+ max_vuote +"] Clienti in attesa " + num_clientiInAttesa + "] "+" Fornitori in attesa [" + num_fornitoriInAttesa + "]");
			
			
			if (num_vuote < num_piene && num_clientiInAttesa > 0) {
				clientiInAttesa.signalAll();
			}
			
			
		} finally {
			lock.unlock();
		}
		
	}
	
	private void stampa() {
		System.err.println("Birreria Piene:[" + num_piene +"/" +max_piene + "] Vuote:[" + num_vuote +"/"+ max_vuote +"] Clienti in attesa " + num_clientiInAttesa + "] "+" Fornitori in attesa [" + num_fornitoriInAttesa + "]");
	}

}
