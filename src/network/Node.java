package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node implements Cloneable {

    public String nodeName;
    public final String itemName;
    public int id;
    public boolean isOn;
    public double operability;
    public double alpha;

    public ArrayList<Node> parentNodes;
    public ArrayList<Node> childrenNodes;

    public Node(String NodeName, String ItemName){
        this.nodeName = NodeName;
        this.itemName = ItemName;
        this.id = 0;

        this.parentNodes = new ArrayList<>();
        this.childrenNodes = new ArrayList<>();
    }

    public Node nextNode(){
        for (Node parent:parentNodes){
            if (parent.isOn){
                return parent;
            }
        }
        return null;
    }

    public Node nextSisterNode(ArrayList<Node> usedNodes){
        for (Node parent:parentNodes){
            if(!usedNodes.contains(parent) && parent.isOn){
                return parent;
            }
        }
        return null;
    }

    public Node clone() throws CloneNotSupportedException{
        return (Node) super.clone();
    }
}
