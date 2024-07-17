package knapsack.message;

@FunctionalInterface
public interface Address {
    public void receive(Message m);
}

