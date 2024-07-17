package knapsack.ModelActor;

import java.util.List;
import java.util.Random;

import knapsack.Individual;
import knapsack.message.Address;
import knapsack.message.Message;
import knapsack.message.PopulationMessage;
import knapsack.message.crossOverPopulationMessage;
import knapsack.message.mutatedPopulationMessage;

public class MutateActor extends Actor {
    private final Address actorManagerAddress;
    private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
    private Random r = new Random();

    public MutateActor(Address actorManagerAddress) {
        this.actorManagerAddress = actorManagerAddress;
    }

    @Override
    public void processMessage(Message m) {
        if (m instanceof crossOverPopulationMessage) {
            Individual[] newPopulation = ((crossOverPopulationMessage) m).getPopulation();

            for (int i = 1; i < POP_SIZE; i++) {
				if (r.nextDouble() < PROB_MUTATION) {
					newPopulation[i].mutate(r);
				}
			}
            this.send(new mutatedPopulationMessage(newPopulation), actorManagerAddress);

        }
    }   
}
