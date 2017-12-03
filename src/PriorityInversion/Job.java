package PriorityInversion;

public class Job {
    public final int arrivalTime;
    public final int priority;
    public final int type;
    public final SharedBuffer buffer;
    public final int runtime;
    public int remainingTime;

    public Job(int arrivalTime, int type, int priority, int runtime, SharedBuffer buffer) {
        this.arrivalTime = arrivalTime;
        this.type = type;
        this.priority = priority;
        this.buffer = buffer;
        this.buffer.jobs.add(this);
        this.runtime = runtime;
        this.remainingTime = runtime;
    }

    public void runSlice() throws Exception {
        if(remainingTime < 1) {
            throw new Exception("Tried to run job that has already completed.");
        }
        this.buffer.locked = true;
        //Add to the buffer.
        this.buffer.buffer.add(String.valueOf(type).charAt(0));
        remainingTime--;
        if(remainingTime < 1) {
            printBuffer();
            this.buffer.locked = false;
        }
    }

    public void preempt(Job job) {
        //Ignore this request if you're being preempted by yourself. That's just silly.
        if(job != this) {
            printBuffer();
            System.out.println("(" + this.type + " preempted by " + job.type + ")");
        }
    }

    public boolean canPreempt(Job job) {
        if(this.priority <= job.priority) {
            //This job can't preempt given job because its priority is too low.
            return false;
        }
        if(this.buffer.jobs.contains(job)) {
            //This job can't preempt the given job because it shares a buffer with this one.
            return false;
        }
        return true;
    }

    public void printBuffer() {
        System.out.print("T" + type + ":");
        buffer.printBuffer();
        System.out.println(":T" + type);
        buffer.flushBuffer();
    }
}
