package montagneRusse;

import java.util.Stack;
import java.util.concurrent.Semaphore;

public class Stazione {
	
	private int Tmax;
	private int countSulTrenino;
	private int fase;
	private Semaphore mutex;
	private Semaphore visitatoriIngresso;
	private Semaphore visitatoriUscita;
	private Semaphore sospendiTreninoSemaphore;
	
	
	public Stazione(int Tmax) {
		this.Tmax = Tmax;
		this.countSulTrenino = 0;
		this.fase = 0;
		mutex = new Semaphore(1);
		sospendiTreninoSemaphore = new Semaphore(0);
		visitatoriIngresso = new Semaphore(1);
		visitatoriUscita = new Semaphore(0);
	}
	
	public void accesso(Visitatore v){
//		try {
//			mutex.acquire();
//			while(countSulTrenino == Tmax || !treninoInStazione) {
//				sospendiTreninoSemaphore.release();
//				mutex.release();
//				System.out.println("Trenino pieno o non in stazione");
//				visitatoriIngresso.acquire();
//				mutex.acquire();
//				countSulTrenino++;
//				int diff = Tmax - countSulTrenino;
//				System.out.println("Sono riuscito a salire ci sono ancora "+ diff );
//				mutex.release();
//				return;
//			}
//			visitatoriIngresso.acquire();
//			countSulTrenino++;
//			int diff = Tmax - countSulTrenino;
//			System.out.println("Sono riuscito a salire ci sono ancora "+ diff );
//			mutex.release();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}	
		try {
			mutex.acquire();
			while(countSulTrenino == Tmax || fase != 0) {
				sospendiTreninoSemaphore.release();
				mutex.release();
				visitatoriIngresso.acquire();
				mutex.acquire();
			}
			countSulTrenino++;
			System.out.println("Sono visitatrore "+ v.id +" e sono salito nel posto " + countSulTrenino);
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void uscita() {
		try {
			mutex.acquire();
			while(fase != 2) {
				mutex.release();
				visitatoriUscita.acquire();
				mutex.acquire();
			}
			int oldCounter = countSulTrenino;
			countSulTrenino = 0;
			visitatoriIngresso.release(oldCounter);
			System.out.println("Sono scesi tutti dal trenino----------------");
			sospendiTreninoSemaphore.release();
			fase = 0;
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void inizio_giro() {
		try {
			mutex.acquire();
			while(countSulTrenino < Tmax/2 || fase != 0 ) {
				mutex.release();
				System.out.println("Sono trenino non ci sono abbastanza persone per partire");
				sospendiTreninoSemaphore.acquire();
				mutex.acquire();
			}
			fase++;
			System.out.println("--------------------Sono trenino parto con "+ countSulTrenino);
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void fine_giro() {
		try {
			mutex.acquire();
			System.out.println("Sono trenino finito giro ora potranno scendere " + countSulTrenino);
			visitatoriUscita.release();
			fase++;
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

}
