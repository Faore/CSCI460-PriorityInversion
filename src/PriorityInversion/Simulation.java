package PriorityInversion;

import java.util.ArrayList;

public class Simulation {
    int currentTick;
    Job preemptedJob;
    Job currentlyRunningJob;

    public final ArrayList<Job> futureJobs;
    public final ArrayList<Job> currentJobs;
    public final ArrayList<Job> completedJobs;
    public final SharedBuffer sharedBuffer;

    public Simulation(ArrayList<Job> jobs, SharedBuffer sharedBuffer) {
        this.currentTick = 0;
        this.futureJobs = jobs;
        this.currentJobs = new ArrayList<>();
        this.completedJobs = new ArrayList<>();
        this.currentlyRunningJob = null;
        this.sharedBuffer = sharedBuffer;
    }

    public void run() throws Exception {
        //While there are still jobs to do.
        while(!futureJobs.isEmpty() || !currentJobs.isEmpty()) {
            runTick();
        }
    }

    public void runTick() throws Exception {
        ArrayList<Job> toMove = new ArrayList<Job>();
        //Add any future jobs to the currentJob list
        for (Job job: futureJobs) {
            if(job.arrivalTime == currentTick) {
                toMove.add(job);
            }
        }
        for(Job job: toMove) {
            currentJobs.add(job);
            futureJobs.remove(job);
        }
        //Find out what job to run on this slice.
        if(!currentJobs.isEmpty()) {
            //There are jobs to run.
            if(currentlyRunningJob == null) {

                if(sharedBuffer.locked) {
                    //There is a preempted T3 (It will be the first one in the currentJobs list). We need to finish it, or run and task with a higher priority that doesn't share a buffer with it.
                    Job preemptedT3 = null;
                    for (Job job: currentJobs) {
                        if(job.type == 3) {
                            preemptedT3 = job;
                            break;
                        }
                    }
                    int highestPriority = Integer.MIN_VALUE;
                    Job highestPriorityJob = null;
                    for(Job job: currentJobs) {
                        if(job.canPreempt(preemptedT3) && job.priority > highestPriority) {
                            highestPriorityJob = job;
                        }
                    }
                    if(highestPriorityJob == null) {
                        preemptedJob = currentlyRunningJob;
                        currentlyRunningJob = preemptedT3;
                    } else {
                        preemptedJob = currentlyRunningJob;
                        currentlyRunningJob = highestPriorityJob;
                    }
                } else {
                    //There is no currently running job to preempt. We can run any job. Find the highest priority job and set it to currently running.
                    int highestPriority = Integer.MIN_VALUE;
                    Job highestPriorityJob = null;
                    for (Job job: currentJobs) {
                        if(job.priority > highestPriority) {
                            highestPriority = job.priority;
                            highestPriorityJob = job;
                        }
                    }
                    preemptedJob = currentlyRunningJob;
                    currentlyRunningJob = highestPriorityJob;
                }
            } else {
                //There is a currently running job. To preempt it, we need to find a higher priority job that isn't sharing a buffer with it.

                ArrayList<Job> possibleJobs = new ArrayList<Job>();
                for(Job job : currentJobs) {
                    if(job.canPreempt(currentlyRunningJob)) {
                        if(sharedBuffer.locked) {
                            //There is a preempted T3 (It will be the first one in the currentJobs list). We need to finish it, or run and task with a higher priority that doesn't share a buffer with it.
                            Job preemptedT3 = null;
                            for (Job pjob : currentJobs) {
                                if (pjob.type == 3) {
                                    preemptedT3 = pjob;
                                    break;
                                }
                            }
                            if(job.canPreempt(preemptedT3)) {
                                possibleJobs.add(job);
                            }
                        } else {
                            possibleJobs.add(job);
                        }
                    }
                }
                //Look through the possible jobs and find the highest priority job.
                int highestPriority = Integer.MIN_VALUE;
                Job highestPriorityJob = null;
                for (Job job: possibleJobs) {
                    if(job.priority > highestPriority) {
                        highestPriority = job.priority;
                        highestPriorityJob = job;
                    }
                }
                if(highestPriorityJob == null) {
                    preemptedJob = currentlyRunningJob;
                } else {
                    preemptedJob = currentlyRunningJob;
                    currentlyRunningJob = highestPriorityJob;
                }
            }
        }

        //Run the currently running job if possible.
        if(this.currentlyRunningJob != null) {
            if(this.preemptedJob != null) {
                preemptedJob.preempt(currentlyRunningJob);
            }
            this.currentlyRunningJob.runSlice();
            if(this.currentlyRunningJob.remainingTime < 1) {
                //This job just finished. Remove it from the queue.
                currentJobs.remove(currentlyRunningJob);
                completedJobs.add(currentlyRunningJob);
                this.currentlyRunningJob = null;
            }
        }
        currentTick++;
    }
}
