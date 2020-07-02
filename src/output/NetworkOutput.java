package output;

import input.NetworkInput;
import input.StandardInput;
import main.Main;
import math.Equations;
import math.MatrixUtil;
import network.Node;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.ejml.simple.SimpleMatrix;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class NetworkOutput {

    private final XSSFSheet linksSheet;
    private final FileInputStream fis;
    private final FileOutputStream fos;
    private final XSSFWorkbook workbook;
    private final StandardInput standardInput;
    private final NetworkInput networkInput;
    private final XSSFCellStyle centerAligned;
    private final XSSFCellStyle roundedCenterAligned;

    private ArrayList<Node> nodes;
    private String[] nodeNames;
    private double[] A;
    private double[] B;
    private int N;
    private double[] positivePhisCounts;
    private double[] negativePhisCounts;
    private long[] divisionCounts;
    private double phisSum;
    private long phiSumDivisionCounts;

    public NetworkOutput(NetworkInput input) throws IOException {
        this.fis = input.fis;
        this.fos = new FileOutputStream(input.path);
        this.workbook = input.workbook;
        this.linksSheet = input.linksSheet;
        this.networkInput = input;
        this.centerAligned = workbook.createCellStyle();
        this.centerAligned.setAlignment(HorizontalAlignment.CENTER);
        this.roundedCenterAligned = workbook.createCellStyle();
        this.roundedCenterAligned.setAlignment(HorizontalAlignment.CENTER);
        this.roundedCenterAligned.setDataFormat((workbook.createDataFormat().getFormat("0.00##")));
        this.standardInput = null;
    }

    public NetworkOutput(StandardInput input) throws IOException, InterruptedException, CloneNotSupportedException {
        this.fis = input.getFis();
        this.fos = new FileOutputStream(input.getPath());
        this.workbook = input.getWorkbook();
        this.linksSheet = input.getLinksSheet();
        this.standardInput = input;
        this.centerAligned = workbook.createCellStyle();
        this.centerAligned.setAlignment(HorizontalAlignment.CENTER);
        this.roundedCenterAligned = workbook.createCellStyle();
        this.roundedCenterAligned.setAlignment(HorizontalAlignment.CENTER);
        this.roundedCenterAligned.setDataFormat((workbook.createDataFormat().getFormat("0.00##")));
        this.networkInput = null;

        standardInput.process();

        this.A = standardInput.getA();
        this.B = standardInput.getB();
        this.N = standardInput.getN();
        this.nodeNames = standardInput.getNodeNames();
        this.positivePhisCounts = standardInput.getPositivePhisCounts();
        this.negativePhisCounts = standardInput.getNegativePhisCounts();
        this.divisionCounts = standardInput.getDivisionCounts();
        this.nodes = standardInput.getNodes();
        this.phisSum = standardInput.getPhisSum();
        this.phiSumDivisionCounts = standardInput.getPhiSumDivisionCounts();
    }

    public void writeStandardInput(boolean writePhisTable){
        if(standardInput == null){
            System.out.println("WRONG INPUT!! PLEASE USE STANDARD INPUT");
        }else{
            XSSFSheet tableFormSheet = workbook.getSheet("Table Form");
            if(tableFormSheet == null){
                tableFormSheet = workbook.createSheet("Table Form");
            }

            double[] case1B = new double[B.length];

            for(int i = 0; i < B.length; i++){
                if(B[i] == 0){
                    case1B[i] = -0.5;
                }else{
                    case1B[i] =B[i];
                }
            }

            Instant startedGettingAlphaMatrix = Instant.now();
            SimpleMatrix alphaMatrix = Equations.getMatrix1Answers(A, B, N, positivePhisCounts, negativePhisCounts, divisionCounts);
            Instant finishedGettingAlphaMatrix = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedGettingAlphaMatrix, finishedGettingAlphaMatrix).toMillis() + "ms got Matrix 1");
            }

            Instant startedGettingAlphaMatrix2 = Instant.now();
            SimpleMatrix alpha2Matrix = Equations.getMatrix2Answers(A, B, N, positivePhisCounts, negativePhisCounts, divisionCounts, phisSum, phiSumDivisionCounts);
            Instant finishedGettingAlphaMatrix2 = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedGettingAlphaMatrix2, finishedGettingAlphaMatrix2).toMillis() + "ms got Matrix 2");
            }

            Instant startedGettingCase1Matrix = Instant.now();
            SimpleMatrix case1Matrix = Equations.getCase(case1B, N, positivePhisCounts, negativePhisCounts, divisionCounts, 1);
            Instant finishedGettingCase1Matrix = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedGettingCase1Matrix, finishedGettingCase1Matrix).toMillis() + "ms got case 1");
            }

            Instant startedGettingCase2Matrix = Instant.now();
            SimpleMatrix case2Matrix = Equations.getCase(B, N,positivePhisCounts, negativePhisCounts, divisionCounts, 2);
            Instant finishedGettingCase2Matrix = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedGettingCase2Matrix, finishedGettingCase2Matrix).toMillis() + "ms got case 2");
            }

            Instant startedGettingCase3Matrix = Instant.now();
            SimpleMatrix case3Matrix = Equations.getCase(B, N, positivePhisCounts, negativePhisCounts, divisionCounts,3);
            Instant finishedGettingCase3Matrix = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedGettingCase3Matrix, finishedGettingCase3Matrix).toMillis() + "ms got case 3");
            }

            Instant startedWritingHeader = Instant.now();
            writeStringVector(tableFormSheet, 0, "", nodeNames);
            writeVector(tableFormSheet, 1, "A", A);
            writeVector(tableFormSheet, 2, "B", B);
            writeVector(tableFormSheet, 3, "Case 1;Alpha", new double[0]);
            writeVector(tableFormSheet, 4, "Case 1;Ordering", new double[0]);
            writeVector(tableFormSheet, 5, "Case 2;Alpha", new double[0]);
            writeVector(tableFormSheet, 6, "Case 2;Ordering", new double[0]);
            writeVector(tableFormSheet, 7, "Case 3;Alpha", new double[0]);
            writeVector(tableFormSheet, 8, "Case 3;Ordering", new double[0]);
            writeVector(tableFormSheet, 9, "Matrix;Alpha", new double[0]);
            writeVector(tableFormSheet, 10, "Matrix;Ordering", new double[0]);
            writeVector(tableFormSheet, 11, "Matrix 2;Alpha", new double[0]);
            writeVector(tableFormSheet, 12, "Matrix 2;Ordering", new double[0]);
            writeVector(tableFormSheet, 13, "", new double[0]);
            Instant finishedWritingHeaders = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedWritingHeader, finishedWritingHeaders).toMillis() + "ms wrote excel headings");
            }

            Instant startedWritingResults = Instant.now();
            writeMatrixResults(tableFormSheet, nodeNames, alphaMatrix, alpha2Matrix, case1Matrix, case2Matrix, case3Matrix);
            Instant finishedWritingResults = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedWritingResults, finishedWritingResults).toMillis() + "ms wrote results");
            }

            Instant startedWritingResourcesSheet = Instant.now();

            //SET NODE OPERABILITY
            double max = 0;
            double min = 100;
            for(int i = 0; i < nodes.size(); i++){
                if(alphaMatrix.get(i, 0) > max){
                    max = alphaMatrix.get(i, 0);
                }
                if(alphaMatrix.get(i, 0) < min){
                    min = alphaMatrix.get(i, 0);
                }
                nodes.get(i).alpha = alphaMatrix.get(i, 0);
            }


            for(int i = 0; i < nodes.size(); i++){
                if((max - min) == 0){
                    nodes.get(i).operability = 100;
                }else{
                    nodes.get(i).operability =  (100 * ((alphaMatrix.get(i, 0) - min) / (max - min)));
                }
            }

            writeResources(nodes);

            Instant finishedWritingResourcesSheet = Instant.now();

            if(Main.debugging){
                System.out.println(Duration.between(startedWritingResourcesSheet, finishedWritingResourcesSheet).toMillis() + "ms wrote Resources Sheet");
            }

            /*
            if(writePhisTable){

                int tableOffset = 1;
                writeTable(tableFormSheet, 13, tableOffset, standardInput.getNodeNames(), possibilitiesTable);

                String[] phisHeader = new String[1];
                phisHeader[0] = "Phis";
                int[][] data = new int[1][phis.length];
                System.arraycopy(phis, 0, data[0], 0, phis.length);
                writeTable(tableFormSheet, 13, tableOffset + standardInput.getNodes().size(), phisHeader, data);

                XSSFRow row = tableFormSheet.getRow(13);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue("Table");
                cell.setCellStyle(centerAligned);

                if(Main.debugging){
                    System.out.println(((System.currentTimeMillis() - NetworkOutput.currentTimeMillis) / 1000f) + " wrote Possibilities Table");
                }
            }

             */

        }
    }

    public void writeNetworkInput(){

        /*
        double[] case3B = new double[B.length];

        for(int i = 0; i < B.length; i++){
            if(B[i] == 0){
                case3B[i] = -0.5;
            }else{
                case3B[i] =B[i];
            }
        }

        SimpleMatrix alphaMatrix = Equations.getMatrix1Answers(A, B, N, positivePhisCounts);

        SimpleMatrix alpha2Matrix = Equations.getMatrix2Answers(A, B, N, positivePhisCounts, phisSum);

        SimpleMatrix case1Matrix = Equations.getCase(case3B, N, positivePhisCounts, negativePhisCounts, 1);

        SimpleMatrix case2Matrix = Equations.getCase(B, N,positivePhisCounts, negativePhisCounts, 2);

        SimpleMatrix case3Matrix = Equations.getCase(B, N, positivePhisCounts, negativePhisCounts, 3);

        writeMatrixResults(linksSheet, nodeNames, alphaMatrix, alpha2Matrix, case1Matrix, case2Matrix, case3Matrix);

         */
    }

    public void writeTable(XSSFSheet sheet, int rowNumber, int columnNumber, String[] headers, int[][] data){
        XSSFRow headerRow = sheet.getRow(rowNumber);
        if(headerRow == null){
            headerRow = sheet.createRow(rowNumber);
        }
        for(int i = 0; i < headers.length; i++){
            XSSFCell cell = headerRow.createCell(i + columnNumber);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(centerAligned);
        }

        for(int i = 0; i < data[0].length; i++){
            XSSFRow row = sheet.getRow(rowNumber + i + 1);
            if(row == null){
                row = sheet.createRow(rowNumber + i + 1);
            }
            for (int a = 0; a < data.length; a++){
                XSSFCell cell = row.createCell(a + columnNumber);
                cell.setCellValue(data[a][i]);
                cell.setCellStyle(centerAligned);
            }
        }
    }

    public void writeVector(XSSFSheet sheet, int rowNumber, String header, double[] vector){

        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCell headerCell = row.createCell(0);
        headerCell.setCellValue(header);
        headerCell.setCellStyle(centerAligned);
        for(int i = 0; i < vector.length; i++){
            XSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(vector[i]);
            cell.setCellStyle(centerAligned);
        }

    }

    public void writeStringVector(XSSFSheet sheet, int rowNumber, String header, String[] vector){

        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCell headerCell = row.createCell(0);
        headerCell.setCellValue(header);
        headerCell.setCellStyle(centerAligned);
        for(int i = 0; i < vector.length; i++){
            XSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(vector[i]);
            cell.setCellStyle(centerAligned);
        }

    }

    public void writeVectorByFindingHeader(XSSFSheet sheet, String header, double[] vector){

        XSSFRow vectorRow = null;
        for(int i = 0; i < sheet.getLastRowNum(); i++){
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = null;
            try {
                cell = row.getCell(0);
            }catch (Exception ignored){}
            if(cell != null){
                if(cell.toString().toLowerCase().equals(header)){
                    vectorRow = row;
                    break;
                }
            }
        }

        if(vectorRow == null){
            System.out.println("Could not find Row labeled " + header);
            return;
        }

        for(int i = 0; i < vector.length; i++){
            XSSFCell cell = vectorRow.createCell(i + 1);
            cell.setCellValue(vector[i]);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(roundedCenterAligned);
        }
    }

    public void writeStringVectorByFindingHeader(XSSFSheet sheet, String header, String[] vector){

        XSSFRow vectorRow = null;
        for(int i = 0; i < sheet.getLastRowNum(); i++){
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = null;
            try {
                cell = row.getCell(0);
            }catch (Exception ignored){}
            if(cell != null){
                if(cell.toString().toLowerCase().equals(header)){
                    vectorRow = row;
                    break;
                }
            }
        }

        if(vectorRow == null){
            System.out.println("Could not find Row labeled " + header);
            return;
        }

        for(int i = 0; i < vector.length; i++){
            XSSFCell cell = vectorRow.createCell(i + 1);
            cell.setCellValue(vector[i]);
            cell.setCellStyle(centerAligned);
        }
    }

    public void writeResources(ArrayList<Node> nodes){

        if(standardInput == null){
            System.out.println("You have the wrong input");
        }else{
            XSSFSheet resourcesSheet = workbook.getSheet("Resources");
            if(resourcesSheet == null){
                resourcesSheet = workbook.createSheet("Resources");
            }

            XSSFRow headerRow = resourcesSheet.createRow(0);
            XSSFCell cell = headerRow.createCell(0);
            cell.setCellValue("Label");
            cell = headerRow.createCell(1);
            cell.setCellValue("Id");
            cell = headerRow.createCell(2);
            cell.setCellValue("Operability_Level");
            cell = headerRow.createCell(3);
            cell.setCellValue("Alphas");

            for(int i = 0; i < nodes.size(); i++){
                XSSFRow row = resourcesSheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(nodes.get(i).itemName);
                cell = row.createCell(2);
                cell.setCellValue(nodes.get(i).operability);
                cell = row.createCell(3);
                cell.setCellValue(nodes.get(i).alpha);
            }
        }
    }

    public void close() throws IOException {
        fis.close();
        workbook.write(fos);
        fos.close();
    }

    private static String[] orderVectorByName(String[] nodeNames, double[] vector){
        String[] orderedString = new String[nodeNames.length];

        double[] vectorCopy = new double[nodeNames.length];
        for(int i = 0; i < nodeNames.length; i++){
            vectorCopy[i] = vector[i];
        }
        Arrays.sort(vectorCopy);
        double[] reverseVectorCopy = new double[vector.length];
        for(int i = 0; i < vectorCopy.length; i++){
            reverseVectorCopy[vectorCopy.length - 1 - i] = vectorCopy[i];
        }

        ArrayList<Integer> used = new ArrayList<>();
        for(int a = 0; a < nodeNames.length; a++){
            for(int i = 0; i < nodeNames.length; i++){
                if(reverseVectorCopy[a] == vector[i] && !used.contains(i)){
                    orderedString[a] = nodeNames[i];
                    used.add(i);
                    break;
                }
            }
        }

        return orderedString;
    }

    private void writeMatrixResults(XSSFSheet sheet, String[] nodeNames, SimpleMatrix alphaMatrix, SimpleMatrix alpha2Matrix ,SimpleMatrix case1Matrix, SimpleMatrix case2Matrix, SimpleMatrix case3Matrix){
        writeVectorByFindingHeader(sheet, "case 1;alpha", MatrixUtil.createVectorFromMatrixData(case1Matrix));
        String[] ordered = orderVectorByName(nodeNames, MatrixUtil.createVectorFromMatrixData(case1Matrix));
        writeStringVectorByFindingHeader(sheet, "case 1;ordering", ordered);

        writeVectorByFindingHeader(sheet, "case 2;alpha", MatrixUtil.createVectorFromMatrixData(case2Matrix));
        ordered = orderVectorByName(nodeNames, MatrixUtil.createVectorFromMatrixData(case2Matrix));
        writeStringVectorByFindingHeader(sheet, "case 2;ordering", ordered);

        writeVectorByFindingHeader(sheet, "case 3;alpha", MatrixUtil.createVectorFromMatrixData(case3Matrix));
        ordered = orderVectorByName(nodeNames, MatrixUtil.createVectorFromMatrixData(case3Matrix));
        writeStringVectorByFindingHeader(sheet, "case 3;ordering", ordered);

        writeVectorByFindingHeader(sheet, "matrix;alpha", MatrixUtil.createVectorFromMatrixData(alphaMatrix));
        ordered = orderVectorByName(nodeNames, MatrixUtil.createVectorFromMatrixData(alphaMatrix));
        writeStringVectorByFindingHeader(sheet, "matrix;ordering", ordered);

        writeVectorByFindingHeader(sheet, "matrix 2;alpha", MatrixUtil.createVectorFromMatrixData(alpha2Matrix));
        ordered = orderVectorByName(nodeNames, MatrixUtil.createVectorFromMatrixData(alpha2Matrix));
        writeStringVectorByFindingHeader(sheet, "matrix 2;ordering", ordered);
    }

}
