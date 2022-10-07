import java.io.BufferedReader; import java.io.File;
import java.io.FileInputStream; import java.io.InputStreamReader; import java.util.ArrayList; import java.util.Collections; import java.util.HashSet;
import java.util.Iterator; import java.util.List; import java.util.Scanner;

public class AprioriAlgo{
    private static double min_support; 
    private static double min_confidence; 
    static List<List<String>> record=new ArrayList<List<String>>();
    static List<List<String>> frequentIS=new ArrayList<List<String>>(); 
    static boolean endTag; 
    static int times=2;
    public static void main(String[] args) { 
        System.out.println("please input the min_support value");
        Scanner in=new Scanner(System.in); min_support=in.nextDouble(); 
        System.out.println("the min_support value is"+min_support);
        System.out.println("please input the min_condidence value");
        min_confidence=in.nextDouble();
        System.out.println("the min_confidence value is "+min_confidence);
        System.out.println();
        in.close();
        long start=System.currentTimeMillis();//count time from here
        record=getrecord("./DataSet01.txt");//scan data-set //showData(record); for test
        apriori_alg();//use Apriori algorithm to generate frequent item-sets
        //System.out.println("Frequent item-sets is following:"); for test
          //showData(frequentIS); for test
        System.out.println();
        
        AssociationRulesMining();//generate all the association rules here
        long end=System.currentTimeMillis();//count time until System.out.println("Executing time: "+(end-start)
        
        System.out.println("Executing time: "+(end-start)+"ms");
    }

    /**
    * method description: read transaction records from data_set file
    * @param path
    * @return
    */
    private static List<List<String>> getrecord(String path){ 
        try {
            File dataset=new File(path); 
            if(dataset.isFile()&&dataset.exists()) {
                InputStreamReader read=new InputStreamReader(new FileInputStream(dataset));
                BufferedReader buffered_reader=new BufferedReader(read);
                String one_line; 
                
                while((one_line=buffered_reader.readLine())!=null) {
                    String[] one_record=one_line.split(",|:"); 
                    List<String> lineList=new ArrayList<String>();

                    for(int i=1;i<one_record.length;i++) {
                        lineList.add(one_record[i]);
                    } 
                    
                    Collections.sort(lineList);
                    record.add(lineList); 
                }
                read.close();
            }
            
            else if(dataset.isDirectory()&&dataset.exists()){
                System.out.println("Path is a directory!");
            }
            else System.out.println("Can't find the file");
        }
        catch(Exception e){ 
            e.printStackTrace();
        }
        System.out.println("The input data is following:");
        showData(record);
        System.out.println();
        return record;
    }
    /**
    * method description: implementation of Apriori Algorithm
    */ 
    private static void apriori_alg() { 
        List<List<String>> tempFList;
        
        //definition of OneD_CandidateSet: all the candidate item-sets only have one item
        List<List<String>> OneD_CandidateSet=get1stCandidateSet();
        //showData(OneD_CandidateSet)
        //definition of OneD_FrequentSet: all the frequent item-sets only have one item
        List<List<String>> OneD_FrequentSet=getNthFrequentSet(OneD_CandidateSet);
        frequentIS.addAll(OneD_FrequentSet); 
        //System.out.println("1D frequent item-set:"); for test 
        //showData(OneD_FrequentSet); for test
        tempFList=OneD_FrequentSet; 
    
        while(endTag==false) {
            List<List<String>> nextCandidateSet=getNextCandidateSet(tempFList);
            List<List<String>> nextFrequentSet=getNthFrequentSet(nextCandidateSet); 
            frequentIS.addAll(nextFrequentSet); 

            if(endTag==false) {
                System.out.println(times+"D frequent item-set:"); 
                showData(nextFrequentSet); 
                times++; 
                tempFList=nextFrequentSet; 
            } 
        } 
    }
    /**
    * method description: find the 1st candidate set(all theunique items) 
    * @return
    */
    private static List<List<String>> get1stCandidateSet(){
        List<List<String>> tablelist=new ArrayList<List<String>>();
        HashSet<String> hs=new HashSet<String>();
        
        for(int i=0;i<record.size();i++) {
            for(int j=0;j<record.get(i).size();j++) {
                hs.add(record.get(i).get(j));
            }
        }

        Iterator<String> iterator=hs.iterator();
        while(iterator.hasNext()) {
            List<String> tempList=new ArrayList<String>();
            tempList.add(iterator.next());
            tablelist.add(tempList);
        }
        return tablelist;
    }
    /**
    * get frequent-sets from nth candidate-set * @param NthcandidateSet
    * @return
    */
    private static List<List<String>> getNthFrequentSet(List<List<String>> NthcandidateSet){
        int count;
        boolean endTag_=true; 
        List<List<String>> nthFrequentSet=new ArrayList<List<String>>();

        for(int i=0;i<NthcandidateSet.size();i++) { 
            count=countFrequency(NthcandidateSet.get(i)); 
            
            if(count>=min_support*record.size()) {
                nthFrequentSet.add(NthcandidateSet.get(i));
                //map.add(new Mymap(NthcandidateSet.get(i),count));
                endTag_=false; 
            }
        }
        endTag=endTag_;
        if(endTag==true) {
            System.out.println("can't find more frequent-sets!");
        }
        return nthFrequentSet; 
    }
    /**
    * method description: count the times that one item-set in nth-candidateSet occurs
    * @param candidateSet
    * @return
    */ 
    private static int countFrequency(List<String> candidateSet) {
        int count=0;
        
        for(int i=0;i<record.size();i++) {
            boolean countTag=true;
            
            for(int j=0;j<candidateSet.size();j++) {
                String temp=candidateSet.get(j); 
                
                if(!record.get(i).contains(temp)) {
                    countTag=false;
                    break;
                }
            }
            if(countTag==true) count++; 
        }
        return count;
    }
    /**
    * method description: use the last frequent item-sets to generate the next candidate item-sets
    * @param LastFrequentSet 
    * @return
    */
    private static List<List<String>> getNextCandidateSet(List<List<String>> LastFrequentSet){
        List<List<String>> nextCandidateSet=new ArrayList<List<String>>();
        List<List<String>> afterJoining=new ArrayList<List<String>>();
        int dimension=LastFrequentSet.get(0).size(); 
        
        for(int i=0;i<LastFrequentSet.size();i++) {
            //join
            for(int j=i+1;j<LastFrequentSet.size();j++) {
                boolean joinable=true;
                for(int k=0;k<dimension-1;k++) {
                    //Step 1 ofchecking if two item-set can be joined
                    if(LastFrequentSet.get(i).get(k)! =LastFrequentSet.get(j).get(k)) {
                        joinable=false;
                    }
                }
                if(LastFrequentSet.get(i).get(dimension-1)==LastFrequentSet.get( j).get(dimension-1)) {
                    //Step 2 of checking two item-set can be joined
                    joinable=false; 
                }
                if(joinable==true) {
                    List<String> temp=new ArrayList<String>();
                    temp.addAll(LastFrequentSet.get(i));
                    temp.add(LastFrequentSet.get(j).get(dimension-1)); 
                    Collections.sort(temp); 
                    afterJoining.add(temp);
                }
            }
        }
        //showData(afterJoining); For test
        for(int i=0;i<afterJoining.size();i++) {
            //remove item- set whose subsets are not all in the last frequent item-set
            boolean allSubsetOccur=true; 
            for(int j=0;j<dimension;j++) {
                String item=afterJoining.get(i).remove(j);
                List<String> temp=afterJoining.get(i); 
                if(!LastFrequentSet.contains(temp)) {
                    allSubsetOccur=false; }
                    afterJoining.get(i).add(j,item);//return afterJoining to the original
                }
                if(allSubsetOccur==true) {
                    nextCandidateSet.add(afterJoining.get(i));
                }
            }
            if(nextCandidateSet.isEmpty()) {
                System.out.println("Can't generate next candidateSet!");
            }
            return nextCandidateSet;
        }
        
