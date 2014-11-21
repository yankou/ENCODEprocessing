package ChipSeqProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;

/*
 * read significant peak files and construct peak<->gene maps
 * input: 1. refFlat of selected genomes; 
 * 		  2. significant peaks in .bed file
 * output: 1. [peak, gene, disToTSS]
 * 		   2. [gene, #ofPeaks, peakInfo]
 * 		   3. [gene, bins]
 */

public class peak2GeneBatch {
	
	private static int upCut = 10000;
	private static int downCut = 5000;	
	@SuppressWarnings("unused")
	private static int binStep = 5;
	private static int totalTag = 100000000;
	private static String genome = "mm9";
	private String outputPath = "maps/mouse/";
	
	
	public static void main(String[] args){
		
//		upCut = Integer.parseInt(args[0]);
//		downCut = Integer.parseInt(args[1]);
////		binStep = Integer.parseInt(args[2]);
//		totalTag = Integer.parseInt(args[2]);
//		genome = args[3];
		File ref = new File("genomes/"+genome+".txt");
//		File _bedFile = new File("bedFile/K562_gata2_rep1_peaks.subpeaks.bed");
		
//		File ref = new File("genomes/"+args[0]);
		File _bedFile[] = new File("bedFile/mouse/").listFiles();		
		
		peak2GeneBatch pg = new peak2GeneBatch();
		System.gc();
		
		for(File f:_bedFile){
			System.out.println("\n===============================\nmapping: "+f.getName());
			pg.peakMap(ref, f);
			pg.geneMap(ref, f);
		}		
//		pg.peakMap(ref, _bedFile);
//		pg.geneMap(ref, _bedFile);
		
		System.out.println("peak2Gene done.");
	}

	
	
	//create map of peaks from bed file with closest gene and distance to TSS
	public void peakMap(File refFile, File _bedFile){
		
		HashMap<String, HashMap<String, genomeMap>> chrGenoMap = new HashMap<String, HashMap<String, genomeMap>>();
		chrGenoMap = readGenome.getGeneCoord(refFile, upCut, downCut);
		
		HashMap<peak, HashMap<String, Integer>> peakmap = new HashMap<peak, HashMap<String, Integer>>();
		//for peakmap, key is peak; for secondary HashMap in peakmap, key is gene matched and value is the distance of the peak to TSS of that gene
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(_bedFile));
			String s="";
			int count=0;
			
			while((s=br.readLine())!=null){
				
				String temp[]=s.split("\t");
				if(!temp[0].equalsIgnoreCase("chrM")){
					peak newPeak = new peak(s.replace("\n", ""));
					count++;				
						
					HashMap<String, Integer> tempMatch = bestMatch(newPeak, chrGenoMap);
					if(!tempMatch.isEmpty()){
						peakmap.put(newPeak, tempMatch);					
					}
				}
			}
			System.out.println("number of peaks for matchintg: "+count);
			System.out.println("number of peaks matched: "+peakmap.size());
			br.close();
			
