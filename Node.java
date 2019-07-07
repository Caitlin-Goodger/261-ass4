/**
 * Class that all the other nodes can extend
 */
public abstract class Node implements RobotProgramNode{
    public Node() {

    }

    public abstract String toString();

    public abstract void execute(Robot r);

}
