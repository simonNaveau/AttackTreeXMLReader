import elements.Attack;
import elements.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test {
    public static void main(String[] args) throws Exception {
        AttackTreeXMLReader reader = new AttackTreeXMLReader("res/basicTest.xml");
        AttackTreeXMLReader readerComplement = new AttackTreeXMLReader("res/basicTestComplement.xml");
        reader.convertTree();
        readerComplement.convertTree();
        //System.out.println("\n\n\n<====================== PRINTING FROM MODEL ======================>");
        //reader.printFromModel(reader.attackTree, "");
        //System.out.println("<============================== END ==============================> :");
        Attack fake = new Attack(UUID.randomUUID().toString(), "prout", new ArrayList(), new ArrayList());

        List<Element> tmp2 = readerComplement.treeToList(readerComplement.attackTree);
        Attack test = (Attack) tmp2.get(0);
        test.setFather(fake);
        List<Element> tmp = reader.treeToList(reader.attackTree);
        tmp.addAll(tmp2);





        reader.treeFusion(tmp);







        reader.createXMLFromAttack(reader.findReceivingTree(tmp), "./result.xml");
        System.out.println("Nombre d'element = "+reader.getChildrenNumberAttack(reader.attackTree));




//
//        System.out.println(fake.toString());
//        tmp.add(fake);
//        System.out.println(reader.findReceivingTree(tmp));
    }
}
