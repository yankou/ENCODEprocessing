package validation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
public class sortGene2 {

	//private static int top = 2000;
	private static int promotor = 2000;
	boolean roundup = true;
	
	public static void main(String[] args){
		
		sortGene2 sg = new sortGene2();		
		
		File[] _scoreFile = new File("enrichGene/").listFiles();
//		System.out.println("sorting: "+_scoreFile.getName());			
//		sg.sort(_scoreFile, top);		
		
		for(File _f : _scoreFile){
			
			System.out.println("sorting: "+_f.getName());			
			if(_f.getName().contains("eGene.txt")){
				sg.sortDis(_f, /*top,*/ promotor);
			}
			else{sg.sort(_f/*, top*/);}			
		}		
		System.out.println("All score files done.");		
	}
	
	
	public void sort(File _scoreFile/*, int top*/){
		
		HashMap<String, Double> scoreMap = new HashMap<String, Double>();
		
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(_scoreFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter("output/"+_scoreFile.getName().replace(".txt", "_top.txt")));
			
			String s="";
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				if(!scoreMap.containsKey(temp[0].toUpperCase())){
					scoreMap.put(temp[0].toUpperCase(), Double.parseDouble(temp[1]));
				}
				else{System.out.println("duplicate eGene: "+temp[0]);}
			}
			System.out.println("num of enriched genes: "+scoreMap.size());
			br.close();
			
			
			/*
			 * check how many values are duplicated
			 */
			HashSet<Double> dupli = new HashSet<Double>();
			int dup=0;
			for(String gene: scoreMap.keySet()){
				if(!dupli.contains(scoreMap.get(gene))){
					dupli.add(scoreMap.get(gene));
				}
				else{
					dup++;
					//System.out.println("checking dupli: "+gene+","+scoreMap.get(gene));
				}
			}
			System.out.println("num of duplicated score value: "+dup);
			
			
			List<Map.Entry<String, Double>> entries = sortByValue(scoreMap);
			//System.out.println(entries.size());
			
			ListIterator<Entry<String, Double>> it = entries.listIterator();
			LinkedHashMap<String, Double> sorted = new LinkedHashMap<String, Double>();
			while(it.hasNext()){
				String gene = it.next().getKey();
				sorted.put(gene, scoreMap.get(gene));
				
			}
			System.out.println("num of genes after sorting: "+sorted.size());
			
			int top=0;
			if(sorted.size()>=2000){
				top=2000;
			}
			else{
				top=sorted.size();
			}
			
			
			List<String> keyList = new ArrayList<String>(sorted.keySet());
			int topcount=0;
			
			
			for(int i=(sorted.size()-1);i>=(sorted.size()-top);i--){
				
				bw.write(keyList.get(i)+"\t"+String.format("%.4e", scoreMap.get(keyList.get(i)))+"\n");
				topcount++;
				//System.out.println(keyList.get(i)+"\t"+String.format("%.4e", scoreMap.get(keyList.get(i))));
				
			}
			
