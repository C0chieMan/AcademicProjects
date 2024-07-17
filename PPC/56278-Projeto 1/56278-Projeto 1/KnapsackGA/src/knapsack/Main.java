package knapsack;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
	private static final int N_SAMPLES = 1;
	public static void main(String[] args) throws IOException {

		FileWriter writer = new FileWriter("lib/testNoPhaser_times.csv");
		long allStartTime =System.nanoTime();

		for(int i = 0; i< N_SAMPLES; i++){
			long startTime =System.nanoTime();

			KnapsackGAMode2 ga = new KnapsackGAMode2();
			ga.run();

			long endtime = System.nanoTime();
			writer.write( endtime - startTime + "\n");	
		}

		long allEndtime = System.nanoTime();
		writer.write(allEndtime-allStartTime + "");

		writer.close();
	}

	/** 
	public static void main(String[] args) throws IOException {

		FileWriter writer = new FileWriter("lib/testWPhaser_times.csv");
		long allStartTime =System.nanoTime();

		for(int i = 0; i< N_SAMPLES; i++){
			long startTime =System.nanoTime();

			KnapsackGA ga = new KnapsackGA();
			ga.run();

			long endtime = System.nanoTime();
			writer.write( endtime - startTime + "\n");	
		}

		long allEndtime = System.nanoTime();
		writer.write(allEndtime-allStartTime + "");

		writer.close();
	}
	*/
}
