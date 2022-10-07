import java.io.BufferedReader; 
import java.io.File;
import java.io.FileInputStream; 
import java.io.InputStreamReader; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.HashSet;
import java.util.List; 
import java.util.Scanner; 
import java.util.Set; 

public class Brute_force {
    private static double min_support; 
    private static double min_confidence; 
    static List<List<String>> record=new
    ArrayList<List<String>>();
    static List<Set<String>> frequentIS=new
    ArrayList<Set<String>>();
    static List<String> itemset=new ArrayList<String>(); static boolean endTag=false;
    static int times=1;

    public static void main(String[] args) { 
        System.out.println("please input the min_support value");
        Scanner in=new Scanner(System.in); 
        min_support=in.nextDouble(); 
        System.out.println("the min_support value is"+min_support);
        System.out.println("please input the min_condidence value");
        min_confidence=in.nextDouble();
        System.out.println("the min_confidence value is "+min_confidence);
        System.out.println();
        in.close();
        long start=System.currentTimeMillis();
        record=getrecord("./DataSet01.txt");//scan data-set
        
        //showData(record); for test
        