import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lavanderia {
	
	private final static int ABBONATO = 0;
	private final static int NON_ABBONATO = 1;
	
	private int potenzaMassima;
	private int potenzaLavatrice;
	private int potenzaAsciugatrice;
	private int potenzaAttuale;
	
	private int numeroLavatrici;
	private int numeroAsciugatrici;
	private int lavatriciAttive;
	private int asciugatriciAttive;
	
	private int[] numAspettoLavatrice = new int[2];
	private int[] numAspettoAsciugatrice = new int[2];;
	
	private Lock lock;
	private Condition[] aspettoLavatrice;
	private Condition[] aspettoAsciugatrice;
	
	public Lavanderia(int potenzaMassima, int numeroLavatrici, int numeroAsciugatrici, int potenzaLavatrice ,int potenzaAsciugatrice) {
		this.potenzaMassima = potenzaMassima;
		this.numeroLavatrici = numeroLavatrici;
		this.numeroAsciugatrici = numeroAsciugatrici;
		this.potenzaLavatrice = potenzaLavatrice;
		this.potenzaAsciugatrice = potenzaAsciugatrice;
		
		this.potenzaAttuale = 0;
		this.lavatriciAttive = 0;
		this.asciugatriciAttive = 0;
		
		this.numAspettoLavatrice = new int[]{0,0};
		this.numAspettoAsciugatrice = new int[]{0,0};
		
		this.aspettoLavatrice = new Condition[2];
		this.aspettoAsciugatrice = new Condition[2];
		
		this.lock = new ReentrantLock();
		this.aspettoLavatrice[ABBONATO] = lock.newCondition();
		this.aspettoLavatrice[NON_ABBONATO] = lock.newCondition();
		this.aspettoAsciugatrice[ABBONATO] = lock.newCondition();
		this.aspettoAsciugatrice[NON_ABBONATO] = lock.newCondition();
		
	}
	
	//SEZIONE ABBONATI
	public void prendiLavatriceAbbonato(Cliente cliente) throws InterruptedException {
		lock.lock();
		try {
			
			while((numAspettoAsciugatrice[NON_ABBONATO]+numAspettoAsciugatrice[ABBONATO]) > 0 ||
					(this.potenzaAttuale + this.potenzaLavatrice) > this.potenzaMassima ||  
					(this.lavatriciAttive + 1) > this.numeroLavatrici) {
				System.out.println("Sono abbonato "+ cliente.getId() + " sto aspettando lavatrice");
				numAspettoLavatrice[ABBONATO]++;
				this.aspettoLavatrice[ABBONATO].await();
				numAspettoLavatrice[ABBONATO]--;
			}
			
			System.out.println("Sono abbonato "+ cliente.getId() + " ho preso lavatrice ");
			
			
			this.potenzaAttuale += this.potenzaLavatrice;
			this.lavatriciAttive++;
			
			this.stampa();
			
		}finally {
			lock.unlock();
		}
		
	}

	public void lasciaLavatrice(Cliente cliente) {
		lock.lock();
		try {
			
			System.out.println(cliente.getId() + " ho finito lavatrice");
			this.lavatriciAttive--;
			this.potenzaAttuale -= this.potenzaLavatrice;
			
			if(numAspettoAsciugatrice[ABBONATO] > 0) {
				aspettoAsciugatrice[ABBONATO].signal();
			} else if (numAspettoAsciugatrice[NON_ABBONATO] > 0) {
				aspettoAsciugatrice[NON_ABBONATO].signal();
			} 
			if(numAspettoAsciugatrice[NON_ABBONATO] == 0 && numAspettoAsciugatrice[ABBONATO] == 0) {
				if(numAspettoLavatrice[ABBONATO] > 0) {
					aspettoLavatrice[ABBONATO].signalAll();
				} 
				if(numAspettoLavatrice[NON_ABBONATO] > 0) {
					aspettoLavatrice[NON_ABBONATO].signalAll();
				}
			}
			this.stampa();
		}finally {
			lock.unlock();
		}
		
	}

	public void prendiAsciugatriceAbbonato(Cliente cliente) throws InterruptedException {
		lock.lock();
		try {
			
			while((this.potenzaAttuale + this.potenzaAsciugatrice) > this.potenzaMassima ||  
					(this.asciugatriciAttive + 1) > this.numeroAsciugatrici) {
				System.out.println("Sono abbonato "+ cliente.getId() + " sto aspettando asciugatrice");
				numAspettoAsciugatrice[ABBONATO]++;
				this.aspettoAsciugatrice[ABBONATO].await();
				numAspettoAsciugatrice[ABBONATO]--;
			}
			
			System.out.println("Sono abbonato "+ cliente.getId() + " ho preso asciugatrice");
			
			this.potenzaAttuale += this.potenzaAsciugatrice;
			this.asciugatriciAttive++;
			this.stampa();
			
		}finally {
			lock.unlock();
		}
		
	}

	public void lasciaAsciugatrice(Cliente cliente) {
		lock.lock();
		try {
			
			
			System.out.println(cliente.getId() + " ho finito asciugatrice TUTTO");
			this.asciugatriciAttive--;
			this.potenzaAttuale -= this.potenzaAsciugatrice;
			this.stampa();
			
			if(numAspettoAsciugatrice[ABBONATO] > 0) {
				aspettoAsciugatrice[ABBONATO].signal();
			} else if (numAspettoAsciugatrice[NON_ABBONATO] > 0) {
				aspettoAsciugatrice[NON_ABBONATO].signal();
			} 
			if(numAspettoAsciugatrice[NON_ABBONATO] == 0 && numAspettoAsciugatrice[ABBONATO] == 0) {
				if(numAspettoLavatrice[ABBONATO] > 0) {
					aspettoLavatrice[ABBONATO].signalAll();
				} 
				if(numAspettoLavatrice[NON_ABBONATO] > 0) {
					aspettoLavatrice[NON_ABBONATO].signalAll();
				}
			}
		}finally {
			lock.unlock();
		}
		
	}
	
	
	//SEZIONE NON ABBONATI
	public void prendiLavatriceNormale(Cliente cliente) throws InterruptedException {
		lock.lock();
		try {
			
			while((numAspettoAsciugatrice[NON_ABBONATO]+numAspettoAsciugatrice[ABBONATO]) > 0 ||
					numAspettoLavatrice[ABBONATO] > 0 ||
					(this.potenzaAttuale + this.potenzaLavatrice) > this.potenzaMassima ||  
					(this.lavatriciAttive + 1) > this.numeroLavatrici) {
				System.out.println("	Sono NON abbonato "+ cliente.getId() + " sto aspettando lavatrice");
				numAspettoLavatrice[NON_ABBONATO]++;
				this.aspettoLavatrice[NON_ABBONATO].await();
				numAspettoLavatrice[NON_ABBONATO]--;
			}
			
			System.out.println("	Sono NON abbonato "+ cliente.getId() + " ho preso la lavatrice");
			this.potenzaAttuale += this.potenzaLavatrice;
			this.lavatriciAttive++;
			
			this.stampa();
			
		}finally {
			lock.unlock();
		}
		
	}


	public void prendiAsciugatriceNormale(Cliente cliente) throws InterruptedException {
		lock.lock();
		try {
			
			while(numAspettoAsciugatrice[ABBONATO] > 0 ||
					(this.potenzaAttuale + this.potenzaAsciugatrice) > this.potenzaMassima ||  
					(this.asciugatriciAttive + 1) > this.numeroAsciugatrici) {
				System.out.println("	Sono NON abbonato "+ cliente.getId() + " sto aspettando asciugatrice");
				numAspettoAsciugatrice[NON_ABBONATO]++;
				this.aspettoAsciugatrice[NON_ABBONATO].await();
				numAspettoAsciugatrice[NON_ABBONATO]--;
			}
			
			System.out.println("	Sono NON abbonato "+ cliente.getId() + " ho preso l'asciugatrice");
			this.potenzaAttuale += this.potenzaAsciugatrice;
			this.asciugatriciAttive++;
			
			this.stampa();
		}finally {
			lock.unlock();
		}
	}
	
	private void stampa() {
		System.err.println("		Potenza [" + potenzaAttuale +"/" +potenzaMassima + "] "+ "Lavatrici [" + lavatriciAttive +"/" +numeroLavatrici + "] "+ "Asciugatrici [" + asciugatriciAttive +"/" +numeroAsciugatrici + "]");
		//System.err.println("		Abbonati coda Lav [" + numAspettoLavatrice[ABBONATO] +"] "+ "Abbonati coda ASci [" + numAspettoAsciugatrice[ABBONATO] +"] "+ " NO coda Lav [" + numAspettoLavatrice[NON_ABBONATO] +"] "+ "No coda ASci [" + numAspettoAsciugatrice[NON_ABBONATO] +"] ");
		
	}

}
