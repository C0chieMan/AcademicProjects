package knapsack.message;

import java.util.List;

import knapsack.Individual;

public class PopulationMessage extends Message {

    private final Individual[] population;

    public PopulationMessage(Individual[] population) {
        this.population = population;
    }

    public Individual[] getPopulation() {
        return population;
    }   
    
}
