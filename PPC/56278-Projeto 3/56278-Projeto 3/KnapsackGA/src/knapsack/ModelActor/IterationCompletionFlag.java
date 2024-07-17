package knapsack.ModelActor;

public class IterationCompletionFlag {
    private volatile boolean isIterationComplete = false;

    public synchronized void setIterationComplete(boolean complete) {
        isIterationComplete = complete;
        notifyAll();
    }

    public synchronized void waitForCompletion() throws InterruptedException {
        while (!isIterationComplete) {
            wait();
        }
        isIterationComplete = false; // Reset for next iteration
    }
}
