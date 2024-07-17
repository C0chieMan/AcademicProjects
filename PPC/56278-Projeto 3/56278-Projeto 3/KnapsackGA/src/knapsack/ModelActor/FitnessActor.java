package knapsack.ModelActor;

import knapsack.Individual;
import knapsack.message.Address;
import knapsack.message.Message;
import knapsack.message.PopulationMessage;
import knapsack.message.FitnessPopulationMessage;

import java.util.ArrayList;
import java.util.List;

public class FitnessActor extends Actor {
    private final Address actorManagerAddress;
    private static final int POP_SIZE = 100000;

    public FitnessActor(Address actorManagerAddress) {
        this.actorManagerAddress = actorManagerAddress;
    }

    @Override
    public void processMessage(Message message) {
        if (message instanceof PopulationMessage) {
            Individual[] population = ((PopulationMessage) message).getPopulation();

            for (int i = 0; i < POP_SIZE; i++) {
				population[i].measureFitness();
			}

            this.send(new FitnessPopulationMessage(population), actorManagerAddress);
        }
    }

}
