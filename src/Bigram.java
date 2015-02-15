
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bhumikasaivamani
 */
public class Bigram 
{
    Map<String,Integer> Vocabulary;
    Map<String,ColumnData> BigramCountTable;
    //Map<String,ColumnData> BigramProbabilityTable;
    int bigramCount;
    Map<Integer,Integer> bigramCountMap;
    
    public Bigram()
    {
        bigramCount=0;
        Vocabulary=new HashMap<String,Integer>();
        BigramCountTable=new HashMap<String,ColumnData>();
        bigramCountMap = new HashMap<>();
        
        //BigramProbabilityTable=new HashMap<String,ColumnData>();
    }
 
    public void ExtractData(String path)
    {
        try
        {
            FileReader input=new FileReader(path);
            BufferedReader br=new BufferedReader(input);
            String line=br.readLine();
            while(line!=null)
            {
                StringTokenizer token=new StringTokenizer(line," ");
                while(token.hasMoreTokens())
                {
                   String word=token.nextToken().trim().toLowerCase();
                   if(word.length()==1 && !word.equals("."))
                   {
                       word=word.replaceAll("[^a-zA-Z0-9]+","");
                   }
                   if(word.length()==0)
                       continue;
                   if(Vocabulary.containsKey(word))
                   {
                       int value=Vocabulary.get(word);
                       int newValue=value+1;
                       Vocabulary.replace(word, newValue);
                   }
                   else
                   {
                      Vocabulary.put(word,1);
                   }
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        System.out.println("");
    }
    
    public void calculateBigramCounts(String path)
    {
        String previousWord="";
        for(String word:Vocabulary.keySet())
        {
          BigramCountTable.put(word, new ColumnData(Vocabulary.keySet()));
        }
        
        //read corpus
        try
        {
            FileReader input=new FileReader(path);
            BufferedReader br=new BufferedReader(input);
            String line=br.readLine();
            while(line!=null)
            {
                StringTokenizer token=new StringTokenizer(line," ");
                while(token.hasMoreTokens())
                {
                   String word=token.nextToken().trim().toLowerCase();
                   
                   /*if(word.equals("."))
                   {
                       previousWord="";
                       continue;
                   }*/
                   if(word.length()==1 && !word.equals("."))
                   {
                       word=word.replaceAll("[^a-zA-Z0-9]+","");
                   }
                   if(word.length()==0)
                       continue;
                  
                   if(!previousWord.equals(""))
                   {
                       double value=BigramCountTable.get(word).columnValues.get(previousWord)+1;
                       BigramCountTable.get(word).columnValues.replace(previousWord, value);
                       bigramCount++;
                   }
                   previousWord=word;
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        buildBigramCountMap();
        //System.out.println(BigramCountTable.get("the").columnValues.get("."));
    }
    
    public void buildBigramCountMap()
    {
        for(String key : BigramCountTable.keySet())
        {
            for(String prev : BigramCountTable.get(key).columnValues.keySet())
            {
                double dVal = BigramCountTable.get(key).columnValues.get(prev);
                int val = (int) dVal;
                if(bigramCountMap.get(val)==null){
                    bigramCountMap.put(val, 1);
                }
                else
                {
                    int oldVal = bigramCountMap.get(val);
                    bigramCountMap.replace(val, oldVal+1);
                }
            }
        }
    }
    
    public int VocabularyWordCount()
    {
        int vCount=0;
        for(String s:Vocabulary.keySet())
        {
            vCount+=Vocabulary.get(s);
        }
        return vCount;
    }
    /*
    public void calculateBigramProbabilities()
    {
        int vCount=VocabularyWordCount();
        for(String word:Vocabulary.keySet())
        {
          BigramProbabilityTable.put(word, new ColumnData(Vocabulary.keySet()));
        }
        for(String word:BigramCountTable.keySet())
        {
            for(String prevWord:BigramCountTable.get(word).columnValues.keySet())
            {
                double v=BigramCountTable.get(word).columnValues.get(prevWord);
                if(v>0)
                {
                    double prob=(double)v/Vocabulary.get(prevWord);
                    BigramProbabilityTable.get(word).columnValues.put(prevWord, prob);
                }
            }
        }
        System.out.println(BigramProbabilityTable.get("the").columnValues.get("from"));
    }
    */
    
    public double getBigramProbability(String word,String prevWord)
    {
        double v=BigramCountTable.get(word).columnValues.get(prevWord);
        if(v>0)
        {
            double prob=(double)v/Vocabulary.get(prevWord);
            return prob;
        }
        return 0.0;
    }
    
    public double getBigramSmoothingProbability(String word,String prevWord)
    {
        double v=BigramCountTable.get(word).columnValues.get(prevWord);
        double prob=(double)(v+1)/(Vocabulary.get(prevWord)+Vocabulary.size());
        return prob;
        
    }
    
    public double getBigramGoodTuringProbability(String word,String prevWord)
    {
        double cD=BigramCountTable.get(word).columnValues.get(prevWord);
        int c = (int) cD;
        if(bigramCountMap.get(c+1)!=null)
        {
            double cStar=(double)(c+1)*bigramCountMap.get(c+1)/bigramCountMap.get(c);
            return (double)cStar/bigramCount;
        }
            
        return 0.0;
    }
    
    public void printTable(Map<String,ColumnData> input) {
        System.out.format("%-30s", "");
        List<String> keys = new LinkedList<>();
        keys.addAll(input.keySet());
        for(String key : input.keySet())
        {
            System.out.format("%-30s", key);
            //System.out.print("\t\t");
        }
        System.out.println("");
        for(String key : input.keySet()){
            
            //System.out.print(key+"\t\t");
            System.out.format("%-30s", key);
            //System.out.print("\t\t");
            for(int i=0;i<input.keySet().size(); i++){
                String w = keys.get(i);
                String val = Double.toString(input.get(w).columnValues.get(key));
                //if(val.length()>7)
                //    System.out.format("%-15s", val.substring(0, 7));
                //else
                    System.out.format("%-30s", val);
                //System.out.printf("%.5f",input.get(w).columnValues.get(key));
                //System.out.print("\t\t");
            }
            System.out.println("");
        }
        
    }
    
    public void constructBigramCountTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramProbTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],BigramCountTable.get(segments[i]).columnValues.get(segments[j]));
            }
            sentenceBigramProbTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramProbTable);
    
        
    }
    
    public void constructBigramProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
    public void constructBigramSmoothingProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramSmoothingProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
    public void constructBigramGoodTuringProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramGoodTuringProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
    public double calculateTotalProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            else
            {
                double p = getBigramProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            
        }
        return totalProb;
    }
    
