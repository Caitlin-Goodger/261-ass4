/**
 * Node of Shield, is it on of off
 */
public class ShieldNode extends Node{
    boolean shieldOn;

    public ShieldNode(boolean shieldOn) {
        this.shieldOn = shieldOn;
    }

    @Override
    public String toString() {
        if(shieldOn) {
            return "shield on \n";
        }
        return "shield off \n ";
    }

    @Override
    public void execute(Robot r) {
        r.setShield(shieldOn);
    }
}
