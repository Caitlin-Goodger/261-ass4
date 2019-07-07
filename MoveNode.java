/**
 * Move Node for moving the robot
 */
public class MoveNode extends Node{
    public int amount;

    public MoveNode(int a) {
        amount = a; // Move can take an amount to move forward
    }

    @Override
    public String toString() {
        return "move \n";
    }

    @Override
    public void execute(Robot r) {
        for(int i =0;i<amount;i++) { //Move the amount of times of the parameter
            r.move();
        }
    }
}
