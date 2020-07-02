package input;

import input.thread.PhisRunnable;
import input.thread.PhisRunnableRandom;
import main.Main;
import network.Node;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StandardInput {

    private final String path;
    private int startNodeIndex = -1;
    private int goalNodeIndex = -1;

    private final XSSFSheet linksSheet;
    private final XSSFSheet dependenciesSheet;
    private final FileInputStream fis;
    private final XSSFWorkbook workbook;

    private BigInteger possibilitiesCount = new BigInteger("0");

    private ArrayList<Node> nodes;
    private double[] A;
    private double[] B;
    private int N;
    private double[] positivePhisCounts;
    private double[] negativePhisCounts;
    private double phisSum;
    private long[] divisionCounts;
    private long phiSumDivisionCounts = 0;
    private boolean missingDependencies = false;

    public StandardInput(String path) throws IOException {
        XSSFSheet dependenciesSheet1;
        this.path = path;
        this.fis = new FileInputStream(path);
        workbook = new XSSFWorkbook(fis);
        linksSheet = workbook.getSheet("Links");
        dependenciesSheet1 = workbook.getSheet("Dependencies");

        if(dependenciesSheet1 == null){
            Scanner in = new Scanner(System.in);
            System.out.println("The Dependencies sheet is missing. Would you like to manually choose a case?");
            System.out.println("Answer yes or no.");

            if(in.nextLine().equals("yes")){
                System.out.println("Which case would you like to run?");
                System.out.println("1. A = 1, B = -0.5");
                System.out.println("2. A = 1, B = 0");
                System.out.println("3. A = 1, B = -1");
                System.out.println("Answer 1, 2, or 3");
                missingDependencies = true;

                String input = in.nextLine();

                if(this.nodes == null){
                    this.nodes = readNodes();
                }

                this.A = new double[nodes.size()];
                this.B = new double[nodes.size()];

                switch (input) {
                    case "1":
                        for (int i = 0; i < nodes.size(); i++) {
                            A[i] = 1;
                            B[i] = -.5;
                        }
                        break;
                    case "2":
                        for (int i = 0; i < nodes.size(); i++) {
                            A[i] = 1;
                            B[i] = 0;
                        }
                        break;
                    case "3":
                        for (int i = 0; i < nodes.size(); i++) {
                            A[i] = 1;
                            B[i] = -1;
                        }
                        break;
                }
            }
        }
        dependenciesSheet = dependenciesSheet1;
    }

    public void process() throws InterruptedException, CloneNotSupportedException {
        if(this.nodes == null){
            this.nodes = readNodes();
        }

        this.N = this.nodes.size();
        if(!missingDependencies){
            this.A = getVector("a");
            this.B = getVector("b");
        }

        this.positivePhisCounts = new double[nodes.size()];
        this.negativePhisCounts = new double[nodes.size()];
        this.divisionCounts = new long[nodes.size()];

        getPhisCounts();
    }

    public ArrayList<Node> getNodes(){return nodes;}

    public String[] getNodeNames(){

        if(nodes == null){
            this.nodes = readNodes();
        }

        String[] names = new String[nodes.size()];

        for(int i = 0; i < nodes.size(); i++){
            names[i] = nodes.get(i).itemName;
        }

        return names;
    }

    public double[] getA() {
        return A;
    }

    public double[] getB() {
        return B;
    }

    public int getN() {
        return N;
    }

    public String getPath() {
        return path;
    }

    public int getStartNodeIndex() {
        return startNodeIndex;
    }

    public int getGoalNodeIndex() {
        return goalNodeIndex;
    }

    public XSSFSheet getLinksSheet() {
        return linksSheet;
    }

    public FileInputStream getFis() {
        return fis;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public synchronized double[] getPositivePhisCounts() {
        return positivePhisCounts;
    }

    public synchronized double[] getNegativePhisCounts() {
        return negativePhisCounts;
    }

    public synchronized double getPhisSum(){return phisSum;}

    public synchronized long[] getDivisionCounts() {
        return divisionCounts;
    }

    public synchronized long getPhiSumDivisionCounts(){return phiSumDivisionCounts;}

    public synchronized void incrementPossibilitiesCount(BigInteger number){
        possibilitiesCount = possibilitiesCount.add(number);
    }

    public synchronized  void incrementPhisSum(double number){
        phisSum += number;
    }

    public synchronized void incrementPhiSumDivisionCount(long number){
        phiSumDivisionCounts += number;
    }

    public synchronized void incrementPositivePhisCounts(double[] number) {
        for(int i = 0; i < positivePhisCounts.length; i++){
            positivePhisCounts[i] += number[i];
        }
    }

    public synchronized void incrementNegativePhisCounts(double[] number) {
        for(int i = 0; i < negativePhisCounts.length; i++){
            negativePhisCounts[i] += number[i];
        }
    }

    public synchronized void incrementDivisionCounts(long[] number) {
        for(int i = 0; i < divisionCounts.length; i++){
            divisionCounts[i] += number[i];
        }
    }

    private ArrayList<Node> readNodes(){

        Instant startReadingNodes = Instant.now();

        ArrayList<Node> nodes = new ArrayList<>();

        for (int i = 0; i < linksSheet.getLastRowNum(); i++) {
            XSSFRow row = linksSheet.getRow(i + 1);
            XSSFCell columnB = row.getCell(1);
            XSSFCell columnC = row.getCell(2);
            XSSFCell columnD = row.getCell(3);
            XSSFCell columnE = row.getCell(4);

            int parentNodeIndex = 0;
            int childNodeIndex = 0;

            boolean preExistentParentNode = false;
            boolean preExistentChildNode = false;

            for (int a = 0; a < nodes.size(); a++) {
                if (nodes.get(a).itemName.equals(columnB.toString())) {
                    preExistentParentNode = true;
                    parentNodeIndex = a;
                }
                if (nodes.get(a).itemName.equals(columnD.toString())) {
                    preExistentChildNode = true;
                    childNodeIndex = a;
                }
            }

            if (!preExistentParentNode) {
                Node node = new Node("N" + columnC.toString().substring(0, columnC.toString().length() - 2), columnB.toString());
                nodes.add(node);
                parentNodeIndex = nodes.size() - 1;
                if (columnB.toString().toLowerCase().equals("start")) {
                    startNodeIndex = nodes.indexOf(node);
                }else if(columnB.toString().toLowerCase().equals("goal")){
                    goalNodeIndex = nodes.indexOf(node);
                }
            }
            if (!preExistentChildNode) {
                Node node = new Node("N" + columnE.toString().substring(0, columnE.toString().length() - 2), columnD.toString());
                nodes.add(node);
                childNodeIndex = nodes.size() - 1;
                if (columnD.toString().toLowerCase().equals("start")) {
                    startNodeIndex = nodes.indexOf(node);
                }else if(columnD.toString().toLowerCase().equals("goal")){
                    goalNodeIndex = nodes.indexOf(node);
                }
            }

            nodes.get(parentNodeIndex).childrenNodes.add(nodes.get(childNodeIndex));
            nodes.get(childNodeIndex).parentNodes.add(nodes.get(parentNodeIndex));
        }
        //endregion

        ArrayList<Node> nodesWithoutParents = new ArrayList<>();
        ArrayList<Node> nodesWithoutChildren = new ArrayList<>();

        for(Node node:nodes){
            if(node.parentNodes.size() == 0){
                nodesWithoutParents.add(node);
            }
            if(node.childrenNodes.size() == 0){
                nodesWithoutChildren.add(node);
            }
        }

        //region If not present then create Start Node
        if (startNodeIndex == -1) {
            if(nodesWithoutChildren.size() > 1){
                Node startNode = new Node("NSTART", "START");
                for(Node node : nodesWithoutChildren){
                    startNode.childrenNodes.add(node);
                    node.parentNodes.add(startNode);
                }
                nodes.add(startNode);
                startNodeIndex = nodes.indexOf(startNode);
            }else if(nodesWithoutChildren.size() == 1){
                startNodeIndex = nodes.indexOf(nodesWithoutChildren.get(0));
            }
        }
        //endregion

        //region If not present then create Start Node
        if (goalNodeIndex == -1) {
            if(nodesWithoutParents.size() > 1){
                Node goalNode = new Node("NGOAL", "GOAL");
                for(Node node : nodesWithoutParents){
                    goalNode.childrenNodes.add(node);
                    node.parentNodes.add(goalNode);
                }
                nodes.add(goalNode);
                goalNodeIndex = nodes.indexOf(goalNode);
            }else if(nodesWithoutParents.size() == 1){
                goalNodeIndex = nodes.indexOf(nodesWithoutParents.get(0));
            }
        }

        Instant endedReadingNodes = Instant.now();
        //endregion
        if(Main.debugging){
            System.out.println(Duration.between(startReadingNodes, endedReadingNodes).toMillis() + "ms read all the Nodes");
        }
        return nodes;
    }

    private void getPhisCounts() throws InterruptedException, CloneNotSupportedException {
        Instant startGettingPhis = Instant.now();

        if(nodes == null){
            this.nodes = readNodes();
        }

        //Find total minutes
        System.out.println("How many minutes max do you want the program to spend calculating the probabilities of nodes?");
        System.out.println("Only type a whole number.");

        boolean hasMaxMinutes = false;
        while (!hasMaxMinutes){
            try {
                Main.maxNumberOfMinutes = Integer.parseInt(Main.in.nextLine());
                hasMaxMinutes = true;
            }catch (Exception a){
                System.out.println(Main.in.nextLine() + "That is a not whole number please try again");
            }
        }

        BigInteger poss = powerN(new BigInteger("2"), nodes.size());

        //Get how many threads to use
        System.out.println("How many threads do you want to use? NOTE ** threads must be less than " + poss.toString());
        System.out.println("Answer with a whole number");
        boolean hasThreads = false;
        while (!hasThreads) {
            try{
                Main.numberOfThreads = Integer.parseInt(Main.in.nextLine());
                hasThreads = true;
            }catch (Exception a){
                System.out.println("That is not a whole number please try again");
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(Main.numberOfThreads);

        BigInteger possibilitiesPerThread = poss.divide(BigInteger.valueOf(Main.numberOfThreads));
        BigInteger start = BigInteger.ZERO;
        BigInteger end = possibilitiesPerThread;
        BigInteger additionalPoss = BigInteger.ZERO;

        if(!possibilitiesPerThread.multiply(BigInteger.valueOf(Main.numberOfThreads)).equals(poss)){
            additionalPoss = poss.min(BigInteger.valueOf(Main.numberOfThreads));
        }

        if(Main.randomPossibilitySelection) {
            PhisRunnableRandom runnable;
            for(int i = 0; i < Main.numberOfThreads; i++) {
                if (i == Main.numberOfThreads - 1) {
                    runnable = new PhisRunnableRandom(start, end.add(additionalPoss), this);
                } else {
                    runnable = new PhisRunnableRandom(start, end, this);
                }
                executor.execute(runnable);
                start = start.add(possibilitiesPerThread);
                end = end.add(possibilitiesPerThread);
            }
        }else {
            PhisRunnable runnable;
            for(int i = 0; i < Main.numberOfThreads; i++){
                if (i == Main.numberOfThreads - 1) {
                    runnable = new PhisRunnable(start, end.add(additionalPoss), this);
                } else {
                    runnable = new PhisRunnable(start, end, this);
                }
                executor.execute(runnable);
                start = start.add(possibilitiesPerThread);
                end = end.add(possibilitiesPerThread);
            }
        }

        executor.shutdown();
        executor.awaitTermination(999999, TimeUnit.HOURS);

        Instant finishedGettingPhis = Instant.now();
        if(Main.debugging){
            System.out.println(Duration.between(startGettingPhis, finishedGettingPhis).toMinutes() + "m calculated the Nodes possibility table");
        }

        System.out.println("Calculated " + possibilitiesCount + " / " + poss + " Possibilities");

        BigInteger totalTime = (((poss.divide(possibilitiesCount)).divide(BigInteger.valueOf(Main.maxNumberOfMinutes))).divide(BigInteger.valueOf(60))).divide(BigInteger.valueOf(24));

        System.out.println("It would take " + totalTime + " days to complete " + poss + " possibilities");
    }

    private double[] getVector(String vectorName){

        Instant startedGettingVector = Instant.now();

        XSSFRow vectorRow = null;
        for(int i = 0; i < dependenciesSheet.getLastRowNum() + 1; i++){
            XSSFRow row = dependenciesSheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            if(cell0.toString().toLowerCase().equals(vectorName)){
                vectorRow = row;
                break;
            }
        }

        if(vectorRow == null){
            System.out.println("Couldn't find the " + vectorName + " Row");
            return new double[0];
        }

        double[] vector = new double[vectorRow.getLastCellNum() - 1];

        for(int i = 0; i < vectorRow.getLastCellNum() - 1; i++){
            XSSFCell cell = vectorRow.getCell(i + 1);
            vector[i] = Double.parseDouble(cell.toString());
        }

        Instant finishedGettingVector = Instant.now();

        if(Main.debugging){
            System.out.println(Duration.between(startedGettingVector, finishedGettingVector).toMillis() + "ms read vector " + vectorName);
        }

        return vector;
    }

    public static BigInteger powerN(BigInteger number, int power) {
        if(power == 0) return new BigInteger("0");
        BigInteger result = number;

        while(power > 1) {
            result = result.multiply(number);
            power--;
        }

        return result;
    }
}
