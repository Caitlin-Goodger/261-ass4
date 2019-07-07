public class VariableNode extends Node{
    String name;
    String stringValue;
    String intValue; //It is a string so that it is easy to tell if it is null
    boolean robotVariable = false;
    String operator;
    VariableNode v1;
    VariableNode v2;

    //Multiple Constructors depending on what it is given
    public VariableNode(String name, int value, boolean robotVariable) {
        this.name = name;
        intValue = value + "";
        this.robotVariable = robotVariable;
    }

    public VariableNode(String operator, VariableNode v1, VariableNode v2) {
        this.operator = operator;
        this.v1 = v1;
        this.v2 = v2;
    }

    public VariableNode(String name, VariableNode v1, boolean robotVariable) {
        this.name = name;
        this.v1 = v1;
        this.robotVariable = robotVariable;
    }


    public String toString() {
        if(v2 != null) {
            if(operator.equals("add")) {
                return "(" + v1.toString() + " + " + v2.toString() + ")";
            } else if(operator.equals("sub")) {
                return "(" + v1.toString() + " - " + v2.toString() + ")";
            } else if(operator.equals("mul")) {
                return "(" + v1.toString() + " * " + v2.toString() + ")";
            } else if(operator.equals("div")) {
                return "(" + v1.toString() + " + " + v2.toString() + ")";
            }
        }
        return name;
    }
    //Check if the value given is a string
    public boolean isString() {
        return stringValue != null;
    }

    //Get the value of the variable
    public Object getValue() {
        if(v1 != null && v2 != null) {
            return evaluate();
        }
        if(stringValue == null && intValue != null) {
            return Integer.parseInt(intValue);
        } else if(stringValue != null && intValue == null) {
            return stringValue;
        } else if (v1 != null && v2 == null) {
            return v1.getValue();
        }
        return null;
    }

    //Set int value. Has to be converted to a string
    public void setValue(int i) {
        intValue = Integer.toString(i);
    }

    //Evalute the equation, eg add the two numbers if it is add
    public int evaluate() {
        if(operator.equals("add")) {
            return (int) (v1.getValue()) + (int) v2.getValue();
        } else if(operator.equals("sub")) {
            return (int) (v1.getValue()) - (int) v2.getValue();
        } else if(operator.equals("mul")) {
            return (int) (v1.getValue()) * (int) v2.getValue();
        } else if(operator.equals("div")) {
            return (int) (v1.getValue()) / (int) v2.getValue();
        } else {
            return 0;
        }
    }

    @Override
    public void execute(Robot r) {
        if(v1.operator != null) {
            System.out.println(v1.v1.v1 = new VariableNode(v1.v1.v1.name,v1.evaluate(),true));
        }
    }
}
