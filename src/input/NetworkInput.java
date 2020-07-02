package input;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class NetworkInput {

    public final XSSFSheet linksSheet;
    public final FileInputStream fis;
    public final XSSFWorkbook workbook;
    public final String path;

    public NetworkInput(String path) throws IOException {
        this.path = path;
        fis = new FileInputStream(path);
        workbook = new XSSFWorkbook(fis);
        linksSheet = workbook.getSheet("Links");
    }


    public int[][] getNodeTable(){
        XSSFRow tableRow = null;
        for(int i = 0; i < linksSheet.getLastRowNum(); i++){
            XSSFRow row = linksSheet.getRow(i);
            XSSFCell cell = null;
            try {
                cell = row.getCell(0);
            }catch (Exception ignored){

            }
            if(cell != null){
                if(cell.toString().toLowerCase().equals("table")){
                    tableRow = row;
                    break;
                }
            }
        }

        if(tableRow == null){
            System.out.println("Could not find Row Table");
            return new int[0][0];
        }

        int numberOfNodes = tableRow.getLastCellNum() - 2;

        int[][] nodeTable = new int[numberOfNodes][linksSheet.getLastRowNum() - tableRow.getRowNum()];

        for(int i = 0; i < linksSheet.getLastRowNum() - tableRow.getRowNum(); i++){
            XSSFRow rowI = linksSheet.getRow(i + 1 + tableRow.getRowNum());
            for(int a = 0; a < numberOfNodes; a++){
                XSSFCell cellA = rowI.getCell(a + 1);
                nodeTable[a][i] = (int) Double.parseDouble(cellA.toString());
            }
        }
        return nodeTable;
    }

    public String[] getNodeNames(){
        XSSFRow tableRow = null;
        for(int i = 0; i < linksSheet.getLastRowNum(); i++){
            XSSFRow row = linksSheet.getRow(i);
            XSSFCell cell = null;
            try {
                cell = row.getCell(0);
            }catch (Exception ignored){

            }
            if(cell != null){
                if(cell.toString().toLowerCase().equals("table")){
                    tableRow = row;
                    break;
                }
            }
        }

        if(tableRow == null){
            System.out.println("Could not find Row Table");
            return new String[0];
        }

        int numberOfNodes = tableRow.getLastCellNum() - 2;
        String[] nodeNames = new String[numberOfNodes];

        for(int a = 0; a < numberOfNodes; a++){
            XSSFCell cellA = tableRow.getCell(a + 1);
            nodeNames[a] = cellA.toString();
        }
        return nodeNames;
    }

    public int[] getPhi(){
        XSSFRow tableRow = null;
        for(int i = 0; i < linksSheet.getLastRowNum(); i++){
            XSSFRow row = linksSheet.getRow(i);
            XSSFCell cell = null;
            try {
                cell = row.getCell(0);
            }catch (Exception ignored){

            }
            if(cell != null){
                if(cell.toString().toLowerCase().equals("table")){
                    tableRow = row;
                    break;
                }
            }
        }

        if(tableRow == null){
            System.out.println("Could not find Row Table");
            return new int[0];
        }

        int[] phis = new int[linksSheet.getLastRowNum() - tableRow.getRowNum()];

        for(int i = 0; i < linksSheet.getLastRowNum() - tableRow.getRowNum(); i++){
            XSSFRow rowI = linksSheet.getRow(i + 1 + tableRow.getRowNum());
            XSSFCell cell = rowI.getCell(tableRow.getLastCellNum() - 1);
            phis[i] = (int) Double.parseDouble(cell.toString());
        }
        return phis;
    }

    public double[] getVector(String vectorName){

        XSSFRow vectorRow = null;
        for(int i = 0; i < linksSheet.getLastRowNum(); i++){
            XSSFRow row = linksSheet.getRow(i);
            XSSFCell cell = row.getCell(0);
            if(cell != null){
                if(cell.toString().toLowerCase().equals(vectorName)){
                    vectorRow = row;
                    break;
                }
            }
        }

        if(vectorRow == null){
            System.out.println("Could not find Row A");
            return new double[0];
        }

        int numberOfNodes = vectorRow.getLastCellNum() - 1;
        double[] vector =  new double[numberOfNodes];

        for(int i = 0; i < numberOfNodes; i++){
            XSSFCell cell = vectorRow.getCell(i +1);
            try {
                vector[i] = Double.parseDouble(cell.toString());
            }catch (Exception a){
                a.printStackTrace();
                return new double[0];
            }
        }
        return vector;
    }
}
