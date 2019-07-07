/**
 * Node to turn the robot right
 */
public class TurnRightNode extends Node {

    public TurnRightNode() {
    }

    @Override
    public String toString() {
        return "turnR \n";
    }

    @Override
    public void execute(Robot r) {
        r.turnRight();
    }
}
