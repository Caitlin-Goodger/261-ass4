/**
 * Node to make the robot wait
 */
public class WaitNode extends Node {
    public int time;

    public WaitNode(int time) {
        this.time = time; // Wait takes a paramter as to how long to wait
    }

    @Override
    public String toString() {
        return  "wait: " + time + "\n";
    }

    @Override
    public void execute(Robot r) {
        for(int i =0; i<time;i++) { // Wait for as long as the paramter
            r.idleWait();
        }
    }
}
