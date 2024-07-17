package knapsack.message;

import java.util.List;

import knapsack.Individual;

public class crossOverPopulationMessage extends Message {
    private final Individual[] population;

    public crossOverPopulationMessage(Individual[] population) {
        this.population = population;
    }

    public Individual[] getPopulation() {
        return population;
    }
    
}
