package knapsack.message;

import java.util.List;

import knapsack.Individual;

public class mutatedPopulationMessage extends Message {

    private final Individual[] population;

    public mutatedPopulationMessage(Individual[] population) {
        this.population = population;
    }

    public Individual[] getPopulation() {
        return population;
    }
    
}