        private static void showData(List<List<String>> list) { 
            for(int i=0;i<list.size();i++) {
                int temp=list.get(i).size()-1; 
                for(int j=0;j<temp;j++) {
                    System.out.print(list.get(i).get(j)+", "); 
                }
                System.out.print(list.get(i).get(temp));
                System.out.println(); 
            }
        }
        private static void AssociationRulesMining() { 
            for(int i=0;i<frequentIS.size();i++) {
                List<String> temp=frequentIS.get(i); 
                if(temp.size()>1) {
                    List<String> tempClone=new ArrayList<String>();
                    tempClone.addAll(temp);
                    List<List<String>> allSubset=getSubsets(tempClone);
                    for(int j=0;j<allSubset.size();j++) { 
                        List<String> s1=allSubset.get(j); 
                        List<String> s2=getRest(temp,s1); 
                        double confidence=isAssociationRule(s1,s2,temp); 
                        if(confidence>=min_confidence) {
                            double support=getCount(temp)*1.0/record.size(); 
                            System.out.println("[Support= "+support+", confidence= "+confidence+"]");
                        }
                    }
                }
            }
        }
        private static List<List<String>> getSubsets(List<String> list) {
            List<List<String>> result=new ArrayList<List<String>>();
            int length=list.size();
            int numberOfSubsets=length==0?0:1<<(length);

            for(int i=1;i<numberOfSubsets-1;i++) {
                //Starting from 1 because we don't need empty set
                List<String> subset=new ArrayList<String>();
                int index=i;
                for(int j=0;j<list.size();j++) {
                    if((index&1)==1) { 
                        subset.add(list.get(j));
                    }
                    index=index>>1;
                }
                Collections.sort(subset); 
                result.add(subset);
            }
            return result; 
        }
        /**
        * method description: extract s1 from temp 
        * @param temp
        * @param s1
        * @return
        */
        private static List<String> getRest(List<String> temp, List<String> s1){
            List<String> result=new ArrayList<String>();
            for(int i=0;i<temp.size();i++) { 
                String item=temp.get(i); 
                if(!s1.contains(item)) {
                    result.add(item);
                }
            } 
            return result; 
        }
        /**
        * method description: check if a subset of a item-set can generate an association rule 
        * @param s1
        * @param s2
        * @param temp 
        * @return
        */ 
        private static double isAssociationRule(List<String> s1,List<String> s2,List<String> temp) {
            double confidence; int counts1;
            int counttemp;

            if(temp.size()!=0&&temp!=null) { 
                counts1=getCount(s1); 
                //System.out.println(counts1); 
                counttemp=getCount(temp); 
                //System.out.println(counttemp); 
                confidence=counttemp*1.0/counts1;

                if(confidence>=min_confidence) { 
                    System.out.print("Association rule:"+s1.toString()+"-->"+s2.toString()+" "); 
                    return confidence;
                }
                else return 0; 
            }
            else return 0; 
        }

        private static int getCount(List<String> list) { 
            int count=0;
            for(int i=0;i<record.size();i++) { 
                boolean tag=true;
                for(int j=0;j<list.size();j++) {
                    String temp=list.get(j); 
                    if(!record.get(i).contains(temp)) {
                        tag=false; 
                        break; 
                    }
                    if(tag==true) count++;
                }
                return count; 
            }
        }
    }
        







            
        
        



















