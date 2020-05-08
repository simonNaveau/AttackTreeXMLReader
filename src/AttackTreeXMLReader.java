import elements.Attack;
import elements.Operator;
import elements.OperatorType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class was made to parse Attack trees stored in XML files. This file need <operator type=...></operator> and <attack name=...></attack> tags.
 *
 * @author Naveau Simon
 */
public class AttackTreeXMLReader {

    private DocumentBuilder builder;

    /**
     * The root element of the XML once object created
     */
    private Element root;

    /**
     * The root element of the attack tree once created with the convertTree() method called
     */
    public Attack attackTree;


    /**
     * Contructor of the class, open the XML file and create the root element
     *
     * @param file the path to the xml attack tree
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public AttackTreeXMLReader(String file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(file));
        root = document.getDocumentElement();
    }

    /**
     * Create the modeling of the attack tree from the XML file loaded at the creation of the parser
     *
     * @return the root Attack element
     * @throws Exception
     */
    public Attack convertTree() throws Exception {
        String id_tree = UUID.randomUUID().toString();

        NodeList list = root.getChildNodes();
        ArrayList<ArrayList<elements.Element>> array;
        ArrayList<Operator> operators = new ArrayList<>();
        ArrayList<Attack> attacks = new ArrayList<>();
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element tmpE = (Element) node;
                if (node.getNodeName().equals("operator")) {
                    ArrayList<Attack> listAttack = new ArrayList<>();
                    array = this.getChildren(node, id_tree);
                    for (elements.Element ae : array.get(1)) {
                        if (ae instanceof Attack) {
                            listAttack.add((Attack) ae);
                        }
                    }
//                    System.out.println("Operator: "+tmpE.getAttribute("type")+" / "+array.get(1).size()+ "attacks");
                    Operator op = new Operator(id_tree, UUID.randomUUID().toString(), getTypeFromString(tmpE.getAttribute("type")), array.get(1));
                    for (Attack a : listAttack) {
                        a.setFather(op);
                    }
                    operators.add(op);
                } else if (node.getNodeName().equals("attack")) {
                    array = this.getChildren(node, id_tree);
                    ArrayList<Operator> listOperator = new ArrayList<>();
                    for (elements.Element ae : array.get(0)) {
                        if (ae instanceof Operator) {
                            listOperator.add((Operator) ae);
                        }
                    }
                    ArrayList<Attack> listAttack = new ArrayList<>();
                    for (elements.Element ae : array.get(1)) {
                        if (ae instanceof Attack) {
                            listAttack.add((Attack) ae);
                        }
                    }
//                    System.out.println("Attack: "+tmpE.getAttribute("name")+" / "+array.get(0).size()+ "operators and "+array.get(1).size()+" attacks");
                    Attack at = new Attack(id_tree, tmpE.getAttribute("name"), array.get(0), array.get(1));
                    for (Operator o : listOperator) {
                        at.setFather(o);
                    }
                    for (Attack a : listAttack) {
                        at.setFather(a);
                    }
                    attacks.add(at);
                }
            }
        }
        this.attackTree = new Attack(id_tree, root.getAttribute("name"), operators, attacks);
        return this.attackTree;
    }

    /**
     * Get all the children of a given node
     *
     * @param n the node containing the children to get
     * @return An arraylist of 2 arraylist, ret[0] == operator children / ret[1] == attack children
     * @throws Exception
     */
    private ArrayList<ArrayList<elements.Element>> getChildren(Node n, String id_tree) throws Exception {
        NodeList list = n.getChildNodes();
        ArrayList<ArrayList<elements.Element>> array;
        ArrayList<elements.Element> operators = new ArrayList<>();
        ArrayList<elements.Element> attacks = new ArrayList<>();
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            array = this.getChildren(node, id_tree);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element tmpE = (Element) node;
                if (node.getNodeName().equals("operator")) {
//                    System.out.println("Operator: "+tmpE.getAttribute("type")+" / "+array.get(1).size()+ "attacks");
                    ArrayList<Attack> listAttack = new ArrayList<>();

                    for (elements.Element ae : array.get(1)) {
                        if (ae instanceof Attack) {
                            listAttack.add((Attack) ae);
                        }
                    }
                    Operator op = new Operator(id_tree, UUID.randomUUID().toString(), getTypeFromString(tmpE.getAttribute("type")), array.get(1));
                    for (Attack a : listAttack) {
                        a.setFather(op);
                    }
                    operators.add(op);
                } else if (node.getNodeName().equals("attack")) {
//                    System.out.println("Attack: "+tmpE.getAttribute("name")+" / "+array.get(0).size()+ "operators and "+array.get(1).size()+" attacks");
                    ArrayList<Operator> listOperator = new ArrayList<>();
                    for (elements.Element ae : array.get(0)) {
                        if (ae instanceof Operator) {
                            listOperator.add((Operator) ae);
                        }
                    }
                    ArrayList<Attack> listAttack = new ArrayList<>();
                    for (elements.Element ae : array.get(1)) {
                        if (ae instanceof Attack) {
                            listAttack.add((Attack) ae);
                        }
                    }
                    Attack at = new Attack(id_tree, tmpE.getAttribute("name"), array.get(0), array.get(1));
                    for (Attack a : listAttack) {
                        a.setFather(at);
                    }
                    for (Operator o : listOperator) {
                        o.setFather(at);
                    }
                    attacks.add(at);
                }
            }
        }
        ArrayList<ArrayList<elements.Element>> ret = new ArrayList<>();
        ret.add(operators);
        ret.add(attacks);
        return ret;
    }

    /**
     * Scan the XML file and print it on the console with out creating the model
     */
    public void printTreeFromXML() {
        System.out.println("<============== PRINTING FROM THE XML FILE DIRECTLY ==============>");
        NodeList list = root.getChildNodes();
        String ecart = "";
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(ecart + node.getNodeName());
                printChildFromXML(node, ecart);
            }
        }
        System.out.println("<============================== END ==============================> :");
    }

    /**
     * Print the children of a given node in the XML file
     *
     * @param n       the node to print children's
     * @param spacing the spacing to put in front of this node
     */
    private void printChildFromXML(Node n, String spacing) {
        spacing = "\t" + spacing;
        NodeList list = n.getChildNodes();
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(spacing + node.getNodeName());
                printChildFromXML(node, spacing);
            }
        }
    }

    /**
     * Print an element and all is children
     *
     * @param element the element to print
     * @param spacing the spacing to put in front of this element
     */
    public void printFromModel(elements.Element element, String spacing) {
        if (element.getClass() == Attack.class) {
            System.out.println(spacing + ((Attack) element).name);
            ((Attack) element).OperatorChildren.forEach(val -> this.printFromModel(val, "\t" + spacing));
            ((Attack) element).AttackChildren.forEach(val -> this.printFromModel(val, "\t" + spacing));
        } else {
            System.out.println(spacing + ((Operator) element).type);
            ((Operator) element).children.forEach(val -> this.printFromModel(val, "\t" + spacing));
        }
    }

    /**
     * Get the TYPE from the OperatorType enumeration corresponding to the string given in parameter
     *
     * @param type the string corresponding to a OperatorType
     * @return the requested OperatorType
     * @throws Exception Throw an exception if the operator is not in the OperatorType enumeration
     */
    private static OperatorType getTypeFromString(String type) throws Exception {
        switch (type) {
            case "OR":
                return OperatorType.OR;
            case "AND":
                return OperatorType.AND;
            case "NOR":
                return OperatorType.NOR;
            case "XOR":
                return OperatorType.XOR;
            default:
                throw new Exception("This type " + type + " is not referenced");
        }
    }

    /**
     * From an attack create the XML attackTree associated
     *
     * @param attack the root attack of the tree to put in the XML file
     * @param path   the path to save the XML
     * @throws TransformerException
     */
    public void createXMLFromAttack(Attack attack, String path) throws TransformerException {
        Document document = builder.newDocument();

        //Creating the root element
        Element root = document.createElement("attack");
        root.setAttribute("name", attack.name);
        document.appendChild(root);

        attack.AttackChildren.forEach(attackChild -> {
            Element at = document.createElement("attack");
            at.setAttribute("name", attackChild.name);
            root.appendChild(at);
            attackChild.OperatorChildren.forEach(val -> this.addChildToXML(val, at, document));
            attackChild.AttackChildren.forEach(val -> this.addChildToXML(val, at, document));
        });

        attack.OperatorChildren.forEach(operatorChild -> {
            Element operator = document.createElement("operator");
            operator.setAttribute("type", "" + operatorChild.type);
            root.appendChild(operator);
            operatorChild.children.forEach(val -> this.addChildToXML(val, operator, document));
        });

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult sortie = new StreamResult(new File(path));
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(source, sortie);


    }

    /**
     * Add the children of an element as child to him recursively to create an attack Tree as XML
     *
     * @param element the element to add
     * @param father  the father of the element
     * @param doc     the XML document builder
     */
    private void addChildToXML(elements.Element element, Element father, Document doc) {
        if (element.getClass() == Attack.class) {
            Element attack = doc.createElement("attack");
            attack.setAttribute("name", ((Attack) element).name);
            father.appendChild(attack);
            ((Attack) element).OperatorChildren.forEach(val -> this.addChildToXML(val, attack, doc));
            ((Attack) element).AttackChildren.forEach(val -> this.addChildToXML(val, attack, doc));
        } else {
            Element operator = doc.createElement("operator");
            operator.setAttribute("type", "" + ((Operator) element).type);
            father.appendChild(operator);
            ((Operator) element).children.forEach(val -> this.addChildToXML(val, operator, doc));
        }
    }

    /**
     * Make the fusion in one tree of elements comming from different one.
     * @param original the list of AbstractElements
     * @throws Exception
     */
    public void treeFusion(List<elements.Element> original) throws Exception {
        Attack theRoot = findReceivingTree(original);
        String receivingId = theRoot.name;
        if (receivingId == null) throw new Exception("Error in finding the receiving tree TREEFUNCTION Method");

        for (elements.Element element1 : original) {
            if (element1.getClass() == Attack.class) {
                for (elements.Element element2 : original) {
                    if (element2.getClass() == Attack.class) {
                        if (((Attack) element1).name.equals(((Attack) element2).name) && element1 != element2) {
                            if (getChildrenNumberAttack((Attack) element1) >= getChildrenNumberAttack((Attack) element2)) {
                                if (!(((Attack) element1).id_tree.equals(receivingId))) {
                                    ((Attack) element1).id_tree = receivingId;
                                    ((Attack) element1).setFather(((Attack) element2).getFather());
                                    //TODO changer l'id_tree de tous les enfants du père de element1 (en soit pas important si on est pas sensé remanipuler l'arbre)
                                    if (((Attack) element1).getFather().getClass() == Attack.class) {
                                        Attack father = (Attack) ((Attack) element1).getFather();
                                        father.AttackChildren.remove(element2);
                                        father.AttackChildren.add((Attack) element1);
                                    } else {
                                        Operator father = (Operator) ((Attack) element1).getFather();
                                        father.children.remove(element2);
                                        father.children.add((Attack) element1);
                                    }
                                }
                            } else {
                                if (!(((Attack) element2).id_tree.equals(receivingId))) {
                                    ((Attack) element2).id_tree = receivingId;
                                    ((Attack) element2).setFather(((Attack) element1).getFather());
                                    //TODO changer l'id_tree de tous les enfants du père de element2 (en soit pas important si on est pas sensé remanipuler l'arbre)
                                    if (((Attack) element2).getFather().getClass() == Attack.class) {
                                        Attack father = (Attack) ((Attack) element2).getFather();
                                        father.AttackChildren.remove(element1);
                                        father.AttackChildren.add((Attack) element2);
                                    } else {
                                        Operator father = (Operator) ((Attack) element2).getFather();
                                        father.children.remove(element1);
                                        father.children.add((Attack) element2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Find the id_tree corresponding of the receiving tree in a list of AbstractElement
     *
     * @param original the list of AbstractElement
     * @return the root of the tree receiving new elements
     */
    public Attack findReceivingTree(List<elements.Element> original) {
        for (elements.Element element1 : original) {
            if (element1.getClass() == Attack.class && (((Attack) element1).getFather() == null)) {
                boolean test = true;
                for (elements.Element element2 : original) {
                    if (element2.getClass() == Attack.class) {
                        if (element1 != element2) {
                            if (((Attack) element1).name.equals(((Attack) element2).name)) {
                                test = false;
                            }
                        }
                    }
                }
                if (test) return ((Attack) element1);
            }
        }
        return null;
    }

    /**
     * Get the number of item composing an attack
     *
     * @param attack the attack to analyse
     * @return the number of children of the attack
     */
    public int getChildrenNumberAttack(Attack attack) {
        int ret = 0;
        for (Attack attackChild : attack.AttackChildren) {
            ret++;
            ret = ret + this.getChildrenNumberAttack(attackChild);
        }
        for (Operator operatorChild : attack.OperatorChildren) {
            ret++;
            ret = ret + this.getChildrenNumberOperator(operatorChild);
        }
        return ret;
    }

    /**
     * Get the number of item composing an operator
     *
     * @param operator the operator to analyse
     * @return the number of children of the operator
     */
    public int getChildrenNumberOperator(Operator operator) {
        int ret = 0;
        for (Attack attackChild : operator.children) {
            ret++;
            ret = ret + this.getChildrenNumberAttack(attackChild);
        }
        return ret;
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////JUSTE POUR LES TEST
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the number of item composing an attack
     *
     * @param attack the attack to analyse
     * @return the number of children of the attack
     */
    public List<elements.Element> treeToList(Attack attack) {
        ArrayList<elements.Element> ret = new ArrayList<>();
        ret.add(attack);
        for (Attack attackChild : attack.AttackChildren) {
            ret.addAll(this.treeToList(attackChild));
        }
        for (Operator operatorChild : attack.OperatorChildren) {
            ret.addAll(this.treeToList2(operatorChild));
        }
        return ret;
    }

    /**
     * Get the number of item composing an operator
     *
     * @param operator the operator to analyse
     * @return the number of children of the operator
     */
    public List<elements.Element> treeToList2(Operator operator) {
        ArrayList<elements.Element> ret = new ArrayList<>();
        ret.add(operator);
        for (Attack attackChild : operator.children) {
            ret.addAll(this.treeToList(attackChild));
        }
        return ret;
    }
}
