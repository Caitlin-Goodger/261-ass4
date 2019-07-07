/**
 * Condition Node
 * Use for a condition of a loop
 * Can be used to evalute the condition to see if it is true
 * Doesn't extend Node as it doesn't need an execute method;
 */
public class ConditionNode {
    String operator;
    VariableNode v1;
    VariableNode v2;
    ConditionNode c1;
    ConditionNode c2;

    //Two different constructors depending on what it is given
    public ConditionNode(String operator, VariableNode v1, VariableNode v2) {
        this.operator = operator;
        this.v1 = v1;
        this.v2 = v2;
    }

    public ConditionNode(String operator, ConditionNode c1, ConditionNode c2) {
        this.operator = operator;
        this.c1 = c1;
        this.c2 = c2;
    }

    //To String Method
    public String toString() {
        if (operator.equals("lt")) {
            return v1 + " < " + v2;
        } else if (operator.equals("gt")) {
            return v1 + " > " + v2;
        } else if (operator.equals("eq")) {
            return v1 + " == " + v2;
        } else if (operator.equals("or")) {
            return c1 + " || " + c2;
        } else if (operator.equals("and")) {
            return c1 + " && " + c2;
        } else if (operator.equals("not")) {
            return "!" + c1;
        }
        return null;
    }

    /**
     * Holding checks whether the condition is true
     * @return
     */
   public boolean holding() {
        if(v1 != null) {
            if (v1.isString()) {
                return v1.getValue().equals(v2.getValue());
            }
            //If there is a operator check that the values make the operator true
            if (this.operator.equals("lt")) {
                return (int) (v1.getValue()) < (int) (v2.getValue());
            } else if (this.operator.equals("gt")) {
                return (int) (v1.getValue()) > (int) (v2.getValue());
            } else if (this.operator.equals("eq")) {
                return (int) (v1.getValue()) == (int) (v2.getValue());
            }
            return false;
        } else {
            if(c2 != null) {
                if(operator.equals("and")) {
                    return c1.holding() && c2.holding();
                } else if (operator.equals("or")) {
                    return c1.holding() || c2.holding();
                }
            } else {
                return !c1.holding();
            }
        }
       return !c1.holding();
   }

    /**
     * Initialise sets the variable nodes to the correct value based on what is in there
     * @param r
     */
   public void initialise(Robot r) {
        if(v1 != null && v2 != null) {
            if(v1.robotVariable) {
                if(v1.name.equals("fuelLeft")) {
                    v1.setValue(r.getFuel());
                } else if(v1.name.equals("oppLR")) {
                    v1.setValue(r.getOpponentLR());
                }  else if(v1.name.equals("oppFB")) {
                    v1.setValue(r.getOpponentFB());
                }  else if(v1.name.equals("numBarrels")) {
                    v1.setValue(r.numBarrels());
                } else if(v1.name.equals("barrelLR")) {
                    v1.setValue(r.getClosestBarrelLR());
                }  else if(v1.name.equals("barrelFB")) {
                    v1.setValue(r.getClosestBarrelFB());
                }  else if(v1.name.equals("wallDist")) {
                    v1.setValue(r.getDistanceToWall());
                }
            }
            if(v2.robotVariable) {
                if(v2.name.equals("fuelLeft")) {
                    v2.setValue(r.getFuel());
                } else if(v2.name.equals("oppLR")) {
                    v2.setValue(r.getOpponentLR());
                } else if(v2.name.equals("oppFB")) {
                    v2.setValue(r.getOpponentFB());
                } else if(v2.name.equals("numBarrels")) {
                    v2.setValue(r.numBarrels());
                } else if(v2.name.equals("barrelLR")) {
                    v2.setValue(r.getClosestBarrelLR());
                } else if(v2.name.equals("barrelFB")) {
                    v2.setValue(r.getClosestBarrelFB());
                } else if(v2.name.equals("wallDist")) {
                    v2.setValue(r.getDistanceToWall());
                }
            }

        } else {
            c1.initialise(r);
            if(c2 != null) {
                c2.initialise(r);
            }
        }
   }
}

