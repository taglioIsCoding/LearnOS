package triangolare;

import java.util.Arrays;

public class Stadio {
	
	private int s;
	private int tifosiCorrenti;
	private int counterTifosi[] = new int[]{0,0,0};
	private int counterFasciaEta[] = new int[]{0,0,0};
	
	public Stadio(int maxTifosi){
		this.s = maxTifosi;
		tifosiCorrenti = 0;
	}
	
	public synchronized int entra(Tifoso t) {
		if(tifosiCorrenti + 1 <= s){
			tifosiCorrenti++;
			aggiornaStatistiche(t);
			return 1;
		}
		return 0;
	}

	private synchronized void aggiornaStatistiche(Tifoso t) {
		switch (t.getSquadra()){
			case "Real C":
				counterTifosi[0]++;
				break;
			case "Java united":
				counterTifosi[1]++;
				break;
			case "A.C. shell":
				counterTifosi[2]++;
				break;
		}
		
		if(t.getEta() < 13){
			counterFasciaEta[0]++;
			return;
		} else if( t.getEta() < 65){
			counterFasciaEta[1]++;
			return;
		} else{
			counterFasciaEta[2]++;
		}
		
		return;
	}

	public synchronized void esci() {
		this.tifosiCorrenti--;
		return;
	}
	
	public String toString(){
		return "Nello stadio sono entrati" + Arrays.toString(counterTifosi) + " nelle fasce d'eta " + Arrays.toString(counterFasciaEta);
	}

}
