import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class Coin extends RecursiveTask<Integer> {

	public static final int LIMIT = 999;
	private int[] coins;
    private int index;
    private int accumulator;
	static ForkJoinPool pool = new ForkJoinPool();
	private static final int THRESHOLD = 5;

	public Coin(int[] coins, int index, int accumulator) {
        this.coins = coins;
        this.index = index;
        this.accumulator = accumulator;
    }

	@Override
	protected Integer compute() {

		if (index >= coins.length) {
			if (accumulator < LIMIT) {
				return accumulator;
			}
			return -1;
		}

		if (accumulator + coins[index] > LIMIT) {
			return -1;
		}

		/*
		 * Tested this 3 granularity methods with different values has seen in the box plot of the report
		 * I left the best outcome uncommented
		 */

		if (index >= THRESHOLD){
			return seq(coins, index, accumulator);
		}

		//if (pool.getQueuedTaskCount() > 4 * Runtime.getRuntime().availableProcessors() ) return seq(coins, index, accumulator);

		//if ( RecursiveTask.getSurplusQueuedTaskCount() > 6 ) return seq(coins, index, accumulator);


		Coin aC = new Coin(coins, index + 1, accumulator);
        aC.fork();

        Coin bC = new Coin(coins, index + 1, accumulator + coins[index]);
		bC.fork();
		
		int b = bC.join();
		int a = aC.join();

		return Math.max(a, b);
		
	}
	
	public static int[] createRandomCoinSet(int N) {
		int[] r = new int[N];
		for (int i = 0; i < N ; i++) {
			if (i % 10 == 0) {
				r[i] = 400;
			} else {
				r[i] = 4;
			}
		}
		return r;
	}

	public static void main(String[] args) throws IOException {
		//BufferedWriter seqW = new BufferedWriter(new FileWriter("Files/sequencial.csv"));
		BufferedWriter parW = new BufferedWriter(new FileWriter("Files/test.csv"));
		int nCores = Runtime.getRuntime().availableProcessors();

		int[] coins = createRandomCoinSet(30);

		int repeats = 40;

		for (int i=0; i<repeats; i++) {
			
			long seqInitialTime = System.nanoTime();
			int rs = seq(coins, 0, 0);
			long seqEndTime = System.nanoTime() - seqInitialTime;
			System.out.println(nCores + ";Sequential;" + seqEndTime);
			//seqW.write(seqEndTime +"\n");
			
			
			long parInitialTime = System.nanoTime();
			int rp = par(coins, 0, 0);
			long parEndTime = System.nanoTime() - parInitialTime;
			System.out.println(nCores + ";Parallel;" + parEndTime);
			parW.write(parEndTime + "\n");
			
			 
			if (rp != rs) {
				System.out.println("Wrong Result!");
				System.exit(-1);
			}
			
		}
		parW.close();
		//seqW.close();

	}

	private static int seq(int[] coins, int index, int accumulator) {
		
		if (index >= coins.length) {
			if (accumulator < LIMIT) {
				return accumulator;
			}
			return -1;
		}
		if (accumulator + coins[index] > LIMIT) {
			return -1;
		}
		int a = seq(coins, index+1, accumulator);
		int b = seq(coins, index+1, accumulator + coins[index]);
		return Math.max(a,  b);
	}
	
	private static int par(int[] coins, int index, int accumulator) {
		return pool.invoke(new Coin(coins, index, accumulator));
	}

}