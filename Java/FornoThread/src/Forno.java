import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Forno {
	
	private static final int DOLCE_CORTO = 0;
	private static final int DOLCE_LUNGO = 1;
	private static final int SALATO_CORTO = 2;
	private static final int SALATO_LUNGO = 3;
	
	private static final int DOLCE = 0;
	private static final int SALATO = 1;
	
	private int capacitaMax;
	private int X;
	private int statoForno; // -1 non assegnato, 0 dolce, 1 salato
	private int numProdottiNelForno;
	private int numProdottiInAttesaDelForno[];
	private boolean inservienteVuolePulire;
	
	private Lock lock;
	private Condition aspettoIlFornoPerCuocere[] = new Condition[4];
	private Condition aspettoIlFornoPerPulire;

	
	public Forno(int capacitaMax, int x) {
		this.capacitaMax = capacitaMax;
		this.X = x;
		this.statoForno = -1;
		this.numProdottiNelForno = 0;
		
		this.numProdottiInAttesaDelForno = new int[] {0,0,0,0};
		this.inservienteVuolePulire = false;
		
		this.lock = new ReentrantLock();
		this.aspettoIlFornoPerCuocere[DOLCE_CORTO] = lock.newCondition();
		this.aspettoIlFornoPerCuocere[DOLCE_LUNGO] = lock.newCondition();
		this.aspettoIlFornoPerCuocere[SALATO_CORTO] = lock.newCondition();
		this.aspettoIlFornoPerCuocere[SALATO_LUNGO] = lock.newCondition();
		this.aspettoIlFornoPerPulire = lock.newCondition();
		
	}

	public void infornaProdotto(Fornaio fornaio, boolean isSalato, int tempoCottura) throws InterruptedException {
		lock.lock();
		try {
			if(isSalato) {
				//PRODOTTI SALATI
				if(tempoCottura >= X) {
					//PRODOTTI SALATI LUNGHI
					while(inservienteVuolePulire || numProdottiNelForno + 1 > capacitaMax ||
							this.statoForno == DOLCE) {
						System.out.println("Sono " + fornaio.getId() + " volevo infornare "+ (isSalato ? "Salato ":"Dolce ") + " Lungo");
						this.numProdottiInAttesaDelForno[SALATO_LUNGO]++;
						this.aspettoIlFornoPerCuocere[SALATO_LUNGO].await();
						this.numProdottiInAttesaDelForno[SALATO_LUNGO]--;
					}
					
					System.out.println("Sono " + fornaio.getId() + " ho infornato "+ (isSalato ? "Salato ":"Dolce ") + " Lungo");
					this.statoForno = SALATO;
					this.numProdottiNelForno++;
					this.stampa();
					
				} else {
					//PRODOTTI SALATI CORTI
					while(inservienteVuolePulire || numProdottiNelForno + 1 > capacitaMax ||
							this.statoForno == DOLCE || numProdottiInAttesaDelForno[SALATO_LUNGO] > 0) {
						System.out.println("Sono " + fornaio.getId() + " volevo infornare "+ (isSalato ? "Salato ":"Dolce ") + " Corto");
						this.numProdottiInAttesaDelForno[SALATO_CORTO]++;
						this.aspettoIlFornoPerCuocere[SALATO_CORTO].await();
						this.numProdottiInAttesaDelForno[SALATO_CORTO]--;
					}
					
					System.out.println("Sono " + fornaio.getId() + " ho infornato "+ (isSalato ? "Salato ":"Dolce ") + " Corto");
					this.statoForno = SALATO;
					this.numProdottiNelForno++;
					this.stampa();
				}
				
			} else {
				//PRODOTTI DOLCI
				
				if(tempoCottura >= X) {
					//PRODOTTI DOLCI LUNGHI
					while(inservienteVuolePulire || numProdottiNelForno + 1 > capacitaMax ||
							this.statoForno == SALATO||
							numProdottiInAttesaDelForno[SALATO_LUNGO] + numProdottiInAttesaDelForno[SALATO_CORTO] > 0) {
						System.out.println("Sono " + fornaio.getId() + " volevo infornare "+ (isSalato ? "Salato ":"Dolce ") + " Lungo");
						this.numProdottiInAttesaDelForno[DOLCE_LUNGO]++;
						this.aspettoIlFornoPerCuocere[DOLCE_LUNGO].await();
						this.numProdottiInAttesaDelForno[DOLCE_LUNGO]--;
					}
					
					System.out.println("Sono " + fornaio.getId() + " ho infornato "+ (isSalato ? "Salato ":"Dolce ") + " Lungo");
					this.statoForno = DOLCE;
					this.numProdottiNelForno++;
					this.stampa();
					
					
				} else {
					//PRODOTTI DOLCI CORTI
					while(inservienteVuolePulire || numProdottiNelForno + 1 > capacitaMax ||
							this.statoForno == SALATO || 
							numProdottiInAttesaDelForno[SALATO_LUNGO] + numProdottiInAttesaDelForno[SALATO_CORTO] + numProdottiInAttesaDelForno[DOLCE_LUNGO] > 0 ) {
						
						System.out.println("Sono " + fornaio.getId() + " volevo infornare "+ (isSalato ? "Salato ":"Dolce ") + " Corto");
						this.numProdottiInAttesaDelForno[DOLCE_CORTO]++;
						this.aspettoIlFornoPerCuocere[DOLCE_CORTO].await();
						this.numProdottiInAttesaDelForno[DOLCE_CORTO]--;
					}
					
					System.out.println("Sono " + fornaio.getId() + " ho infornato "+ (isSalato ? "Salato ":"Dolce ") + " Corto");
					
					this.statoForno = DOLCE;
					this.numProdottiNelForno++;
					this.stampa();
					
				}
				
			}
			
		} finally {
			lock.unlock();
		}	
	}
	
	public void estraiProdotto(Fornaio fornaio, boolean isSalato) {
		lock.lock();
		try {
			this.numProdottiNelForno--;
			System.out.println("Sono " + fornaio.getId() + " ho tirato fuori "+ (isSalato ? "Salato ":"Dolce "));
			this.stampa();
			if(numProdottiNelForno == 0) {
				//FORNO VUOTO
				this.statoForno = -1;
				if(inservienteVuolePulire) {
					this.aspettoIlFornoPerPulire.signal();
					
				} else if(isSalato) {
					//NON devo pulire è ho appena finito di cuocere tutti i salati
					if(this.numProdottiInAttesaDelForno[DOLCE_LUNGO] > 0) {
						this.aspettoIlFornoPerCuocere[DOLCE_LUNGO].signalAll();
					}
					
					if(this.numProdottiInAttesaDelForno[DOLCE_CORTO] > 0) {
						this.aspettoIlFornoPerCuocere[DOLCE_CORTO].signalAll();
					}
				} else {
					//NON devo pulire è ho appena finito di cuocere tutti i dolci
					if(this.numProdottiInAttesaDelForno[SALATO_LUNGO] > 0) {
						this.aspettoIlFornoPerCuocere[SALATO_LUNGO].signalAll();
					}
					
					if(this.numProdottiInAttesaDelForno[SALATO_CORTO] > 0) {
						this.aspettoIlFornoPerCuocere[SALATO_CORTO].signalAll();
					}
					
				}
			} else {
				//FORNO ANCORA PIENO
				if(isSalato) {
					//sto cuocendo salati
					if(this.numProdottiInAttesaDelForno[SALATO_LUNGO] > 0) {
						this.aspettoIlFornoPerCuocere[SALATO_LUNGO].signalAll();
					} else  {
						this.aspettoIlFornoPerCuocere[SALATO_CORTO].signalAll();
					}
				} else {
					//sto cuocendo dolci
					if(this.numProdottiInAttesaDelForno[DOLCE_LUNGO] > 0) {
						this.aspettoIlFornoPerCuocere[DOLCE_LUNGO].signalAll();
					} else {
						this.aspettoIlFornoPerCuocere[DOLCE_CORTO].signalAll();
					}
				}
			}
			
		}finally {
			lock.unlock();
		}
	}

	public void iniziaPulisciForno() throws InterruptedException {
		lock.lock();
		try {
			
			while(this.numProdottiNelForno > 0) {
				this.inservienteVuolePulire = true;
				System.out.println("			Sono inservinete volevo pulire"); 
				this.aspettoIlFornoPerPulire.await();
			}
			
			System.out.println("			Sono inservinete inizio a pulire");
			this.stampa();
		}finally {
			lock.unlock();
		}
	}

	public void finePulisciForno() {
		lock.lock();
		try {
			System.out.println("			Sono inservinete finisco di pulire");
			this.inservienteVuolePulire = false;
			this.stampa();

			if(this.numProdottiInAttesaDelForno[SALATO_LUNGO] > 0) {
				this.aspettoIlFornoPerCuocere[SALATO_LUNGO].signalAll();
			} else if (this.numProdottiInAttesaDelForno[SALATO_CORTO] > 0) {
				this.aspettoIlFornoPerCuocere[SALATO_CORTO].signalAll();
			}else if (this.numProdottiInAttesaDelForno[DOLCE_LUNGO] > 0) {
				this.aspettoIlFornoPerCuocere[DOLCE_LUNGO].signalAll();
			} else {
				this.aspettoIlFornoPerCuocere[DOLCE_CORTO].signalAll();
			}
			
			
		}finally {
			lock.unlock();
		}
	}
	
	private void stampa() {
		System.err.println("Nel forno [" + numProdottiNelForno +"/" +capacitaMax + "] Modalita ["+ this.statoForno+"] Inserviente presente["+ this.inservienteVuolePulire+"] SalatiL [" + numProdottiInAttesaDelForno[SALATO_LUNGO] + "] "+"SalatiC [" + numProdottiInAttesaDelForno[SALATO_CORTO] + "]" + "DolciL [" + numProdottiInAttesaDelForno[DOLCE_LUNGO] + "] "+"DolciC [" + numProdottiInAttesaDelForno[DOLCE_CORTO] + "]");
		//System.err.println("		Abbonati coda Lav [" + numAspettoLavatrice[ABBONATO] +"] "+ "Abbonati coda ASci [" + numAspettoAsciugatrice[ABBONATO] +"] "+ " NO coda Lav [" + numAspettoLavatrice[NON_ABBONATO] +"] "+ "No coda ASci [" + numAspettoAsciugatrice[NON_ABBONATO] +"] ");
		
	}

}
