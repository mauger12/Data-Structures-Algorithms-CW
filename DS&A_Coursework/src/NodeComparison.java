import java.util.Comparator;

public class NodeComparison implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.getSize()-o2.getSize();
        }
    }