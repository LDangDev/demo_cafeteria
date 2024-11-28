import simu.framework.Engine;
import simu.model.MyEngine;

public class Main {
    public static void main(String[] args) {
        System.out.println("Cafeteria Simulator\n");

        Engine engine = new MyEngine();
        engine.setSimulationTime(100);
        engine.run();
    }
}