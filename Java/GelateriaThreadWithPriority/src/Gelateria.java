import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Gelateria {
	
	private int capacitaNegozio; // Numero max di persone
	private int personeNelNegozio;
	private int numClientiInAttesaDiEntrare;//Sia clienti sia gelatai si sospendono se si reggiunge capacità max
	private int numGelataiInAttesaDiEntrare; 
	private int numBimbiInAttesaDiEntrare; 
	private int numGelataiInAttesaDiUscire;
	private int gelataiTotali;
	private int gelataiDisponibili; //I clienti non possono entrare se non ci sono gelatai disponibili
	private int gelataiOccupati;
	private boolean[] gelataioPuoUscire;
	
	private Lock lock;

	private Condition voglioEntrareCliente; 
	private Condition voglioEntrareBambino; 
	private Condition voglioEntrareGelataio; 
	
	private Condition gelataioVoglioUscire; 
	
	

	public Gelateria(int capacitaNegozio, int gelataiDisponibili) {
		this.capacitaNegozio = capacitaNegozio;
		this.gelataiTotali = gelataiDisponibili;
		this.numGelataiInAttesaDiEntrare = 0; 
		this.numBimbiInAttesaDiEntrare = 0; 
		this.gelataiDisponibili = 0;
		this.gelataiOccupati = 0;
		this.personeNelNegozio = 0;
		this.numGelataiInAttesaDiUscire = 0;
		
		lock = new ReentrantLock();
		this.voglioEntrareCliente = lock.newCondition();
		this.voglioEntrareBambino = lock.newCondition();
		this.voglioEntrareGelataio = lock.newCondition();
		this.gelataioVoglioUscire = lock.newCondition();
		
		this.gelataioPuoUscire = new boolean[gelataiTotali];

	}

	public int entraCliente(Cliente cliente) throws InterruptedException {
		lock.lock();
		try{
			while(capacitaNegozio < personeNelNegozio + 1 || gelataiDisponibili == gelataiOccupati || numGelataiInAttesaDiEntrare > 0 || numBimbiInAttesaDiEntrare > 0){
				numClientiInAttesaDiEntrare++;
				System.out.println("Sono Cliente "+ cliente.getId()+ " mi sono sospeso ");
				voglioEntrareCliente.await();
				numClientiInAttesaDiEntrare--;
			}
			
			personeNelNegozio++;
			gelataiOccupati++;
			
			int toreturn = -1;
			for(int i = 0; i < gelataiTotali; i++ ){
				if(gelataioPuoUscire[i] == true){
					 gelataioPuoUscire[i] = false;
					 toreturn = i;
					 break;
				}
			}
			
			System.out.println("Sono Cliente "+ cliente.getId()+ " sto prendendo il gelato dal gelataio "+ toreturn);
			return toreturn;
		
		}finally{lock.unlock();}
		
	}
	
	public int entraBimbo(Cliente bimbo) throws InterruptedException {
		lock.lock();
		try{
			while(capacitaNegozio < personeNelNegozio + 1 || gelataiDisponibili == gelataiOccupati || numGelataiInAttesaDiEntrare > 0){
				numBimbiInAttesaDiEntrare++;
				System.out.println("Sono bimbo "+ bimbo.getId()+ " mi sono sospeso ");
				voglioEntrareBambino.await();
				numBimbiInAttesaDiEntrare--;
			}
			
			personeNelNegozio++;
			gelataiOccupati++;
			
			int toreturn = -1;
			for(int i = 0; i < gelataiTotali; i++ ){
				if(gelataioPuoUscire[i] == true){
					 gelataioPuoUscire[i] = false;
					 toreturn = i;
					 break;
				}
			}
			System.out.println("Sono bimbo "+ bimbo.getId()+ " sto prendendo il gelato dal gelataio "+ toreturn);
			return toreturn;
		
		}finally{lock.unlock();}
		
	}

	public void esceCliente(Cliente c, int idGelataio) {
		lock.lock();
		try{
			personeNelNegozio--;
			gelataiOccupati--;
			gelataioPuoUscire[idGelataio] = true;
			

			System.out.println("Sono Cliente "+ c.getId()+ " ho finito con " + idGelataio);
			
			if(numGelataiInAttesaDiUscire > 0){
				//si potrebbe creare un array di condition e usare solo signal;
				gelataioVoglioUscire.signalAll();
			}
			if(numGelataiInAttesaDiEntrare > 0){
				voglioEntrareGelataio.signal();
			} else if(numBimbiInAttesaDiEntrare > 0){
				voglioEntrareBambino.signal();
			} else if (numClientiInAttesaDiEntrare > 0) {
				voglioEntrareCliente.signal();
			}
			
			
		}finally{lock.unlock();}
		
	}

	public void entraGelataio(Gelataio gelataio) throws InterruptedException {
		lock.lock();
		try{
			while(capacitaNegozio < personeNelNegozio + 1){
				numGelataiInAttesaDiEntrare++;
				System.out.println("		Sono Gelataio "+ gelataio.getId()+ " che voleva entrare mi sono sospeso");
				voglioEntrareGelataio.await();
				numGelataiInAttesaDiEntrare--;
			}
			
			personeNelNegozio++;
			gelataiDisponibili++;
			gelataioPuoUscire[(int) gelataio.getId()] = true;
			
			
			if(numBimbiInAttesaDiEntrare > 0 && personeNelNegozio + 1 < capacitaNegozio){
				voglioEntrareBambino.signal();
				System.out.println("		Sono Gelataio "+ gelataio.getId() + " segnalo bimbi");
			}else if(numClientiInAttesaDiEntrare > 0 && personeNelNegozio + 1 < capacitaNegozio) {
				voglioEntrareCliente.signal();
			}
			
			System.out.println("		Sono Gelataio "+ gelataio.getId() + " sono entrato");
			Thread.sleep(10);
		}finally{lock.unlock();}
	}

	public void esceGelataio(Gelataio gelataio) throws InterruptedException {
		lock.lock();
		try{
			while(gelataioPuoUscire[(int) gelataio.getId()] == false){
				System.out.println("		Sono Gelataio "+ gelataio.getId()+ " che voleva uscire mi sono sospeso");
				numGelataiInAttesaDiUscire++;
				gelataioVoglioUscire.await();
			}
			
			numGelataiInAttesaDiUscire--;
			personeNelNegozio--;
			gelataioPuoUscire[(int) gelataio.getId()] = false;
			gelataiDisponibili--;
			
			
			System.out.println("		Sono Gelataio "+ gelataio.getId() + " sono uscito");
		}finally{lock.unlock();}
	}

	
	
	
}
