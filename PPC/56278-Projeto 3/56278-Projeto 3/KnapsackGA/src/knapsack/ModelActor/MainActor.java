package knapsack.ModelActor;

import java.io.FileWriter;
import java.io.IOException;

import knapsack.message.StartMessage;

public class MainActor {
    public static void main(String[] args) throws InterruptedException, IOException {
        IterationCompletionFlag completionFlag = new IterationCompletionFlag();
        
        FileWriter writer = new FileWriter("KnapsackGA/lib/testactors_times.csv");
		long allStartTime =System.nanoTime();

        for (int i = 0; i < 30; i++) {
            long startTime =System.nanoTime();

            ActorManager actorManager = new ActorManager(completionFlag);
            Actor.sendFromMain(new StartMessage(), actorManager.getAddress());
            completionFlag.waitForCompletion();
            
            long endtime = System.nanoTime();
			writer.write( endtime - startTime + "\n");
        }

        long allEndtime = System.nanoTime();
		writer.write(allEndtime-allStartTime + "");

		writer.close();
    }
}
