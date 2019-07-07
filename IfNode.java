
import java.util.ArrayList;
import java.util.List;

/**
 * Class for If Node
 * Can also manage elif and else statements
 */
public class IfNode extends Node{
    BlockNode block;
    ConditionNode condition;
    //This will store a list of any elif or else statements following this if.
    List<IfNode> ifNodes;

    public IfNode(BlockNode block, ConditionNode con) {
        this.block = block;
        condition = con;
        ifNodes = new ArrayList<>();
    }

    @Override
    //To String including the brackets
    public String toString() {
        String str = "";
        if(condition == null) {
            str = "else {\n";
        } else {
            str = "if (" + condition + ") {\n";
        }
        for(RobotProgramNode r : block.components) {
            str = str + r.toString();
        }
        if(ifNodes.size() ==0) {
            str = str + " }\n";
        } else {
            str = str +" } \n";
            for (int i = 0; i< ifNodes.size(); i++) {
                str = str + ifNodes.get(i).toString();
            }
        }
        return str;
    }

    @Override
    //Execute Method
    public void execute(Robot r) {
        if(condition != null) { //If there is a condition for the if loop
            condition.initialise(r);
            if(condition.holding()) { //Check that the condition is true
                for(RobotProgramNode n : block.components) {
                    n.execute(r);
                }
            } else { //Execute elif and else conditions
                for(IfNode n : ifNodes) {
                    for(RobotProgramNode n2 : n.block.components) {
                        n2.execute(r);
                    }
                }
            }
        } else {
            for(RobotProgramNode n : block.components) {
                n.execute(r);
            }
        }
    }
}