    public double calculateTotalSmoothingProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramSmoothingProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            else
            {
                double p = getBigramSmoothingProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            
        }
        return totalProb;
    }
    
    public double calculateGoodTuringProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramGoodTuringProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            else
            {
                double p = getBigramGoodTuringProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
                else
                {
                    //handle this
                }
            }
            
        }
        return totalProb;
    }
    
    public static void main(String args[])
    {
        String sentence1 = "The company chairman said he will increase the profit next year .";
        Bigram b=new Bigram();
        b.ExtractData("/Users/bhumikasaivamani/corpus.txt");
        b.calculateBigramCounts("/Users/bhumikasaivamani/corpus.txt");
        b.constructBigramCountTable(sentence1);
        b.constructBigramProbabilitiesTable(sentence1);
        System.out.println("Total Probability : "+b.calculateTotalProbability(sentence1));
        
        
        b.constructBigramSmoothingProbabilitiesTable(sentence1);
        System.out.println("Total Probability : "+b.calculateTotalSmoothingProbability(sentence1));
        
        b.constructBigramGoodTuringProbabilitiesTable(sentence1);
        System.out.println("Total Probability : "+b.calculateGoodTuringProbability(sentence1));
        //System.out.println(b.getBigramProbability("the","from"));
        //b.calculateBigramProbabilities();
        /*Set cols = new HashSet<>();
        cols.add("the");
        cols.add("car");
        cols.add("plane");cols.add("he");cols.add("will");
        cols.add("not");
        cols.add("eat");
        
        
        Map<String,ColumnData> testMap = new HashMap<>();
        testMap.put("the", new ColumnData(cols));
        testMap.put("car", new ColumnData(cols));
        testMap.put("plane", new ColumnData(cols));
        testMap.put("he", new ColumnData(cols));
        testMap.put("will", new ColumnData(cols));
        testMap.put("not", new ColumnData(cols));
        testMap.put("eat", new ColumnData(cols));
        
        b.printTable(testMap);
        */
    }
    
}