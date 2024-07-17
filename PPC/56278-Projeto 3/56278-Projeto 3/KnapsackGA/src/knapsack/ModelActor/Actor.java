package knapsack.ModelActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import knapsack.message.Address;
import knapsack.message.Message;
import knapsack.message.StopMessage;


/*
 * The Actor model runs on one thread of execution, and it does not share its state with other actors.
 * Java does not enforce this second requirement, so it is up to the programmers.
 * An actor has a (synchronized) mailbox, which other actors know the end of it (Address)
 * In distributed environments, this address can be a socket (ip/port) or message queue.
 * Synchronization still needs to be made correct by the programmer, especially the complete workflow,
 * based on how each actor is programmed.
 */
public abstract class Actor extends Thread {

    private boolean running = true;
    private Queue<Message> mailbox = new ConcurrentLinkedDeque<>();
    private List<Actor> children = new ArrayList<>();

    public Actor() {
        this.start();
    }

    public Address getAddress() {
        return mailbox::add;
    }

    public abstract void processMessage(Message m);

    public void run() {
        while (running || !mailbox.isEmpty()) {
            Message m = mailbox.poll();
            if (m != null) {
                processMessage(m);
                if (m instanceof StopMessage) {
                    running = false;
                    for (Actor a : children) {
                        this.send(m, a.getAddress());
                    }
                }
            }
        }
    }

    public void send(Message m, Address a) {
        m.setSenderAddress(this.getAddress());
        a.receive(m);
    }

    public static void sendFromMain(Message m, Address a) {
        a.receive(m);
    }

    public Actor launchActor(Actor a) {
        this.children.add(a);
        return a;
    }

}
