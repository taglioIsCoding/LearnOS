import java.util.Random;


public class GelateriaMain {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args){
		int maxPersone = 10;
		int numClienti = 20;
		int numGelatai = 3;
		Random random = new Random(); 
		
		Gelateria g = new Gelateria(maxPersone, numGelatai);
		
		for(int i = 0; i < numGelatai; i++){
			Gelataio gelataio = new Gelataio(g, i,random);
			gelataio.start();
		}
		
		for(int i = 0; i < numClienti; i++){
			Cliente cliente = new Cliente(g, i,random);
			cliente.start();
		}
		

	}

}
