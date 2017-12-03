package PriorityInversion;

import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        //Simulation 1:
        runRandomSimulation();
        runPartDSequence();
    }

    public static void runPartDSequence() throws Exception {
        System.out.println("Part d (2):");
        SharedBuffer t13Buffer = new SharedBuffer();
        ArrayList<Job> jobs = new ArrayList<Job>();
        jobs.add(createT3Job(1, t13Buffer));
        jobs.add(createT2Job(3));
        jobs.add(createT3Job(6, t13Buffer));
        jobs.add(createT1Job(8, t13Buffer));
        jobs.add(createT2Job(10));
        jobs.add(createT3Job(12, t13Buffer));
        jobs.add(createT1Job(26, t13Buffer));
        System.out.println("Created Jobs:");
        for (Job job: jobs) {
            System.out.println("<" + job.arrivalTime + ", " + job.type + ">");
        }
        System.out.println("Running Simulation:");
        Simulation simulation = new Simulation(jobs, t13Buffer);
        simulation.run();
    }

    public static void runPriorityInversionExample() throws Exception {
        SharedBuffer t13Buffer = new SharedBuffer();
        ArrayList<Job> jobs = new ArrayList<Job>();
        jobs.add(createT3Job(1, t13Buffer));
        jobs.add(createT2Job(2));
        jobs.add(createT1Job(3, t13Buffer));
        Simulation simulation = new Simulation(jobs, t13Buffer);
        simulation.run();
    }

    public static void runRandomSimulation() throws Exception {
        SharedBuffer t13Buffer = new SharedBuffer();
        ArrayList<Job> jobs = new ArrayList<Job>();

        Random random = new Random();
        System.out.println("Part d (1):");
        for(int i = 0; i < 10; i++) {
            int rand = random.nextInt(3);
            int rand2 = random.nextInt(10);
            switch (rand) {
                case 0:
                    jobs.add(createT1Job(rand2 + 1, t13Buffer));
                    break;
                case 1:
                    jobs.add(createT2Job(rand2 + 1));
                    break;
                case 2:
                    jobs.add(createT3Job(rand2 + 1, t13Buffer));
                    break;
            }
        }
        System.out.println("Created Random Jobs:");
        for (Job job: jobs) {
            System.out.println("<" + job.arrivalTime + ", " + job.type + ">");
        }
        System.out.println("Running Simulation:");
        Simulation simulation = new Simulation(jobs, t13Buffer);
        simulation.run();
    }

    public static Job createT1Job(int arrivalTime, SharedBuffer sharedBuffer) {
        return new Job(arrivalTime, 1, 3, 3, sharedBuffer);
    }

    public static Job createT2Job(int arrivalTime) {
        return new Job(arrivalTime, 2, 2, 10, new SharedBuffer());
    }

    public static Job createT3Job(int arrivalTime, SharedBuffer sharedBuffer) {
        return new Job(arrivalTime, 3, 1, 3, sharedBuffer);
    }

}
