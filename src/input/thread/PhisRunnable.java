package input.thread;

import input.StandardInput;
import main.Main;
import network.Network;
import network.Node;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class PhisRunnable implements Runnable {

    private final BigInteger start;
    private final BigInteger end;
    private final ArrayList<Node> nodes;
    private final int startNodeIndex;
    private final int goalNodeIndex;
    private final StandardInput input;

    public PhisRunnable(BigInteger start, BigInteger end, StandardInput input) throws CloneNotSupportedException {
        this.start = start;
        this.end = end;
        this.nodes = new ArrayList<>();

        for(Node node : input.getNodes()){
            this.nodes.add(node.clone());
        }

        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).parentNodes = new ArrayList<>();
            nodes.get(i).childrenNodes = new ArrayList<>();
            for(int a = 0; a < input.getNodes().get(i).childrenNodes.size(); a++){
                nodes.get(i).childrenNodes.add(nodes.get(input.getNodes().indexOf(input.getNodes().get(i).childrenNodes.get(a))));
            }
            for(int a = 0; a < input.getNodes().get(i).parentNodes.size(); a++){
                nodes.get(i).parentNodes.add(nodes.get(input.getNodes().indexOf(input.getNodes().get(i).parentNodes.get(a))));
            }
        }

        this.startNodeIndex = input.getStartNodeIndex();
        this.goalNodeIndex = input.getGoalNodeIndex();
        this.input = input;
    }

    @Override
    public void run() {

        double doubleMax = Double.MAX_VALUE;
        BigInteger index = start;
        BigInteger possibilities = BigInteger.ZERO;

        double phisSum = 0;
        double[] positivePhisCounts = new double[nodes.size()];
        double[] negativePhisCounts = new double[nodes.size()];
        long[] divisionCounts = new long[nodes.size()];
        long phiSumDivisionCounts = 0;

        Instant start = Instant.now();
        Network network = new Network(new ArrayList<>(nodes), startNodeIndex, goalNodeIndex);

        while(index.compareTo(end) < 0){

            String binary = String.format("%" + nodes.size() + "s", index.toString(2)).replace(' ', '0');

            for(int a = 0; a < nodes.size(); a++) {
                //IF BINARY LETTER IS '1' THEN THE NODE IS ON
                nodes.get(a).isOn = binary.charAt(a) == '1';
            }

            boolean connects = network.nodesConnectToGoal();

            if(connects){
                if(phisSum == doubleMax - 1){
                    phisSum /= 2;
                    phiSumDivisionCounts++;
                }
                phisSum++;
            }

            for(int a = 0; a < binary.length(); a++){
                if(positivePhisCounts[a] == doubleMax - 1 || negativePhisCounts[a] == doubleMax - 1){
                    positivePhisCounts[a] /= 2;
                    negativePhisCounts[a] /= 2;
                    divisionCounts[a]++;
                }

                int add = 0;
                if(connects){
                    add = 1;
                }

                if(binary.charAt(a) == '1'){
                    positivePhisCounts[a] += add;
                }else if(binary.charAt(a) == '0'){
                    negativePhisCounts[a] += add;
                }
            }
            index = index.add(BigInteger.ONE);
            possibilities = possibilities.add(BigInteger.ONE);

            if(Duration.between(start, Instant.now()).toMinutes() == Main.maxNumberOfMinutes){
                break;
            }
        }
        input.incrementPossibilitiesCount(possibilities);

        input.incrementPhisSum(phisSum);
        input.incrementPhiSumDivisionCount(phiSumDivisionCounts);

        input.incrementPositivePhisCounts(positivePhisCounts);
        input.incrementNegativePhisCounts(negativePhisCounts);
        input.incrementDivisionCounts(divisionCounts);
    }
}
