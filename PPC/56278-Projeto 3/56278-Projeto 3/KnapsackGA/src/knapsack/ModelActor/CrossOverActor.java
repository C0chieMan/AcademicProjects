package knapsack.ModelActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import knapsack.Individual;
import knapsack.message.Address;
import knapsack.message.Message;
import knapsack.message.StartMessage;
import knapsack.message.crossOverPopulationMessage;
import knapsack.message.PopulationMessage;

public class CrossOverActor extends Actor {
    private final Address actorManagerAddress;
    private int receivedSubPopulations = 0;
    private static final int POP_SIZE = 100000;
    private static final int TOURNAMENT_SIZE = 3;
    private Random r = new Random();

    public CrossOverActor(Address actorManagerAddress) {
        this.actorManagerAddress = actorManagerAddress;
    }

    @Override
    public void processMessage(Message m) {
        if (m instanceof PopulationMessage) {

            Individual[] population = ((PopulationMessage) m).getPopulation();
            Individual best = bestOfPopulation(population);

            Individual[] newPopulation = new Individual[POP_SIZE];
            newPopulation[0] = best; // The best individual remains

            for (int i = 1; i < POP_SIZE; i++) {
				// We select two parents, using a tournament.
				Individual parent1 = tournament(TOURNAMENT_SIZE, r, population);
				Individual parent2 = tournament(TOURNAMENT_SIZE, r, population);
				newPopulation[i] = parent1.crossoverWith(parent2, r);
				//System.out.println(parent1);
				//System.out.println(parent2);
			}
            this.send(new crossOverPopulationMessage(newPopulation), actorManagerAddress);
            
            
        }
    }

    private Individual tournament(int tournamentSize, Random r, Individual[] population) {
		Individual best = population[r.nextInt(POP_SIZE)];

		for (int i = 0; i < tournamentSize; i++) {
			Individual other = population[r.nextInt(POP_SIZE)];
			if (other.fitness > best.fitness) {
				best = other;
			}
		}
		return best;
	}

    private Individual bestOfPopulation(Individual [] population) {
		/*
		 * Returns the best individual of the population.
		 */
		Individual best = population[0];
		for (Individual other : population) {
			if (other.fitness > best.fitness) {
				best = other;
			}
		}
		return best;
	}

    
}
