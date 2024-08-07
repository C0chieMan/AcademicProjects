package knapsack.message;

import java.util.List;

import knapsack.Individual;


public class FitnessPopulationMessage extends Message{
    private final Individual[] population;

    public FitnessPopulationMessage(Individual [] population) {
        this.population = population;
    }

    public Individual[] getPopulation() {
        return population;
    }
}