			String outputName=_bedFile.getName().replace(".bed", "_peakmap.txt");
			if(_bedFile.getName().contains("scoreisland")){
				outputName = _bedFile.getName().replace(".scoreisland", "_peakmap.txt");
			}			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath+outputName));
			
			for(peak pk : peakmap.keySet()){
				//System.out.println(pk.printEleNoDis());
				HashMap<String, Integer> mapPerPeak = peakmap.get(pk);				
				for(String gene : mapPerPeak.keySet()){
					bw.write(pk.printEleNoDis()+"\t"+gene+"\t"+mapPerPeak.get(gene)+"\n");
				}
			}
			bw.close();
			
			//geneMap(peakmap);			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	//create map of genes and all peaks within cutoff range of gene
	public void geneMap(File refFile, File _bedFile){
				
		HashMap<String, HashMap<String, genomeMap>> chrGenoMap = new HashMap<String, HashMap<String, genomeMap>>();
		chrGenoMap = readGenome.getGeneCoord(refFile, upCut, downCut);		
		HashMap<peak, HashMap<String, Integer>> peakmap = new HashMap<peak, HashMap<String, Integer>>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(_bedFile));
			String s="";
			int count=0;
			
			while((s=br.readLine())!=null){
				
				String temp[]=s.split("\t");
				if(!temp[0].equalsIgnoreCase("chrM")){
					peak newPeak = new peak(s.replace("\n", ""));
					count++;
					HashMap<String, Integer> tempMatch = bestMatch(newPeak, chrGenoMap);
					if(!tempMatch.isEmpty()){
						peakmap.put(newPeak, tempMatch);					
					}
				}
			}
			br.close();
			//System.out.println("number of peaks for matchintg: "+count);
			//System.out.println("number of peaks matched: "+peakmap.size());
		
		HashMap<String, HashSet<peak>> genemap = new HashMap<String, HashSet<peak>>();
		for(peak pk : peakmap.keySet()){
			HashMap<String, Integer> mapPerPeak = peakmap.get(pk);
			String peakinfo = pk.getChr()+"\t"+pk.getPeakStart()+"\t"+pk.getPeakEnd()+"\t"+pk.getReadCount();
			for(String gene : mapPerPeak.keySet()){				
//				if(gene.equalsIgnoreCase("SNHG11")|gene.equalsIgnoreCase("SNORA39")){
//					System.out.println(gene+"\t"+mapPerPeak.get(gene));
//					System.out.println(gene+"\t"+pk.getDisTSS());
//				}				
				peak peakhit = new peak(peakinfo);			
				
				
				if(pk.getDisTSS()!=mapPerPeak.get(gene)){
					
					peakhit.putTSS(mapPerPeak.get(gene));
					if(!genemap.containsKey(gene)){
						HashSet<peak> peakSet = new HashSet<peak>();
						
//						if(gene.equalsIgnoreCase("SNHG11")|gene.equalsIgnoreCase("SNORA39")){
//							System.out.println(gene+"\t"+mapPerPeak.get(gene));
//							System.out.println(gene+"\t"+peakhit.getDisTSS());
//						}
						peakSet.add(peakhit);
						genemap.put(gene, peakSet);
						
					}
					else{
						HashSet<peak> peakSet = genemap.get(gene);
						//pk.putTSS(mapPerPeak.get(gene));
						peakSet.add(peakhit);
						genemap.put(gene, peakSet);					
					}
				}
				
				else{
					if(!genemap.containsKey(gene)){
						HashSet<peak> peakSet = new HashSet<peak>();
						peakSet.add(peakhit);
						genemap.put(gene, peakSet);
					}
					else{
						HashSet<peak> peakSet = genemap.get(gene);
						//pk.putTSS(mapPerPeak.get(gene));
						peakSet.add(peakhit);
						genemap.put(gene, peakSet);										
					}
				}
			}
		}
		System.out.println("number of gene peaked: "+genemap.size());
		
		String outputName=_bedFile.getName().replace(".bed", "_genemap.txt");
		if(_bedFile.getName().contains("scoreisland")){
			outputName = _bedFile.getName().replace(".scoreisland", "_genemap.txt");
		}	
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath+outputName));
		
		for(String gene : genemap.keySet()){
			HashSet<peak> peakSet = genemap.get(gene);
			
			for(peak pk1:peakSet){
				String output = gene+"\t"+peakSet.size()+"\t";
				String peakout = pk1.printEle();
//				
//				if(gene.equalsIgnoreCase("SNHG11")|gene.equalsIgnoreCase("SNORA39")){
//					System.out.println(gene+"\t"+pk1.getDisTSS());
//					//System.out.println(gene+"\t"+peakhit.getDisTSS());
//				}				
				bw.write(output+peakout+"\n");
			}
		}		
		bw.close();
		
		//drawBins(chrGenoMap, genemap, _bedFile.getName(), binStep);
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//get all matched genes to input peak = get all genes within cutoff range of input peak, return a hashmap
	public HashMap<String, Integer> bestMatch(peak _newPeak, HashMap<String, HashMap<String, genomeMap>> _chrGenoMap){
		
		HashMap<String, HashMap<String, genomeMap>> chrGenoMap = _chrGenoMap;
		HashMap<String, Integer> geneDis = new HashMap<String, Integer>();
		peak newPeak = _newPeak;
		//System.out.println(newPeak.printTestEle());
		
		String chr = newPeak.getChr();
		int peakStart = newPeak.getPeakStart();
		int peakEnd = newPeak.getPeakEnd();
		//int peakLength = newPeak.getPeakLength();
		int anchor = peakStart+(peakEnd-peakStart+1)/2;
		
		
		HashMap<String, genomeMap> genePerChr = chrGenoMap.get(chr.toUpperCase());
		
		for(String gene : genePerChr.keySet()){
			
			genomeMap singleGene = genePerChr.get(gene);
			int txStart = singleGene.getTxStart();
			int txEnd = singleGene.getTxEnd();
			int txStartEx = singleGene.getStExtend();
			int txEndEx = singleGene.getEndExtend();			
						
			if(singleGene.getSign()=='+'){
				//criteria: peak has overlapping region with <upCut, gene, downCut> range
				if(!(peakEnd<txStartEx | peakStart>txEndEx)){
					geneDis.put(gene, (anchor-txStart));
				}
			}
			
			else if(singleGene.getSign()=='-'){
				//criteria: peak has overlapping region with <upCut, gene, downCut> range
				if(!(peakEnd<txEndEx | peakStart>txStartEx)){
					//CAUTION: for '-' strand, the TSS information is stored in txStart field; refer to readGenome for details
					geneDis.put(gene, (txEnd-anchor));
				}
			}
			
			else{
				System.out.println("Gene not mappable (not having strand information): "+gene);
			}			
		}
		
		
		return geneDis;
	}
	
	public void drawBins(HashMap<String, HashMap<String, genomeMap>> _chrGenoMap, HashMap<String, HashSet<peak>> _genemap, String _outputName, int _binStep){
		
		//write bin information into output file. Can be used to plot tagDensity/DisTSS figure
		HashMap<String, HashMap<String, genomeMap>> chrGenoMap = _chrGenoMap;
		HashMap<String, HashSet<peak>> genemap = _genemap;
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath+_outputName.replace("Peak", "_binmap.txt")));
			
			for(String gene:genemap.keySet()){
				bw.write(gene+"\t");
				String chr="";
				//first construct bins as an array;
				int txStart=0;
				int txEnd=0;
				int txStartEx=0;
				int txEndEx=0;
				int allLength=0;
				int bodyLength=0;
				char sign=' ';
				
				for(String chrIndex:chrGenoMap.keySet()){
					if(chrGenoMap.get(chrIndex).containsKey(gene)){
						chr = chrIndex;
						genomeMap geneIndex = chrGenoMap.get(chrIndex).get(gene);
						txStart = geneIndex.getTxStart();
						txEnd = geneIndex.getTxEnd();
						txStartEx = geneIndex.getStExtend();
						txEndEx = geneIndex.getEndExtend();
						allLength = Math.abs(txStartEx-txEndEx);
						sign = geneIndex.getSign();
						bodyLength = Math.abs(txStart-txEnd);
					}
				}
				bw.write(bodyLength+"\t");
				
				int binSize = allLength/_binStep +1;
				Integer[] bin = new Integer[binSize];
				
				//if bodyLength%_binStep!=0, the bin next to TES will be different size
				int caution=Integer.MAX_VALUE;
				if(bodyLength%_binStep!=0){
					caution = Math.abs(txStartEx-txStart)/_binStep + bodyLength/_binStep-1;
				}
				
				//construct index for bin translating into genome coordinate
				HashMap<Integer, Integer> binKey = new HashMap<Integer, Integer>();				
				if(sign=='+'){
					for(int i=0;i<bin.length;){											
						binKey.put(txStartEx+i*_binStep,i);
						if(i>caution){
							binKey.put(txEnd+(i-caution-1)*_binStep, i);
						}
					}
				}
				else{
					for(int i=0;i<bin.length;){											
						binKey.put(txStartEx-i*_binStep, i);
						if(i>caution){
							binKey.put(txStart-(i-caution-1)*_binStep, i);
						}
					}
				}
				
				//put readCount of each peak into gene bin
				for(peak pk:genemap.get(gene)){
					if(!chr.equalsIgnoreCase(pk.getChr())){
						System.out.println("ERROR: chr is not consistent in gene and peaks: "+gene+"-"+pk.printEleNoDis());
					}
					else{
						int pkStart = pk.getPeakStart();
						int pkEnd = pk.getPeakEnd();
						int readCount = pk.getReadCount();
						int binNum = pk.getPeakLength()/_binStep+1;
						
						validRegion valid = findValid(txStartEx, txEndEx, pkStart, pkEnd);						
						
						if(sign=='+'){
							
							int binStartRm = (valid.ini-txStartEx)%_binStep;
							int binStartInx = binKey.get(valid.ini-binStartRm);
							int binEndRm = (valid.end-txStartEx)%_binStep;
							int binEndInx = binKey.get(valid.end-binEndRm);
							
							if(binEndInx>=binStartInx){
								int ini = readCount;
								int end = readCount;
								
								//for peaks not starting or ending exactly at the bin edge, unify the tag read count according to proportion of the exact position 
								//inthe bin
								if(binStartRm!=0){
									ini = readCount*(_binStep-binStartRm)/_binStep;
								}
								if(binEndRm!=0){
									end = readCount*binEndRm/_binStep;
								}
								
								bin[binStartInx]+=ini;
								bin[binEndInx]+=end;
								
								for(int i=1;i<(binEndInx-1);i++){
									bin[binStartInx+i]+=readCount;
								}
								
							}
							else{
								System.out.println("ERROR: invalid peak: "+pk.printEle());
							}
						}
						
						else{
							txStartEx = txEndEx;
							int binStartRm = _binStep-(txStartEx-valid.end)%_binStep;
							int binStartInx = binKey.get(valid.end+(txStartEx-valid.end)%_binStep);
							int binEndRm = (txStartEx-valid.ini)%_binStep;
							int binEndInx = binKey.get(valid.ini+binEndRm);
							
							if(binEndInx>=binStartInx){
								int ini = readCount;
								int end = readCount;
								
								//for peaks not starting or ending exactly at the bin edge, unify the tag read count according to proportion of the exact position 
								//inthe bin
								if(binStartRm!=0){
									ini = readCount*binStartRm/_binStep;
								}
								if(binEndRm!=0){
									end = readCount*binEndRm/_binStep;
								}
								
								bin[binStartInx]+=ini;
								bin[binEndInx]+=end;
								
								for(int i=1;i<(binEndInx-1);i++){
									bin[binStartInx+i]+=readCount;
								}
								
							}
							else{
								System.out.println("ERROR: invalid peak: "+pk.printEle());
							}
						}		
					}
				}
				
				//write the tag density per bin into output file.
				for(int i=0;i<bin.length;i++){
					double density = getTagDensity(bin[i], _binStep, totalTag);
					if(i==caution){
						density = getTagDensity(bin[i],bodyLength%_binStep,totalTag);
					}
					bw.write(String.format("%.2e", density)+",");
				}								
				bw.write("\n");
			}
			
			
			
			bw.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private double getTagDensity(int _tagNum, int _binStep, int _tagTotal){
		
		double tagDen=0.0;		
		tagDen = (double)_tagNum/(double)(_binStep*_tagTotal);	
		return tagDen;
	}
	
	private validRegion findValid(int _geneBinStart, int _geneBinEnd, int _pkStart, int _pkEnd){
		
		//String peakType="";
		int geneBinStart = _geneBinStart;
		int geneBinEnd = _geneBinEnd;
		int pkStart = _pkStart;
		int pkEnd = _pkEnd;
		
		int validIni=0;
		int validEnd=0;
		
		//half_upstream
		if(pkStart<geneBinStart && pkEnd>geneBinStart){
			validIni=geneBinStart;
			validEnd=pkEnd;		
		}
		
		//peak inside gene cutoffs
		else if(pkStart>=geneBinStart && pkEnd<=geneBinEnd){
			validIni=pkStart;
			validEnd=pkEnd;	
		}
		
		//half_downstream
		else if(pkStart>geneBinStart && pkStart<geneBinEnd && pkEnd>geneBinEnd){
			validIni=pkStart;
			validEnd=geneBinEnd;	
		}
		
		//gene inside peak
		else if(pkStart<geneBinStart && pkEnd>geneBinEnd){
			validIni=geneBinStart;
			validEnd=geneBinEnd;	
		}
		
		else{
			System.out.println("ERROR: other type of peak, checking needed: ["+_pkStart+","+_pkEnd+"]");
		}
		
		validRegion v = new validRegion(validIni, validEnd);
		return v;
	}
	
	
	
}
