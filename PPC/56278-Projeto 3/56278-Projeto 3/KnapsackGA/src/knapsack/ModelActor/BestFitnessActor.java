package knapsack.ModelActor;

import knapsack.Individual;
import knapsack.message.Address;
import knapsack.message.Message;
import knapsack.message.FitnessPopulationMessage;

import java.util.List;

public class BestFitnessActor extends Actor {
    private final Address actorManagerAddress;

    public BestFitnessActor(Address actorManagerAddress) {
        this.actorManagerAddress = actorManagerAddress;
    }

    @Override
    public void processMessage(Message message) {

        if (message instanceof FitnessPopulationMessage) {
            Individual[] population = ((FitnessPopulationMessage) message).getPopulation();
            Individual best = bestOfPopulation(population);
            System.out.println("is " + best + " with " + best.fitness);
        }
    }

    private Individual bestOfPopulation(Individual[]population) {
        Individual best = population[0];
        for (Individual other : population) {
            if (other.fitness > best.fitness) {
                best = other;
            }
        }
        return best;
    }
}
