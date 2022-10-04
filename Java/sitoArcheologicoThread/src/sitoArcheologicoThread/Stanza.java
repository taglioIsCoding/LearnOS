package sitoArcheologicoThread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Stanza {
	
	private int maxPersoneInSala;
	private int maxPersoneCunicolo;
	// true passeggini presenti, false altrimenti. 0 direzione in, 1 direzione out
	private boolean ciSonoPassegginiNelCunicolo[];
	private int numeroGuideInSala;
	private int personeInSala;
	private int personeNelCunicolo;
	
	private int guideInAttesaDiEntrare;
	private int singoliInAttesaDiEntrare;
	private int coppieInAttesaDiEntrare;
	private int coppieInAttesaDiUscire;
	private int singoliInAttesaDiUscire;
	private int guideInAttesaDiUscire;
	
	private Lock lock;
	
	private Condition voglioEntrareSingolo;
	private Condition voglioEntrareCoppia;
	private Condition voglioEntrareGuida;
	
	private Condition voglioUscireSingolo;
	private Condition voglioUscireCoppia;
	private Condition voglioUscireGuida;
	
	

	public Stanza(int maxPersoneInSala, int maxPersoneCunicolo) {
		super();
		this.maxPersoneInSala = maxPersoneInSala;
		this.maxPersoneCunicolo = maxPersoneCunicolo;
		
		this.ciSonoPassegginiNelCunicolo = new boolean [] {false,false};
		this.numeroGuideInSala = 0;
		this.personeInSala = 0;
		this.personeNelCunicolo = 0;
		
		this.guideInAttesaDiEntrare = 0;
		this.singoliInAttesaDiEntrare = 0;
		this.coppieInAttesaDiEntrare = 0;
		this.coppieInAttesaDiUscire = 0;
		this.singoliInAttesaDiUscire = 0;
		this.guideInAttesaDiUscire = 0;
		
		this.lock = new ReentrantLock();
		this.voglioEntrareSingolo = lock.newCondition();
		this.voglioEntrareCoppia = lock.newCondition();
		this.voglioEntrareGuida = lock.newCondition();
		
		this.voglioUscireSingolo = lock.newCondition();
		this.voglioUscireCoppia = lock.newCondition();
		this.voglioUscireGuida = lock.newCondition();
		
	}

	//SEZIONE SINGOLO
	public void singoloEntraCunicolo(Singolo singolo) throws InterruptedException {
		lock.lock();
		try{
			while (personeInSala + 1 > maxPersoneInSala || personeNelCunicolo + 1 > maxPersoneCunicolo || guideInAttesaDiEntrare > 0 || singoliInAttesaDiUscire+coppieInAttesaDiUscire+guideInAttesaDiUscire > 0) {
				singoliInAttesaDiEntrare++;
				System.out.println("Sono Singolo "+ singolo.getId()+ " volevo entrare ");
				stampa();
				voglioEntrareSingolo.await();
				singoliInAttesaDiEntrare--;
			}
		
			personeNelCunicolo++;
			System.out.println("Sono Singolo "+ singolo.getId()+ " sono nel cunicolo IN");
			stampa();
		}finally{lock.unlock();}
	}
	
	public void singoloEntraStanza(Singolo singolo) {
		lock.lock();
		try{
			
			personeNelCunicolo--;
			personeInSala++;
			System.out.println("Sono Singolo "+ singolo.getId()+ " sono nella stanza");
			stampa();
			
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
			
		
		}finally{lock.unlock();}
	}
	
	public void singoloEntraCunicoloOut(Singolo singolo) throws InterruptedException {
		lock.lock();
		try{
			while (personeNelCunicolo + 1 > maxPersoneCunicolo || coppieInAttesaDiUscire > 0) {
				singoliInAttesaDiUscire++;
				System.out.println("Sono Singolo "+ singolo.getId()+ " volevo uscire");
				stampa();
				voglioUscireSingolo.await();
				singoliInAttesaDiUscire--;
			}
			
			personeInSala--;
			personeNelCunicolo++;
			System.out.println("Sono Singolo "+ singolo.getId()+ " sono nel cunicolo OUT");
			stampa();
		}finally{lock.unlock();}
	}

	public void singoloEsceCunicoloOut(Singolo singolo) {
		lock.lock();
		try{
			personeNelCunicolo--;
			System.out.println("Sono Singolo "+ singolo.getId()+ " sono uscito");
			stampa();
			
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
			
		}finally{lock.unlock();}
		
	}
	
	//SEZIONE COPPIA
	public void coppiaEntraCunicolo(Coppia coppia) throws InterruptedException {
		lock.lock();
		try{
			while (personeNelCunicolo + 2 > maxPersoneCunicolo || personeInSala + 2 > maxPersoneInSala 
					|| ciSonoPassegginiNelCunicolo[1] || guideInAttesaDiEntrare > 0 
					|| singoliInAttesaDiEntrare > 0 || singoliInAttesaDiUscire+coppieInAttesaDiUscire+guideInAttesaDiUscire > 0) {
				coppieInAttesaDiEntrare++;
				System.out.println("	Sono Coppia "+ coppia.getId()+ " volevo entrare");
				stampa();
				voglioEntrareCoppia.await();
				coppieInAttesaDiEntrare--;
			}
			
			ciSonoPassegginiNelCunicolo[0] = true;
			personeNelCunicolo = personeNelCunicolo + 2;
			System.out.println("	Sono Coppia "+ coppia.getId()+ " sono nel cunicolo IN");
			stampa();
		}finally{lock.unlock();}
		
	}

	public void coppiaEntraStanza(Coppia coppia) {
		lock.lock();
		try{
			
			personeNelCunicolo = personeNelCunicolo - 2;
			personeInSala = personeInSala + 2;
			ciSonoPassegginiNelCunicolo[0] = false;
			System.out.println("	Sono Coppia "+ coppia.getId()+ " sono nella stanza");
			stampa();
			
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
			
		
		}finally{lock.unlock();}
		
	}

	public void coppiaEntraCunicoloOut(Coppia coppia) throws InterruptedException {
		lock.lock();
		try{
			while (personeNelCunicolo + 2 > maxPersoneCunicolo || ciSonoPassegginiNelCunicolo[0]) {
				coppieInAttesaDiUscire++;
				System.out.println("	Sono Coppia "+ coppia.getId()+ " volevo uscire");
				stampa();
				voglioUscireCoppia.await();
				coppieInAttesaDiUscire--;
			}
			
			ciSonoPassegginiNelCunicolo[1] = true;
			personeInSala = personeInSala - 2;
			personeNelCunicolo = personeNelCunicolo + 2;
			System.out.println("	Sono Coppia "+ coppia.getId()+ " sono nel cunicolo OUT");
			stampa();
		}finally{lock.unlock();}
		
	}

	public void coppiaEsceCunicoloOut(Coppia coppia) {
		lock.lock();
		try{
			personeNelCunicolo = personeNelCunicolo - 2;
			ciSonoPassegginiNelCunicolo[1] = false;
			System.out.println("	Sono Coppia "+ coppia.getId()+ " sono uscito");
			stampa();
			
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
			
		}finally{lock.unlock();}
		
		
	}
	
	//SEZIONE GUIDA
	public void guidaEntraCunicolo(Guida guida) throws InterruptedException {
		lock.lock();
		try{
			while (personeInSala + 1 > maxPersoneInSala || personeNelCunicolo + 1 > maxPersoneCunicolo || singoliInAttesaDiUscire+coppieInAttesaDiUscire+guideInAttesaDiUscire > 0) {
				guideInAttesaDiEntrare++;
				System.out.println("		Sono guida "+ guida.getId()+ " volevo entrare ");
				stampa();
				voglioEntrareGuida.await();
				guideInAttesaDiEntrare--;
			}
		
			personeNelCunicolo++;
			System.out.println("		Sono guida "+ guida.getId()+ " sono nel cunicolo IN");
			stampa();
		}finally{lock.unlock();}
		
	}

	public void guidaEntraStanza(Guida guida) {
		lock.lock();
		try{
			
			personeNelCunicolo--;
			personeInSala++;
			numeroGuideInSala++;
			System.out.println("		Sono guida "+ guida.getId()+ " sono nella stanza");
			stampa();
			
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
		
		}finally{lock.unlock();}
		
	}

	public void guidaEntraCunicoloOut(Guida guida) throws InterruptedException {
		lock.lock();
		try{
			while (personeNelCunicolo + 1 > maxPersoneCunicolo || numeroGuideInSala < 1|| coppieInAttesaDiUscire > 0 || singoliInAttesaDiUscire > 0) {
				guideInAttesaDiUscire++;
				System.out.println("		Sono guida "+ guida.getId()+ " volevo uscire");
				stampa();
				voglioUscireGuida.await();
				guideInAttesaDiUscire--;
			}
			
			personeInSala--;
			personeNelCunicolo++;
			numeroGuideInSala--;
			System.out.println("		Sono Guida "+ guida.getId()+ " sono nel cunicolo OUT");
			stampa();
		}finally{lock.unlock();}
		
	}

	public void guidaEsceCunicoloOut(Guida guida) {
		lock.lock();
		try{
			personeNelCunicolo--;
			System.out.println("		Sono Guida "+ guida.getId()+ " sono uscito");
			stampa();
			if(coppieInAttesaDiUscire > 0 && personeNelCunicolo + 2 <= maxPersoneCunicolo ) {
				voglioUscireCoppia.signal();
			}else if(singoliInAttesaDiUscire > 0) {
				voglioUscireSingolo.signal();
			}else if(guideInAttesaDiUscire > 0 && numeroGuideInSala > 1) {
				voglioUscireGuida.signal();
			}else if(guideInAttesaDiEntrare > 0) {
				voglioEntrareGuida.signal();
			}else if(singoliInAttesaDiEntrare > 0) {
				voglioEntrareSingolo.signal();
			}else if(coppieInAttesaDiEntrare > 0) {
				voglioEntrareCoppia.signal();
			}
			
		}finally{lock.unlock();}
	}

	private void stampa() {
		System.out.println("Situazione corridoio: ["+ personeNelCunicolo +"/"+maxPersoneCunicolo + "] sala: ["+ personeInSala +"/"+maxPersoneInSala+"]");
	}

}
