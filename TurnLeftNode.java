/**
 * Node to turn the robot to the left
 */
public class TurnLeftNode extends Node {

    public TurnLeftNode() {
    }

    @Override
    public String toString() {
        return "turnL \n";
    }

    @Override
    public void execute(Robot r) {
        r.turnLeft();
    }
}
