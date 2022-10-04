package triangolare;

import java.util.Random;

public class Main {

	/**
	 * @param args
	 */
	
	private final static int MAX_NUM = 20;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String teams[] = new String[]{"Real C", "Java united", "A.C. shell"};
		
		Random r= new Random(System.currentTimeMillis());
		int NT;
		do{
			NT = r.nextInt(MAX_NUM);
		}while (NT < 3);
		Stadio S = new Stadio(NT - 3);
		Negozio negozio = new Negozio(2\);
		Tifoso Tif[] = new Tifoso[NT];
		
		System.out.println("Per questo torneo ci saranno " + NT + " tifosi e " + (NT - 3) + " posti allo stadio");
		
		for(int i = 0; i < NT; i++){
			Tif[i] = new Tifoso(S, r.nextInt(100), teams[r.nextInt(3)] , r, negozio, i);
		}
		
		for(Tifoso tifoso: Tif){
			tifoso.start();
		}
		
		for(Tifoso tifoso: Tif){
			try {
				tifoso.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(S);
		
	}

}
