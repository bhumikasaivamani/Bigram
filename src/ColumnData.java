
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bhumikasaivamani
 */
public class ColumnData 
{
    Map<String,Double> columnValues;
    //Map<String,Double> columnProbabilities;
    public ColumnData(Set<String> words)
    {
       columnValues=new HashMap<String,Double>();
       //columnProbabilities=new HashMap<String,Double>();
       for(String w: words)
       {
           columnValues.put(w,0.0);
           //columnProbabilities.put(w,0.0);
       }
       
    }
    
}
