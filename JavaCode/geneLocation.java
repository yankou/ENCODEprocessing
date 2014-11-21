package ChipSeqProcess;

public class geneLocation {
	String chr;
	int txStart;
	int txEnd;
	int bodyLength;
	char sign;
	
	public void putChr(String _chr){
		chr = _chr.toUpperCase();
	}
	
	public void putStart(int _txStart){
		txStart = _txStart;
	}
	
	public void putEnd(int _txEnd){
		txEnd = _txEnd;
	}
	
	public void putSign(char _sign){
		sign = _sign;
	}
	
	

	public String getChr(){
		return chr;
	}
	
	public int getTxStart(){
		return txStart;
	} 
	
	public int getTxEnd(){
		return txEnd;
	}
	
	public int getLength(){
		return Math.abs(txStart-txEnd);
	}
	
	public char getSign(){
		return sign;
	}
	
	public String printEle(){
		
		String head = "####geneLocation testing####\n";
		String ele = "chr: "+chr+",txStart: "+txStart+",txEnd: "+txEnd+",geneLength: "+getLength()+"\n";		
		String tail = "############################\n\n";
		return head+ele+tail;
	}
	
}
