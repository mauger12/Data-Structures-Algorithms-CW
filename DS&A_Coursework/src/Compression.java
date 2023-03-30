import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Compression {
    private HashMap<String, Integer> characterFrequency = new HashMap<String, Integer>();
    private PriorityQueue<Node> nodes;
    private Node tree;
    private boolean only1Char = false;
    private HashMap<String, String> characterBinary = new HashMap<String, String>();

    public Compression(){}

    public void addCharacter(String characterInt){
        if(characterInt.equals("13"))
            characterFrequency.put("nl", characterFrequency.getOrDefault("nl",0)+1);
        //if the character is a newline, save as nl
        else if(characterInt.equals("10"))
            characterFrequency.put("nls", characterFrequency.getOrDefault("nls",0)+1);
        else
            characterFrequency.put(String.valueOf((char)Integer.parseInt(characterInt)), characterFrequency.getOrDefault(String.valueOf((char)Integer.parseInt(characterInt)),0)+1);

        //if the character exists replace but with 1 higher frequency
        //else add the character with default value 0+1=1
    }

    public void generateForest() {
        nodes = new PriorityQueue<Node>(characterFrequency.size(), new NodeComparison());
        for(String character : characterFrequency.keySet()){
            nodes.add(new Node((String)character, characterFrequency.get(character),null,null));
        }
        //generate lots of single node trees - a forest - for each character in the text
        //for each character-frequency in the hashmap, add a new single node into the nodes priority queue with the same character and frequency and no sub nodes
    }

    public Node generateTree(String treeName){

        if(nodes.size() == 1){
            only1Char = true;
        }

        while(nodes.size()>1){
            Node node1 = nodes.poll();
            Node node2 = nodes.poll();
            int size = node2.getSize() + node1.getSize();
            char[] unsortedCharacters = (node2.getCharacters() + node1.getCharacters()).toCharArray();
            Arrays.sort(unsortedCharacters);
            String characters = new String(unsortedCharacters);

            Node newNode = new Node(characters, size, node2, node1);
            nodes.add(newNode);
        }
        //loop through all the trees setting them as sub nodes to a new node to go in the queue

        tree = nodes.poll();
        System.out.println("Tree generated with size " + "\"" + tree.getSize() + "\"" + " containing characters: \"" + tree.getCharacters() + "\"");
        // print final tree size and the set of characters inside to it

        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(treeName + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(tree);

            out.close();
            file.close();

            System.out.println("Tree has been saved as "+ treeName + ".ser");

        }
        catch(IOException ex) {
            System.out.println("tree not saved");
        }
        //save the tree as a .ser file

        return tree;
    }

    public void generateBinary(Node root, String code) {
        if(only1Char) {
            System.out.println(root.getCharacters() + ":" + "0");
            only1Char = false;
            return;
        }
        //if theres only 1 character in the file, set that character = 0

        if(root.getLeftSubNode() == null || root.getRightSubNode() == null){
            characterBinary.put(root.getCharacters(), code);
            return;
        }

        generateBinary(root.getLeftSubNode(), code + "1");
        generateBinary(root.getRightSubNode(), code + "0");
    }

    public String getBinary(int binaryNumber){
        if(binaryNumber == 13)
            return characterBinary.get("nl");
        else if(binaryNumber == 10)
            return characterBinary.get("nls");
        else
            return characterBinary.get(String.valueOf((char)binaryNumber));
    }

    public String getChar(String binary){
        for(Map.Entry<String, String> entry : characterBinary.entrySet()){
            if (entry.getValue().equals(binary)) {
                if(entry.getKey().equals("nl")){
                    return String.valueOf((char)13);
                }
                else if(entry.getKey().equals("nls")){
                    return String.valueOf((char)10);
                }
                else
                    return entry.getKey();
            }
        }
        return "-1";
        //if something goes wrong it returns -1, else it returns integer value of each character
    }

    public boolean isBinaryValid(String binary){
        if(characterBinary.containsValue(binary))
            return true;
        return false;
    }

    public void setTree(Node tree){
        this.tree = tree;
    }
}