package simu.model;

import simu.model.eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.LinkedList;

public class ServicePoint {
    private static final String GREEN = "\033[0;32m";
    private static final String WHITE = "\033[0;37m";
    private LinkedList<Customer> queue;
    private ContinuousGenerator generator;
    private EventList eventList;
    private EventType eventTypeScheduled;
    private String name;
    private double serviceTimeSum;
    private int customerServiced;
    private boolean reserved = false;

    public ServicePoint(String name, ContinuousGenerator g, EventList tl, EventType type) {
        this.name = name;
        this.generator = g;
        this.eventList = tl;
        this.eventTypeScheduled = type;

        this.serviceTimeSum = 0;
        this.customerServiced = 0;

        queue = new LinkedList<>();
    }

    public void addToQueue(Customer a) {
        queue.addFirst(a);
    }

    public Customer removeFromQueue() {
        if (queue.size() > 0) {
            Customer a = queue.removeLast();
            customerServiced++;
            reserved = false;
            return a;
        } else
            return null;
    }

    public void beginService() {
        System.out.printf("%sStarting service %s for the customer #%d%s\n", GREEN, name, queue.peek().getId(), WHITE);

        reserved = true;
        double serviceTime = generator.sample();
        eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));
    }

    public boolean isReserved() {
        return reserved;
    }

    public boolean isOnQueue() {
        return queue.size() > 0;
    }

    public int getCustomerServiced() {
        return customerServiced;
    }

    public double getMeanServiceTime() {
        return serviceTimeSum / customerServiced;
    }

    public double getTotalQueueTime() {
        double totalQueueTime = 0;
        for (Customer customer : queue) {
            totalQueueTime += Clock.getInstance().getClock() - customer.getArrivalTime();
        }
        return totalQueueTime;
    }
}