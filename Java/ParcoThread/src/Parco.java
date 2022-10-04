import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parco {
	
	private int maxVisitatoriNelParco;
	private int visitatoriStagione;
	private int visitatoriStagioneNord;
	private int visitatoriStagioneSud;
	private int visitatoriNelParco;
	
	private int numPrioritaInAttesaNord;
	private int numPrioritaInAttesaSud;
	private int numNormaliInAttesaNord;
	private int numNormaliInAttesaSud;
	
	private Lock lock;
	private Condition voglioEntrareNormaleNord;  
	private Condition voglioEntrareNormaleSud;
	private Condition voglioEntrarePrioritaNord;  
	private Condition voglioEntrarePrioritaSud;
	
	
	

	public Parco(int maxVisitatoriNelParco, int visitatoriStagione, int visitatoriStagioneNord,
			int visitatoriStagioneSud) {
		this.maxVisitatoriNelParco = maxVisitatoriNelParco;
		this.visitatoriStagione = visitatoriStagione;
		this.visitatoriStagioneNord = visitatoriStagioneNord;
		this.visitatoriStagioneSud = visitatoriStagioneSud;
		
		this.visitatoriNelParco = 0;
		this.numPrioritaInAttesaNord = 0;
		this.numPrioritaInAttesaSud = 0;
		this.numNormaliInAttesaNord = 0;
		this.numNormaliInAttesaSud = 0;
		
		this.lock = new ReentrantLock();
		this.voglioEntrareNormaleNord = lock.newCondition();
		this.voglioEntrareNormaleSud = lock.newCondition();
		this.voglioEntrarePrioritaNord = lock.newCondition();
		this.voglioEntrarePrioritaSud = lock.newCondition();
		
	}

	public void entraNordNormale(Visitatore v) throws InterruptedException {
		lock.lock();
		try{
			
			while(maxVisitatoriNelParco < visitatoriNelParco + 1 || visitatoriStagioneNord +1 > visitatoriStagioneSud + (visitatoriStagione / 2) || numPrioritaInAttesaNord > 0){
				numNormaliInAttesaNord++;
				System.out.println("Sono Visitatore "+ v.getId()+ " mi sono sospeso entrando da nord");
				voglioEntrareNormaleNord.await();
				numNormaliInAttesaNord--;
			}
			
			System.out.println("Sono Visitatore "+ v.getId()+ " sono entrato nel parco da nord");
			visitatoriStagioneNord++;
			if(maxVisitatoriNelParco < visitatoriNelParco + 1) {
				if(numPrioritaInAttesaSud > 0) {
					voglioEntrarePrioritaSud.signal();
				}else if(numNormaliInAttesaSud > 0) {
					voglioEntrareNormaleSud.signal();
				}
			}
			
		}finally {
			lock.unlock();
		}
		
	}
	
	public void entraNordPriorita(Visitatore v) throws InterruptedException {
		lock.lock();
		try{
			
			while(maxVisitatoriNelParco < visitatoriNelParco + 1 || visitatoriStagioneNord +1 > visitatoriStagioneSud + (visitatoriStagione / 2)){
				numPrioritaInAttesaNord++;
				System.out.println("Sono Visitatore "+ v.getId()+ " mi sono sospeso entrando da nord con priorita");
				voglioEntrarePrioritaNord.await();
				numPrioritaInAttesaNord--;
			}
			
			System.out.println("Sono Visitatore "+ v.getId()+ " sono entrato nel parco da nord con priorita");
			visitatoriStagioneNord++;
			if(maxVisitatoriNelParco < visitatoriNelParco + 1) {
				if(numPrioritaInAttesaSud > 0) {
					voglioEntrarePrioritaSud.signal();
				}else if(numNormaliInAttesaSud > 0) {
					voglioEntrareNormaleSud.signal();
				}
			}
			
		}finally {
			lock.unlock();
		}
		
	}

	public void entraSudNormale(Visitatore v) throws InterruptedException {
		lock.lock();
		try{
			
			while(maxVisitatoriNelParco < visitatoriNelParco + 1 || visitatoriStagioneSud +1 > visitatoriStagioneNord + (visitatoriStagione / 2) || numPrioritaInAttesaSud > 0){
				numNormaliInAttesaSud++;
				System.out.println("Sono Visitatore "+ v.getId()+ " mi sono sospeso entrando da sud");
				voglioEntrareNormaleSud.await();
				numNormaliInAttesaSud--;
			}
			
			System.out.println("Sono Visitatore "+ v.getId()+ " sono entrato nel parco da sud");
			visitatoriStagioneSud++;
			
			if(maxVisitatoriNelParco < visitatoriNelParco + 1) {
				if(numPrioritaInAttesaNord > 0) {
					voglioEntrarePrioritaNord.signal();
				}else if(numNormaliInAttesaNord > 0) {
					voglioEntrareNormaleNord.signal();
				}
			}
			
		}finally {
			lock.unlock();
		}
		
	}
	
	public void entraSudPriorita(Visitatore v) throws InterruptedException {
		lock.lock();
		try{
			
			while(maxVisitatoriNelParco < visitatoriNelParco + 1 || visitatoriStagioneSud +1 > visitatoriStagioneNord + (visitatoriStagione / 2)){
				numPrioritaInAttesaSud++;
				System.out.println("Sono Visitatore "+ v.getId()+ " mi sono sospeso entrando da sud con priorita");
				voglioEntrarePrioritaSud.await();
				numPrioritaInAttesaSud--;
			}
			
			visitatoriStagioneSud++;
			System.out.println("Sono Visitatore "+ v.getId()+ " sono entrato nel parco da sud con priorita");
			if(maxVisitatoriNelParco < visitatoriNelParco + 1) {
				if(numPrioritaInAttesaNord > 0) {
					voglioEntrarePrioritaNord.signal();
				}else if(numNormaliInAttesaNord > 0) {
					voglioEntrareNormaleNord.signal();
				}
			}
			
		}finally {
			lock.unlock();
		}
		
		
	}
	
	public void esci(Visitatore v) {
		lock.lock();
		try{
			System.out.println("	Sono Visitatore "+ v.getId()+ " sono uscit dal parco");
			visitatoriNelParco--;
			if(visitatoriStagioneNord > visitatoriStagioneSud) {
				if(numPrioritaInAttesaNord > 0) {
					voglioEntrarePrioritaNord.signal();
				}else if(numNormaliInAttesaNord > 0) {
					voglioEntrareNormaleNord.signal();
				}else if(numPrioritaInAttesaSud > 0) {
					voglioEntrarePrioritaSud.signal();
				}else if(numNormaliInAttesaSud > 0) {
					voglioEntrareNormaleSud.signal();
				}
			}else {
				if(numPrioritaInAttesaSud > 0) {
					voglioEntrarePrioritaSud.signal();
				}else if(numNormaliInAttesaSud > 0) {
					voglioEntrareNormaleSud.signal();
				}else if(numPrioritaInAttesaNord > 0) {
					voglioEntrarePrioritaNord.signal();
				}else if(numNormaliInAttesaNord > 0) {
					voglioEntrareNormaleNord.signal();
				}
			}
		}finally {
			lock.unlock();
		}
	}

}
