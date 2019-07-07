/**
 * Node for the While Node
 */
public class WhileNode extends Node {
    BlockNode block;
    ConditionNode condition;

    public WhileNode(BlockNode b, ConditionNode c) {
        block = b; //Block of nodes in the while loop
        condition = c; //Condition on the while loop
    }

    @Override
    public String toString() {
        String str = "while (" + this.condition + ") { \n";
        for(RobotProgramNode n : block.components) {
            str = str = n.toString();
        }
        str = str + "}\n";
        return str;
    }

    @Override
    public void execute(Robot r) {
        condition.initialise(r);
        while(condition.holding()) { //Keep checking that the condition is true
            condition.initialise(r);
            for(RobotProgramNode n : block.components) {
                n.execute(r);
            }
        }
    }
}
