package math;

import org.ejml.simple.SimpleMatrix;

public class Equations {

    public static SimpleMatrix getMatrix1Answers(double[] A, double[] B, int N, double[] positivePhisCounts, double[] negativePhisCounts,long[] divisionCount){

        double[][] matrixData = new double[N][N];
        for(int n = 0; n < N; n++){
            for(int m = 0; m < N; m++){
                if(m == n){
                    matrixData[n][m] = (Math.pow(A[n] + B[n], 2)) + (Math.pow(B[n], 2));
                }else{
                    matrixData[n][m] = (A[n] + 2 * B[n]) * ((A[m]/2) + B[m]);
                }
            }
        }

        double[] capitalPhis = new double[N];
        for(int n = 0; n < N; n++){
            double capitalPhi = ((A[n] + B[n]) * positivePhisCounts[n] + B[n] * negativePhisCounts[n]) / (Math.pow(2, N - (divisionCount[n] + 1)));
            capitalPhis[n] = capitalPhi;
        }


        SimpleMatrix matrixA = new SimpleMatrix(matrixData);
        SimpleMatrix capitalPhisTransverseMatrix = MatrixUtil.createMatrixFromVectorData(capitalPhis);
        SimpleMatrix matrixAInverse = matrixA.invert();
        SimpleMatrix alpha = matrixAInverse.mult(capitalPhisTransverseMatrix);
        return alpha;
    }

    public static SimpleMatrix getMatrix2Answers(double[] A, double[] B, int N, double[] positivePhisCounts, double[] negativePhisCounts,long[] divisionCount, double phisSum, long phiSumDivisionCounts){

        double[][] matrixCData = new double[(N + 1)][(N + 1)];
        for (int n = 0; n < (N + 1); n++){
            for(int m = 0; m < (N + 1); m++){
                int min = Math.min(n, m);

                if (n < N && m < N && n != m){
                    matrixCData[n][m] = Math.pow(2, N - 1) * Math.pow((A[n] + 2 * B[n]), 2);
                }else if(m < N && m == n){
                    matrixCData[n][m] = Math.pow(2, N) * (Math.pow((A[n] + B[n]), 2) + Math.pow(B[n], 2));
                }else if((m == N && n != m) || (n == N && n != m)){
                    matrixCData[n][m] = Math.pow(2, N) * (A[min] + 2 * B[min]);
                }else if(n == N){
                    matrixCData[n][m] = Math.pow(2, N + 1);
                }
            }
        }

        double[] capitalPhis = new double[(N + 1)];
        for(int n = 0; n < (N + 1); n++){
            if(n < N){
                capitalPhis[n] = (2 * (A[n] + B[n]) * positivePhisCounts[n] + 2 * B[n] * negativePhisCounts[n]) * Math.pow(2, divisionCount[n]);
            }else if(n == N){
                capitalPhis[n] = (2 * phisSum) * Math.pow(2, phiSumDivisionCounts);

            }
        }

        System.out.println("Matrix C");
        SimpleMatrix matrixC = new SimpleMatrix(matrixCData);
        MatrixUtil.displayMatrix(matrixC);
        System.out.println();

        System.out.println("Matrix C Inverse");
        SimpleMatrix matrixCInverse = matrixC.invert();
        MatrixUtil.displayMatrix(matrixCInverse);
        System.out.println();

        System.out.println("Matrix Capital Phis");
        SimpleMatrix matrixCapitalPhis = MatrixUtil.createMatrixFromVectorData(capitalPhis);
        MatrixUtil.displayMatrix(matrixCapitalPhis);
        System.out.println();

        System.out.println("Matrix Alpha");
        SimpleMatrix alpha = matrixCInverse.mult(matrixCapitalPhis);
        MatrixUtil.displayMatrix(alpha);
        System.out.println();

        return alpha;
    }

    public static SimpleMatrix getCase(double[] B, int N, double[] positivePhisCounts, double[] negativePhisCounts, long[] divisionCount, int caseNumber) {


        if(caseNumber == 1){
            double[] answers = new double[N];
            for(int i = 0; i < N; i++){
                double answer = (-1/(2 * B[i])) * ((positivePhisCounts[i] / Math.pow(2.0,N - (divisionCount[i] + 1))) - (negativePhisCounts[i] / Math.pow(2.0, N - (divisionCount[i] + 1))));
                answers[i] = answer;
            }
            return MatrixUtil.createMatrixFromVectorData(answers);
        }

        if(caseNumber == 2){

            double positiveSum = 0;
            for (int n = 0; n < positivePhisCounts.length; n++) {
                positiveSum += positivePhisCounts[n] / Math.pow(2, N - (divisionCount[n] + 2));
            }

            double positiveCountAverage = positiveSum / (N + 1);
            
            double[] answers = new double[N];
            for(int i = 0; i < N; i++){
                double answer = ((positivePhisCounts[i] / Math.pow(2.0, N - (divisionCount[i] + 2))) - positiveCountAverage);
                answers[i] = answer;
            }
            return MatrixUtil.createMatrixFromVectorData(answers);
        }

        if(caseNumber == 3){

            double negativeSum = 0;
            for (int n = 0; n < negativePhisCounts.length; n++) {
                negativeSum += negativePhisCounts[n] / Math.pow(2, N - (divisionCount[n] + 2));
            }

            double negativeCountsAverage = negativeSum / (N + 1);
            
            double[] answers = new double[N];
            for(int i = 0; i < N; i++){
                double answer = (negativeCountsAverage - (negativePhisCounts[i] / Math.pow(2.0, N - (divisionCount[i] + 2))));
                answers[i] = answer;
            }
            return MatrixUtil.createMatrixFromVectorData(answers);
        }

        return new SimpleMatrix(new double[N][N]);
    }

}
