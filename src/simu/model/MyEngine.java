package simu.model;

import simu.framework.ArrivalProcess;
import simu.model.eduni.distributions.Negexp;
import simu.model.eduni.distributions.Normal;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;


import java.util.Random;

public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcess;
    private ServicePoint[] servicePoints;
    private Random random;

    public MyEngine() {
        super();
        servicePoints = new ServicePoint[4];
        servicePoints[0] = new ServicePoint("Ticket Counter", new Negexp(5), eventList, EventType.DEP1);
        servicePoints[1] = new ServicePoint("Non-vegan-counter-1", new Normal(10, 2), eventList, EventType.DEP2);
        servicePoints[2] = new ServicePoint("Non-vegan-counter-2", new Normal(10, 2), eventList, EventType.DEP3);
        servicePoints[3] = new ServicePoint("Vegan-counter", new Normal(15, 3), eventList, EventType.DEP4); // Different type of customer
        arrivalProcess = new ArrivalProcess(new Negexp(10), eventList, EventType.ARR);
        random = new Random();
    }

    protected void initialize() {
        arrivalProcess.generateNextEvent();
    }

    protected void runEvent(Event e) {
        Customer a;
        switch ((EventType) e.getType()) {
            case ARR:
                a = new Customer();
                servicePoints[0].addToQueue(a);
                arrivalProcess.generateNextEvent();
                break;
            case DEP1:
                a = servicePoints[0].removeFromQueue();
                if (random.nextBoolean()) { // Randomly decide if the customer goes to Train 3
                    servicePoints[3].addToQueue(a);
                } else {
                    double queueTimeSP2 = servicePoints[1].getTotalQueueTime();
                    double queueTimeSP3 = servicePoints[2].getTotalQueueTime();
                    System.out.printf("Queue time for counter 1: %.2f, counter 2: %.2f\n", queueTimeSP2, queueTimeSP3);
                    if (queueTimeSP2 <= queueTimeSP3) {
                        servicePoints[1].addToQueue(a);
                    } else {
                        servicePoints[2].addToQueue(a);
                    }
                }
                break;
            case DEP2:
                a = servicePoints[1].removeFromQueue();
                a.setRemovalTime(Clock.getInstance().getClock());
                a.reportResults();
                break;
            case DEP3:
                a = servicePoints[2].removeFromQueue();
                a.setRemovalTime(Clock.getInstance().getClock());
                a.reportResults();
                break;
            case DEP4:
                a = servicePoints[3].removeFromQueue();
                a.setRemovalTime(Clock.getInstance().getClock());
                a.reportResults();
                break;
        }
    }

    protected void tryCEvents() {
        for (ServicePoint sp : servicePoints) {
            if (!sp.isReserved() && sp.isOnQueue()) {
                sp.beginService();
            }
        }
    }

    protected void results() {
        System.out.printf("\nSimulation ended at %.2f\n", Clock.getInstance().getClock());
        System.out.println("Total customers serviced: " + (servicePoints[1].getCustomerServiced() + servicePoints[2].getCustomerServiced() + servicePoints[3].getCustomerServiced()));
    }
}