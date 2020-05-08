package elements;

import java.util.ArrayList;

/**
 * This class an attack tree's attack element
 * @author NAVEAU Simon
 */
public class Attack extends Element{

    /**
     * The name of the attack
     */
    public String name;

    /**
     * The list of the operators in the attack
     */
    public ArrayList<Operator> OperatorChildren;

    /**
     * The list of the subAttacks composing this attack
     */
    public ArrayList<Attack> AttackChildren;

    private Element father;

    public String id_tree;

    /**
     * Constructor of the class
     * @param name the attack name
     * @param OperatorChildren the attack sub operations
     * @param AttackChildren the attack sub attacks
     * @throws Exception
     */
    public Attack(String id_tree, String name, ArrayList OperatorChildren, ArrayList AttackChildren) throws Exception {
        this.id_tree = id_tree;
        this.name = name;
        this.OperatorChildren = OperatorChildren;
        this.AttackChildren = AttackChildren;
    }

    @Override
    public String toString() {
        return "Attack{" +
                "name='" + name + '\'' +
                ", OperatorChildren=" + OperatorChildren.size() +
                ", AttackChildren=" + AttackChildren.size() +
                '}';
    }

    public Element getFather() {
        return father;
    }

    public void setFather(Element father) {
        this.father = father;
    }
}
