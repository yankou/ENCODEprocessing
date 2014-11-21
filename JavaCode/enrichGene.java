package ChipSeqProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class enrichGene {

	/*
	 * find enriched genes from histone mofidication significant peaks
	 * input file: _genemap.txt
	 * output file: _eGene.txt
	 * user input: up/downstream distance to TSS
	 */
	
	public static void main(String[] args){
		
		enrichGene eg = new enrichGene();
		
//		String userFileInput = args[0];
//		int upCut = Integer.parseInt(args[1]);
//		int downCut = Integer.parseInt(args[2]);
		
		String userFileInput="maps/mouse/";
		int upCut = -2000;
		int downCut = 2000;
		
		if(userFileInput.contains("/")){
			System.out.println(userFileInput+" is a directory.\n");			
			File _mapFile[] = new File(userFileInput).listFiles();	
			for(File f:_mapFile){
//				if(f.getName().contains("genemap")&& (f.getName().contains("H3K36me3"))){
//					System.out.println("Finding H3k36me3 enriched genes for: "+f.getName());
//					eg.findEgene(userFileInput+f.getName(), upCut, downCut);
//				}
				
				if(f.getName().contains("genemap")){
					System.out.println("Finding enriched genes for: "+f.getName());
					eg.findEgene(userFileInput+f.getName(), upCut, downCut);
				}
			}
			
		}
		else{
			System.out.println(userFileInput+" is a file.\nFinding enriched genes...\n");
			eg.findEgene(userFileInput, upCut, downCut);
		}
		
//		String _testFile = "maps/GSM669897_BI.HUES6.H3K27me3.Lib_XZ_20101004_11--ChIP_XZ_20100930_11_ES_cell_HUES_6_H3K27Me3_sd-W500-G1500-FDR.001-island_genemap.txt";
//		eg.findEgene(_testFile, upCut, downCut);
		
		System.out.println("find enriched genes done.");
	}
	
	
	public void findEgene(String _mapFile, int _upCutoff, int _downCutOff){
		
		HashMap<String, HashMap<Integer, Integer>> enrichedGenes = new HashMap<String, HashMap<Integer, Integer>>();
		int upCutoff = _upCutoff;
		int downCutoff = _downCutOff;
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(_mapFile));
			String output = _mapFile.replace("_genemap.txt", "_eGene.txt");
			if(output.contains("/")){
				String temp[] = output.split("/");
				output = temp[2];
			}			
			System.out.println(output);
			BufferedWriter bw = new BufferedWriter(new FileWriter("enrichGene/"+output));
			
			String s;
			int line=0;		
			while ((s = br.readLine()) != null) {
				line++;
				//System.out.println(s);
				String[] sp = s.split("\t"); //could be changed to "\t" or any other seperator;
				String[] peak = sp[2].split(",");
				
				if(peak.length!=7){
					System.out.println("line: "+line);
				}
				
				
				HashMap<Integer, Integer> distance = new HashMap<Integer, Integer>();
				if(enrichedGenes.containsKey(sp[0].toUpperCase())){
					distance = enrichedGenes.get(sp[0].toUpperCase());
					distance.put(Integer.parseInt(peak[6]), Integer.parseInt(peak[3]));				
					enrichedGenes.put(sp[0].toUpperCase(), distance);
				}
				else{
					distance.put(Integer.parseInt(peak[6]), Integer.parseInt(peak[3]));			
					enrichedGenes.put(sp[0].toUpperCase(), distance);
				}
			}
			br.close();			
			System.out.println("num of unique genes peaked: "+enrichedGenes.size());
			
			int count=0;
			for(String gene:enrichedGenes.keySet()){
				HashMap<Integer, Integer> distance = enrichedGenes.get(gene);
				int dis = 10000000;
				boolean findHit = false;
				
				for(int d2tss:distance.keySet()){
					//int dis = 10000000;
					if(d2tss<=downCutoff && d2tss>=upCutoff){
						findHit = true;						
						dis = closer(d2tss, dis);						
					}
					else{
						if(d2tss<0){
							int cover = d2tss + distance.get(d2tss)/2;
							if(cover>=upCutoff){
								findHit = true;
								if(dis!=10000000){
									dis = closer(d2tss, dis);
								}
								else{
									dis = closer(d2tss, dis);
								}
								
							}
						}
						if(d2tss>0){
							int cover = d2tss - distance.get(d2tss)/2;
							if(cover<=downCutoff){
								findHit = true;
								if(dis!=10000000){
									dis = closer(d2tss, dis);
								}
								else{
									dis = closer(d2tss, dis);
								}
							}
						}
					}
				}
				if(findHit){
					count++;
					bw.write(gene+"\t"+dis+"\n");
				}
			}
			System.out.println("num of enriched genes found: "+count);
			bw.close();
			
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int closer(int a, int b){
		
		int dis=0;		
		if(a>=0 && b>=0){
			dis = Math.min(a,b);
		}
		
		if((a>0&&b<0)|(a<0&&b>0)){			
			if(Math.abs(a)<=Math.abs(b)){
				dis=a;
			}
			else{
				dis=b;
			}
		}
		
		if(a<=0 && b<=0){
			dis = Math.max(a, b);
		}		
		return dis;
	}
	
	
}
