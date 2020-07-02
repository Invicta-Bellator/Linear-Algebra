package network;

import java.util.ArrayList;

public class Network {

    public ArrayList<Node> nodes;
    public int startNodeIndex;
    public int goalNodeIndex;

    public Network(ArrayList<Node> nodes, int startNodeIndex, int goalNodeIndex){
        this.nodes = nodes;
        this.startNodeIndex = startNodeIndex;
        this.goalNodeIndex = goalNodeIndex;
    }

    public boolean nodesConnectToGoal(){

        ArrayList<Node> usedNodes = new ArrayList<>();
        Node startNode = this.nodes.get(startNodeIndex);

        if(startNode.isOn) {
            for (Node node : startNode.parentNodes) {
                if(node.isOn) {
                    if (node.itemName.equals(this.nodes.get(goalNodeIndex).itemName)) {
                        return true;
                    }
                    while (true) {
                        Node node2 = node;
                        node = node.nextNode();
                        if (node == null) {
                            node = node2.nextSisterNode(usedNodes);
                            if (node == null) {
                                break;
                            } else {
                                usedNodes.add(node);
                            }
                        } else {
                            usedNodes.add(node);
                        }
                        if (node.itemName.equals(this.nodes.get(goalNodeIndex).itemName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
