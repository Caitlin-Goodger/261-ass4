/**
 * Class for Loop Node
 */
public class LoopNode extends Node {
    public BlockNode block; // The Block of nodes inside the loop

    public LoopNode(BlockNode block) {
        this.block = block;
    }

    @Override
    public String toString() {
        String str = "Loop \n";
        for( RobotProgramNode r : block.components) {
            str = str + " " + r.toString();
        }
        return str;
    }

    @Override
    public void execute(Robot r) {
        block.execute(r);
    }
}
