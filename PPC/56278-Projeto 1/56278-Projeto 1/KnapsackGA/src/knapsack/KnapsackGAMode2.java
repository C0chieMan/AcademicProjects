package knapsack;

import java.util.Random;

public class KnapsackGAMode2 {
	private static final int N_GENERATIONS = 500;
	private static final int POP_SIZE = 100000;
	private static final double PROB_MUTATION = 0.5;
	private static final int TOURNAMENT_SIZE = 3;
    private static final int NUM_CORES = 4;

	private Random r = new Random();

	private Individual[] population = new Individual[POP_SIZE];

	public KnapsackGAMode2() {
		populateInitialPopulationRandomly();
	}

	private void populateInitialPopulationRandomly() {
		/* Creates a new population, made of random individuals */
		for (int i = 0; i < POP_SIZE; i++) {
			population[i] = Individual.createRandom(r);
		}
	}

	public void run() {
        for (int generation = 0; generation < N_GENERATIONS; generation++) {
    
            // Step1 - Calculate Fitness
            Thread[] threads = new Thread[NUM_CORES];
            int individualsPerThread = POP_SIZE / NUM_CORES;
            int remainingIndividuals = POP_SIZE % NUM_CORES;
            int currentIndex1 = 0;
            int currentIndex2 = 0;
            int currentIndex3 = 0;
    
            for (int i = 0; i < NUM_CORES; i++) {
                final int startIndex = currentIndex1;
                final int endIndex = startIndex + individualsPerThread + (i < remainingIndividuals ? 1 : 0);
                currentIndex1 = endIndex;
    
                threads[i] = new Thread(() -> {
                    for (int j = startIndex; j < endIndex; j++) {
                        population[j].measureFitness();
                    }
                });
                threads[i].start();
            }
    
            // Wait for all fitness calculations to complete
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
			
    
            // Step2 - Print the best individual so far.
            Individual best = bestOfPopulation();
            System.out.println("Best at generation " + generation + " is " + best + " with " + best.fitness);
    
            // Step3 - Find parents to mate (cross-over)
            Individual[] newPopulation = new Individual[POP_SIZE];
            newPopulation[0] = best; // The best individual remains
    
    
            for (int i = 0; i < NUM_CORES; i++) {
				final int startIndex = currentIndex2;
				final int endIndex = startIndex + individualsPerThread + (i < remainingIndividuals ? 1 : 0);
				currentIndex2 = endIndex;
	
				threads[i] = new Thread(() -> {
					for (int j = startIndex; j < endIndex; j++) {
					// We select two parents, using a tournament.
					Individual parent1 = tournament(TOURNAMENT_SIZE, r);
					Individual parent2 = tournament(TOURNAMENT_SIZE, r);

					newPopulation[j] = parent1.crossoverWith(parent2, r);
                    }
				});
				threads[i].start();
			}
            
            // Wait for all crossover operations to complete
            for (Thread thread : threads) {
                if (thread != null) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    
            // Step4 - Mutate
    
            for (int i = 0; i < NUM_CORES; i++) {
				final int startIndex = currentIndex3;
				final int endIndex = startIndex + individualsPerThread + (i < remainingIndividuals ? 1 : 0);
                currentIndex3 = endIndex;
	
				threads[i] = new Thread(() -> {
					for (int j = startIndex; j < endIndex; j++) {
						if (r.nextDouble() < PROB_MUTATION) {
							newPopulation[j].mutate(r);
						}
			}
				});
				threads[i].start();
			}
    
            // Wait for all mutation operations to complete
            for (Thread thread : threads) {
                if (thread != null) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    
            population = newPopulation;
        }
    }
    

	private Individual tournament(int tournamentSize, Random r) {
		/*
		 * In each tournament, we select tournamentSize individuals at random, and we
		 * keep the best of those.
		 */
		Individual best = population[r.nextInt(POP_SIZE)];
		for (int i = 0; i < tournamentSize; i++) {
			Individual other = population[r.nextInt(POP_SIZE)];
			if (other.fitness > best.fitness) {
				best = other;
			}
		}
		return best;
	}

	private Individual bestOfPopulation() {
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