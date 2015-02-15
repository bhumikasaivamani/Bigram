
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
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
    Map<String,ColumnData> BigramProbabilityTable;
    int bigramCount;
    
    public Bigram()
    {
        bigramCount=0;
        Vocabulary=new HashMap<String,Integer>();
        BigramCountTable=new HashMap<String,ColumnData>();
       // BigramProbabilityTable=new HashMap<String,ColumnData>();
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
                   if(word.length()==1)
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
                   
                   if(word.equals("."))
                   {
                       previousWord="";
                       continue;
                   }
                   if(word.length()==1)
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
        System.out.println(BigramCountTable.get("monday").columnValues.get("closed"));
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
    
    /*public void calculateBigramProbabilities()
    {
        int vCount=VocabularyWordCount();
        for(String word:BigramCountTable.keySet())
        {
            for(String prevWord:BigramCountTable.get(word).columnValues.keySet())
            {
                int v=BigramCountTable.get(word).columnValues.get(prevWord);
                if(v>0)
                {
                    double prob=(double)v/Vocabulary.get(prevWord);
                    BigramProbabilityTable.get(word).columnProbabilities.put(prevWord, prob);
                }
            }
        }
        System.out.println(BigramProbabilityTable.get("monday").columnProbabilities.get("closed"));
    }*/
    public static void main(String args[])
    {
        Bigram b=new Bigram();
        b.ExtractData("/Users/bhumikasaivamani/corpus.txt");
        b.calculateBigramCounts("/Users/bhumikasaivamani/corpus.txt");
        //b.calculateBigramProbabilities();
    }
    
}