			System.out.println("top "+topcount+" genes are selected.");
			
			
//			ListIterator<String> is = keyList.listIterator();
//			int topcount=1;
//			while(is.hasPrevious()){
//				String genekey = is.previous();
//				System.out.println(genekey+"\t"+String.format("%.4e", scoreMap.get(genekey)));
//				while(topcount<=top){
//					
//					bw.write(genekey+"\t"+String.format("%.4e", scoreMap.get(genekey)));
//					topcount++;
//					System.out.println(genekey+"\t"+String.format("%.4e", scoreMap.get(genekey)));
//				}
//			}

			
//			Double[] score = scoreMap.values().toArray(new Double[0]);     
//			Arrays.sort(score);
//			
//			Object[] genes = scoreMap.keySet().toArray();
//			HashSet<String> topGene = new HashSet<String>();
//			
//			int topcount=1;
//			for(int i=0;i<score.length;i++){
//				
//				while(topcount<=top){
//					for(int j=0;j<genes.length;){
//						if(scoreMap.get(genes[j].toString())==score[i]){
//							
//							if(!topGene.contains(genes[j].toString())){
//								bw.write(genes[j]+"\t"+String.format("%.4e", score[i]));
//								j++;
//								topcount++;
//								topGene.add(genes[j].toString());
//							}
//							else{
//								j++;
//							}
//						}
//					}
//				}
//				
//			}
			bw.close();
			
		}catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	
	public void sortDis(File _eGeneFile/*, int top*/, int promotor){
		
		HashMap<String, Double> scoreMap = new HashMap<String, Double>();	
		int top = 2000;
		
		try{			
			String output = "_top";
			if(roundup){
				output = "rd_top";
			}
			BufferedReader br = new BufferedReader(new FileReader(_eGeneFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter("output/"+_eGeneFile.getName().replace(".eGene.txt", output+".txt")));
			if (!_eGeneFile.getName().contains("0_0")){
				top = 3000;
			}
			
			String s="";
			
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				if(!scoreMap.containsKey(temp[0].toUpperCase())){
					scoreMap.put(temp[0].toUpperCase(), Double.parseDouble(temp[1]));
				}
				else{System.out.println("duplicate eGene: "+temp[0]);}
			}
			System.out.println("num of enriched genes: "+scoreMap.size());
			br.close();			
			
			/*
			 * round up distance to +/- promotor length and push to absolute value
			 */
			
			for(String gene: scoreMap.keySet()){
				double dis = scoreMap.get(gene);
				
				if(roundup){
					if(dis>2000){
						dis = 2000;
					}
					else if(dis<0 && dis>=((-1)*2000)){
						dis = (-1)*dis;
					}
					else if(dis<((-1)*2000)){
						dis = 2000;
					}
					scoreMap.put(gene, dis);
				}				
				else{
					if(dis<0){
						dis = (-1)*dis;
					}
					scoreMap.put(gene, dis);
				}				
			}			
			
			/*
			 * check how many values are duplicated
			 */
			HashSet<Double> dupli = new HashSet<Double>();
			int dup=0;
			for(String gene: scoreMap.keySet()){
				if(!dupli.contains(scoreMap.get(gene))){
					dupli.add(scoreMap.get(gene));
				}
				else{
					dup++;
					//System.out.println("checking dupli: "+gene+","+scoreMap.get(gene));
				}
			}
			System.out.println("num of duplicated score value: "+dup);
			
			//System.out.println(scoreMap.get("SNORA69"));
			
			
			List<Map.Entry<String, Double>> entries = sortByValue(scoreMap);
			//System.out.println(entries.size());
			
			ListIterator<Entry<String, Double>> it = entries.listIterator();
			LinkedHashMap<String, Double> sorted = new LinkedHashMap<String, Double>();
			while(it.hasNext()){
				String gene = it.next().getKey();
				sorted.put(gene, scoreMap.get(gene));
				
			}
			System.out.println("num of genes after sorting: "+sorted.size());
			
			List<String> keyList = new ArrayList<String>(sorted.keySet());
			int topcount=0;
			
//			int top=0; //need to be delete when *top* is effective
//			if(sorted.size()>=2000){
//				top=2000;
//			}
//			else{
//				top=sorted.size();
//			}
			
			if(sorted.size()<top){
				top = sorted.size();
			}
			
			System.out.println("taking the top "+top+" genes. ");
			for(int i=0;i<top;i++){
				
				bw.write(keyList.get(i)+"\t"+String.format("%.4e", scoreMap.get(keyList.get(i)))+"\n");
				topcount++;
				//System.out.println(keyList.get(i)+"\t"+String.format("%.4e", scoreMap.get(keyList.get(i))));
			}			
			System.out.println("top "+topcount+" genes are selected.");	
			bw.close();		
			
		}catch (IOException e) {
		    e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unchecked")
    public static <K, V extends Comparable> List<Map.Entry<K, V>> sortByValue(
        Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.size());
        entries.addAll(map.entrySet());
        Collections.sort(entries,
            new Comparator<Map.Entry<K, V>>() {
                public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                    return e1.getValue().compareTo(e2.getValue());
                }
            });

        return entries;
    }
}
