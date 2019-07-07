/**
 * Node for Taking Fuel
 */
public class TakeFuelNode extends Node {
    public TakeFuelNode() {

    }

    @Override
    public String toString() {
        return "takeFuel \n";
    }

    @Override
    public void execute(Robot r) {
        r.takeFuel();
    }
}
