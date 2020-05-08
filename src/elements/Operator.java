package elements;

import java.util.ArrayList;

/**
 * This class an attack tree's operator
 * @author NAVEAU Simon
 */
public class Operator extends Element {

    /**
     * The type of the operator
     */
    public OperatorType type;

    /**
     * The list of the attack in the operation
     */
    public ArrayList<Attack> children;

    private Element father;

    public String idOperator;

    public String id_tree;

    /**
     * Constructor of the class
     * @param type the operator type
     * @param children the operation's operands
     * @throws Exception
     */
    public Operator(String id_tree, String idOperator, OperatorType type, ArrayList children) throws Exception {
        this.id_tree = id_tree;
        this.idOperator = idOperator;
        this.type = type;
        this.children = children;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "type=" + type +
                ", children=" + children.size() +
                '}';
    }

    public Element getFather() {
        return father;
    }

    public void setFather(Element father) {
        this.father = father;
    }
}
