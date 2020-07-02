package main;

import input.StandardInput;
import output.NetworkOutput;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {

    public static Scanner in = new Scanner(System.in);

    public static boolean debugging = false;
    public static int maxNumberOfMinutes = 1;
    public static int numberOfThreads = Runtime.getRuntime().availableProcessors();;
    public static boolean randomPossibilitySelection;
    public static int decimalPlaces;

    public static void main(String[] args) throws IOException, InterruptedException, CloneNotSupportedException {

        Instant mainInitiation = Instant.now();;

        //Get the path of the input file
        System.out.println("What is the absolute path of the excel file? Write it down below");
        String path = in.nextLine();

        System.out.println("Do you want to randomly pick probabilities? This is recommended for large networks");
        System.out.println("Answer yes or no.");

        boolean hasRandom = false;
        while(!hasRandom){
            String answer = in.nextLine();
            if(answer.equals("yes")){
                randomPossibilitySelection = true;
                hasRandom = true;
            }else if(answer.equals("no")){
                randomPossibilitySelection = false;
                hasRandom = true;
            }else{
                System.out.println("Please try again and answer yes or no");
            }
        }

        NetworkOutput output = new NetworkOutput(new StandardInput(path));
        output.writeStandardInput(false);
        output.close();

        Instant mainEnded = Instant.now();
        if(debugging){
            System.out.println(Duration.between(mainInitiation, mainEnded).toMinutes() + "m Program Ended");
        }
    }

}
