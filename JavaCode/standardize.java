package ChipSeqProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * 1. center mapped tags from .bed file by +/-73
 * 2. convert .bed files to standard bed file format: chrM start end XX 0 +/-
 * 
 */

public class standardize {

	public static void main(String[] args){
		
		standardize sd = new standardize();
//		String inpath = args[0];
//		String outpath = args[1];
		
		String inpath = "/home/yan/Documents/dan/atrx";
		String outpath = "/home/yan/Documents/dan/atrx/";
		
		File[] _bedFile = new File(inpath+"/").listFiles();
		
		for(File f:_bedFile){
			if(f.isFile()&&f.getName().endsWith(".bed")){
				System.out.println("start standardizing: "+f.getName());
				sd.run(f, outpath);
//				f.delete();
			}
		}
		System.out.println("standardizing done.");
	}
	
	
	public void run(File _bedFile, String outpath){
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(_bedFile));
			
			String tempFile = _bedFile.getName().replace(".bed", "_sd.bed");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outpath+tempFile));
			
			String s;
			int count=0;
						
			while ((s = br.readLine()) != null) {
				
				//System.out.println(s);
				String[] sp = s.split("\t"); //could be changed to "\t" or any other seperator;				
				int tempStart;
				int tempEnd;
				String output="";
				tempStart = Integer.parseInt(sp[1]);
				tempEnd = Integer.parseInt(sp[2]);
				
				int sign=0;
				for(int i=0;i<sp.length;i++){
					if(sp[i].equals("+")|sp[i].equals("-")){
						sign=i;
					}
				}
				
				//for sense strand
//				if (sp[sign].equals("+")) { 
//					
//					//System.out.print("old start: "+tempStart+"->");
//					tempStart = tempStart+73;	
//					tempEnd = tempEnd+73;
//					//System.out.println("new start: "+tempStart);
//					}
//				
//				//for antisense strand
//				else if(sp[sign].equals("-")){
//					tempStart = tempStart-73;
//					tempEnd = tempEnd-73;
//				}
				
				if(sp[4].equals("+")|sp[4].equals("-")){
					output = sp[0]+"\t"+tempStart+"\t"+tempEnd+"\t"+sp[3]+"\t0\t"+sp[4]+"\n";
				}
				else{
					output = sp[0]+"\t"+tempStart+"\t"+tempEnd+"\t"+sp[3]+"\t"+sp[4]+"\t"+sp[5]+"\n";
				}				
				//System.out.print(output);
				bw.write(output);
				count++;				
			}
			System.out.println("number of lines in bed file: "+count);
			br.close();
			bw.close();
			
		}
//		catch(NumberFormatException e1){
//			System.out.println("wrong format");
//		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
