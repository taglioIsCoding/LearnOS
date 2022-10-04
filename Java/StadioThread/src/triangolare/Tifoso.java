package triangolare;

import java.util.Random;

public class Tifoso extends Thread{

	private Stadio m;
	private Negozio negozio;
	private int insideShop;
	private int eta;
	private String squadra;
	private Random r;
	private int index;
	
	
	public Tifoso(Stadio m, int eta, String squadra, Random r, Negozio negozio, int i){
		this.m = m;
		this.negozio = negozio;
		this.eta = eta;
		this.squadra = squadra;
		this.r = r;
		this.index = i;
		this.insideShop = 0;
	}
	

	public void run(){
		int entrato;
		int tentativi = 0;
		do{
			tentativi = r.nextInt(3);
		}while(tentativi <= 0);
		
		int tentativiFatti = 0;
		
		while(tentativiFatti < tentativi){
			entrato = m.entra(this);
			if(r.nextInt() % 2 == 0) {
				vaiAlNegozio();
			}
			if(entrato == 1){
				try {
					System.out.println("Sono " + this + " e sono entrato");
					Thread.sleep(r.nextInt(10));
					m.esci();
					System.out.println("Sono " + this + " e sono uscito");
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				try {
					System.out.println("Sono " + this + " e sto aspettando per entrare");
					tentativiFatti++;
					Thread.sleep(r.nextInt(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Sono " + this + " e mi sono stufato! Dovevo tentare " + tentativi);
		return;
	}
	
	private void vaiAlNegozio() {
		this.insideShop = this.negozio.entra(this);
		while (this.insideShop != 1) {
			try {
				System.out.println("	Sono " + this + " e sono in coda al negozio");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(r.nextInt(10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.negozio.esce(this);
	}
	
	public void setInsideShop(int insideShop) {
		this.insideShop = insideShop;
	}
	
	public int getEta() {
		return eta;
	}

	public String getSquadra() {
		return squadra;
	}

	public String toString(){
		return this.index +" un tifoso del " + this.squadra + " di eta " + this.eta;
	}
}
