package math;

import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;

public class MatrixUtil {

    public static SimpleMatrix createMatrixFromVectorData(double[] vectorData){

        double[][] matrixData = new double[vectorData.length][1];
        for(int i = 0; i < vectorData.length; i++){
            matrixData[i][0] = vectorData[i];
        }

        return new SimpleMatrix(matrixData);
    }

    public static double[] createVectorFromMatrixData(SimpleMatrix matrix){

        double[] vector = new double[matrix.numRows()];
        for(int i = 0; i < matrix.numRows(); i++){
            vector[i] = matrix.get(i, 0);
        }
        return vector;
    }

    public static void displayMatrix(SimpleMatrix matrix) {
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++)
                System.out.print(matrix.get(i, j) + " ");
            System.out.println();
        }
    }

}
