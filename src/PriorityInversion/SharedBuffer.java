package PriorityInversion;

import java.util.ArrayList;

public class SharedBuffer {
    public final ArrayList<Job> jobs;
    public final ArrayList<Character> buffer;
    public boolean locked;

    public SharedBuffer() {
        this.jobs = new ArrayList<Job>();
        this.buffer = new ArrayList<>();
        this.locked = false;
    }

    public void flushBuffer() {
        this.buffer.clear();
    }

    public void printBuffer() {
        System.out.print(buffer);
    }
}
