import java.io.*;
public class Node implements Serializable {
    private Node leftSubNode;
    private Node rightSubNode;
    private int size;
    private String characters;

    public Node(String characters, int size, Node leftSubNode, Node rightSubNode){
        this.characters = characters;
        this.size = size;
        this.leftSubNode = leftSubNode;
        this.rightSubNode = rightSubNode;
    }

    public int getSize() {
        return size;
    }

    public String getCharacters() {
        return characters;
    }

    public Node getLeftSubNode() {
        return leftSubNode;
    }

    public Node getRightSubNode() {
        return rightSubNode;
    }
}
