package triangolare;

import java.util.Stack;

public class Negozio {
	
	private int C; 
	private Stack<Tifoso> tifosiInCoda = new Stack<>();
	private int numeroAttualeDiPersone;
	
	public Negozio(int C) {
		this.C = C;
		this.numeroAttualeDiPersone = 0;
	}
	
	public synchronized int entra(Tifoso t) {
		if(numeroAttualeDiPersone + 1 > C) {
			tifosiInCoda.push(t);
			return 0;
		}
		
		System.out.println("	"+ t + " e' entrato nel negozio ");
		numeroAttualeDiPersone++;
		t.setInsideShop(1);
		return 1;
	}
	
	public synchronized void esce(Tifoso t) {
		numeroAttualeDiPersone--;
		System.out.println("	Sono " + t + " e sono uscito dal negozio");
		
		if(!this.tifosiInCoda.isEmpty()) {
			this.entra(tifosiInCoda.firstElement());
			tifosiInCoda.remove(tifosiInCoda.firstElement());
		}
	}
	
	
}
