package knapsack.ModelActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import knapsack.Individual;
import knapsack.message.Message;
import knapsack.message.PopulationMessage;
import knapsack.message.FitnessPopulationMessage;
import knapsack.message.StartMessage;
import knapsack.message.StopMessage;
import knapsack.message.crossOverPopulationMessage;
import knapsack.message.mutatedPopulationMessage;


public class ActorManager extends Actor {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
	private Actor fitnessActor;
    private Actor bestFitnessActor;
	private Actor crossOverActor;
	private Actor mutateActor;
	private int currentGeneration = 0;
	private IterationCompletionFlag completionFlag;

	private Random r = new Random();

	private Individual[] population = new Individual[POP_SIZE];

    private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		for (int i = 0; i < POP_SIZE; i++) {
			population[i] = Individual.createRandom(r);
		}
	}

	public ActorManager(IterationCompletionFlag completionFlag) {
		populateInitialPopulationRandomly();
		this.completionFlag = completionFlag;
		
		fitnessActor = launchActor(new FitnessActor(this.getAddress()));
		bestFitnessActor = launchActor(new BestFitnessActor(this.getAddress())); // Initial counter value
		crossOverActor = launchActor(new CrossOverActor(this.getAddress()));
		mutateActor = launchActor(new MutateActor(this.getAddress()));
	}

    @Override
    public void processMessage(Message m) {


		if (m instanceof StartMessage) {
			
			this.send(new PopulationMessage(population), fitnessActor.getAddress());

		} else if (m instanceof FitnessPopulationMessage) {
			
			Individual[] processedPopulation = ((FitnessPopulationMessage) m).getPopulation();
			this.send(new FitnessPopulationMessage(processedPopulation), bestFitnessActor.getAddress());
			this.send(new PopulationMessage(population), crossOverActor.getAddress());
			System.out.print("Best at generation " + currentGeneration + " ");

		} else if (m instanceof crossOverPopulationMessage) {

			Individual[] crossOverPopulation = ((crossOverPopulationMessage) m).getPopulation();
			this.send(new crossOverPopulationMessage(crossOverPopulation), mutateActor.getAddress());
			
		} else if (m instanceof mutatedPopulationMessage) {
			
			if (currentGeneration < N_GENERATIONS - 1) {
				population = ((mutatedPopulationMessage) m).getPopulation();
				this.send(new PopulationMessage(population), fitnessActor.getAddress());
				currentGeneration++;

			} else {
				stopAllActors();
				completionFlag.setIterationComplete(true);
			}
		}
				
	}

	private void stopAllActors() {
    StopMessage stopMsg = new StopMessage();
    send(stopMsg, fitnessActor.getAddress());
    send(stopMsg, bestFitnessActor.getAddress());
    send(stopMsg, crossOverActor.getAddress());
    send(stopMsg, mutateActor.getAddress());
    send(stopMsg, this.getAddress()); 
}
				 
}


