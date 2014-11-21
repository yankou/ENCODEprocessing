package ChipSeqProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/*
 * read genome coordinates for each gene from refFlat file
 * all coordinates including extentions are consistent with genome browser, for both +/- strands
 * define length of extention to upStream and downStream as parameter setting
 */

/************************************************************************************
 * NOTE: FOR '-' STRAND, TSS AND TES IS AS INDICATED IN GENOME FILE;                *
 * 					BUT! TSS_EXTENTION AND TES_EXTENTION ARE MAPPED TO COORDINATES  *
 * THEREFORE!!! FOR '-' STRAND, THE ACTUAL TSS IS THE READ-OUT OF "txEND"           *
 ************************************************************************************ 
 */

public class readGenome {
	
//	public static void main(String[] args){
//		
//		readGenome rg = new readGenome();
//		System.gc();
//		File _genomeFile = new File("genomes/hg19_2012jan.txt");
//		rg.getGeneCoord(_genomeFile, 10000, 5000);
//		
//	}
	
	
	public static HashMap<String, HashMap<String, genomeMap>> getGeneCoord(File _genomeFile, int upStream, int downStream){
		
		HashMap<String, HashMap<String, genomeMap>> chrGeneMap = new HashMap<String, HashMap<String, genomeMap>>();		
		HashMap<String, geneLocation> uniqueTSS = new HashMap<String, geneLocation>();
		
		//use the TSS of longest alternative splice for mapping peaks
		try{
			BufferedReader br = new BufferedReader(new FileReader(_genomeFile));
			String s="";
			int count=0;
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				count++;
				
				geneLocation loc = new geneLocation();
				loc.putChr(temp[2].toUpperCase());
				loc.putStart(Integer.parseInt(temp[4]));
				loc.putEnd(Integer.parseInt(temp[5]));
				loc.putSign(temp[3].charAt(0));
				
				if(!uniqueTSS.containsKey(temp[0].toUpperCase())){			
					uniqueTSS.put(temp[0].toUpperCase(), loc);
				}
				
				else{
					geneLocation oldLoc = uniqueTSS.get(temp[0].toUpperCase());					
					int newLength = loc.getLength();
					int oldLength = oldLoc.getLength();
					if(newLength>oldLength){
						uniqueTSS.put(temp[0].toUpperCase(), loc);
					}					
				}
				
				//testing multiple TSS
//				if(temp[0].contains("EZH2")){
//					System.out.println(loc.printEle());
//				}
				
				
			}
			//System.out.println("unique TSS from genome: "+uniqueTSS.size());
			//System.out.println(uniqueTSS.get("EZH2").printEle());
			
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
		//construct chr-gene-tss genome map based on TSS of longest splice
		try{
			BufferedReader br = new BufferedReader(new FileReader(_genomeFile));
			String s="";
			int count=0;
			
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				count++;
				
				if(uniqueTSS.containsKey(temp[0].toUpperCase())){
					
					//find the longest splice for genes having multiple TSS
					boolean longest = false;
					if(temp[3].contains("+")){
						if(uniqueTSS.get(temp[0].toUpperCase()).getTxStart()==Integer.parseInt(temp[4])){
							longest = true;
						}
					}
					else{
						if(uniqueTSS.get(temp[0].toUpperCase()).getTxEnd()==Integer.parseInt(temp[5])){
							longest = true;
						}
					}
					
					if(uniqueTSS.get(temp[0].toUpperCase()).getChr().equalsIgnoreCase(temp[2]) && longest){
						genomeMap gm = new genomeMap();
						gm.putSign(temp[3].charAt(0));
						gm.putTxStart(Integer.parseInt(temp[4]));
						gm.putTxEnd(Integer.parseInt(temp[5]));
							
						if(temp[3].contains("+")){
							gm.putStExtend(Integer.parseInt(temp[4])-upStream);
							gm.putEndExtend(Integer.parseInt(temp[5])+downStream);
						}
						
						else{
							//unify the coordinates if genome files has been corrected for "-" strand
							int _start = Math.max(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
							int _end = Math.min(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
							
							gm.putStExtend(_start+upStream);
							gm.putEndExtend(_end-downStream);
						}
						
						if(chrGeneMap.containsKey(temp[2].toUpperCase())){
							chrGeneMap.get(temp[2].toUpperCase()).put(temp[0], gm);
						}
						else{
							HashMap<String, genomeMap> geneMap = new HashMap<String, genomeMap>();
							geneMap.put(temp[0], gm);
							chrGeneMap.put(temp[2].toUpperCase(), geneMap);					
						}
					}
				}
				
				/*
				 * if use temp[1] ==> use entrez ID as identifier, e.g: NM_1548973
				 */
//				genomeMap gm = new genomeMap();
//				gm.putSign(temp[3].charAt(0));
//				gm.putTxStart(Integer.parseInt(temp[4]));
//				gm.putTxEnd(Integer.parseInt(temp[5]));
//					
//				if(temp[3].contains("+")){
//					gm.putStExtend(Integer.parseInt(temp[4])-upStream);
//					gm.putEndExtend(Integer.parseInt(temp[5])+downStream);
//				}
//				
//				else{
//					//unify the coordinates if genome files has been corrected for "-" strand
//					int _start = Math.max(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
//					int _end = Math.min(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
//					
//					gm.putStExtend(_start+upStream);
//					gm.putEndExtend(_end-downStream);
//				}
//				
//				if(chrGeneMap.containsKey(temp[2])){
//					chrGeneMap.get(temp[2]).put(temp[1], gm);
//				}
//				else{
//					HashMap<String, genomeMap> geneMap = new HashMap<String, genomeMap>();
//					geneMap.put(temp[1], gm);
//					chrGeneMap.put(temp[2], geneMap);
//				}
								
			}
			//System.out.println("lines of genome file: "+count);
			
			int geneCount=0;
			for(String chr : chrGeneMap.keySet()){
				HashMap<String, genomeMap> geneMap = chrGeneMap.get(chr);
				geneCount += geneMap.size();				
			}
			
			//System.out.println("unique gene entries in genome: "+geneCount);
			//System.out.println(chrGeneMap.get("chr6").get("ANKRD6").printEle());
						
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return chrGeneMap;
	}
	
	
	public static HashMap<String, geneLocation> getUniqueTSS(File _genomeFile){
		
		HashMap<String, geneLocation> uniqueTSS = new HashMap<String, geneLocation>();
		
		//use the TSS of longest alternative splice for mapping peaks
		try{
			BufferedReader br = new BufferedReader(new FileReader(_genomeFile));
			String s="";
			int count=0;
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				count++;
				
				geneLocation loc = new geneLocation();
				loc.putChr(temp[2].toUpperCase());
				loc.putStart(Integer.parseInt(temp[4]));
				loc.putEnd(Integer.parseInt(temp[5]));
				loc.putSign(temp[3].charAt(0));
				
				if(!uniqueTSS.containsKey(temp[0].toUpperCase())){			
					uniqueTSS.put(temp[0].toUpperCase(), loc);
				}
				
				else{
					geneLocation oldLoc = uniqueTSS.get(temp[0].toUpperCase());					
					int newLength = loc.getLength();
					int oldLength = oldLoc.getLength();
					
//					if(temp[0].contains("NUDCD1")){
//						System.out.println("testing transcript length: "+newLength+","+oldLength);
//					}
					
					if(newLength>oldLength){
						uniqueTSS.put(temp[0].toUpperCase(), loc);
					}
					
//					if(temp[0].contains("NUDCD1")){
//						System.out.println("testing transcript length: "+uniqueTSS.get("NUDCD1").getLength()+","+uniqueTSS.get("NUDCD1").getTxEnd());
//					}
					
				}
				
				//testing multiple TSS
//				if(temp[0].contains("EZH2")){
//					System.out.println(loc.printEle());
//				}
				
				
			}
			System.out.println("unique TSS from genome: "+uniqueTSS.size());
			//System.out.println(uniqueTSS.get("EZH2").printEle());
			
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
		return uniqueTSS;
	}
	
	
	
	
	
	
	
	
	
	
	//requires a very large memory space
	public static HashMap<String, HashMap<Integer, Double>> getEmptyBinLocal(File _genomeFile, int binSize, int upStream, int downStream){
		
		HashMap<String, HashMap<Integer, Double>> densityBin = new HashMap<String, HashMap<Integer, Double>>();
		
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(_genomeFile));
			String s="";
			int count=0;
						
			while((s=br.readLine())!=null){
				String temp[]=s.split("\t");
				count++;
				//unify the coordinates if genome files has been corrected for "-" strand
				int _start = Math.max(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
				int _end = Math.min(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
				
				int geneBody = _start-_end;
				int range = upStream+downStream+geneBody+1;
				int ini = -upStream;
				
				//construct bins with local coordinate; empty of tag density;
				if(!densityBin.containsKey(temp[0])){
					HashMap<Integer, Double> bins = new HashMap<Integer, Double>();
										
					for(int i=ini;i<range;){
						bins.put(i, 0.0);
						i+=binSize;
					}
					densityBin.put(temp[0], bins);				
				}
				//else{System.out.println("duplicated genes: "+temp[0]);}
				}
			
			System.out.println("lines of genome file: "+count);
			System.out.println("unique gene entries in genome: "+densityBin.size());
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return densityBin;
	}
	
	
	public HashMap<String, HashMap<String, HashMap<Integer, Double>>> getEmptyBinGlobal(File _genomeFile, HashMap<String, HashMap<Integer, Double>> localBin){
		
		HashMap<String, HashMap<String, HashMap<Integer, Double>>> globalBin = new HashMap<String, HashMap<String, HashMap<Integer, Double>>>();
		
		
		
		return globalBin;
	}
	
}
