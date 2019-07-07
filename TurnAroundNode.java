/**
 * Node to turn node around
 */
public class TurnAroundNode extends Node {

    public TurnAroundNode() {
    }

    @Override
    public String toString() {
        return "turnAround \n";
    }

    @Override
    public void execute(Robot r) {
        r.turnAround();
    }
}
