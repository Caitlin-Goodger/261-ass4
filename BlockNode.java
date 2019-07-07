import java.util.List;

/**
 * A Blocknode is a block of nodes
 * Often used as the block inside a loop
 */
public class BlockNode extends Node {
    List<RobotProgramNode> components;

    public BlockNode(List<RobotProgramNode> c) {
        components = c;
        //Check that all the nodes are actually a node
//        for(int i = components.size()-1;i>0;i--) {
//            if(components.get(i) == null) {
//                components.remove(i);
//            }
//        }
    }

    @Override
    /**
     * To String method for block, includes all the components of the block
     */
    public String toString() {
        String str = "";
        if(components == null || components.size() ==0) {
            return str;
        }
        for(RobotProgramNode n : components) {
            if(n != null) {
                str = str + " " + n.toString();
            }
        }
        return str;
    }

    @Override
    /**
     * Execute Method. Execute all the nodes in the block
     */
    public void execute(Robot r) {
        for(RobotProgramNode n : components) {
            n.execute(r);
        }
    }

}
