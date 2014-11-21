package ChipSeqProcess;

/*
 * construct genome coordinates for gene
 * 
 */

public class genomeMap {
	
	char sign;
	int txStart;
	int txEnd;
	int stExtend;
	int endExtend;
	
	public void putSign(char _sign){
		sign = _sign;
	}
	
	public void putTxStart(int _txStart){
		txStart = _txStart;
	} 
	
	public void putTxEnd(int _txEnd){
		txEnd = _txEnd;
	}
	
	public void putStExtend(int _stExtend){
		stExtend = _stExtend;
	}
	
	public void putEndExtend(int _endExtend){
		endExtend = _endExtend;
	}
	
	
	
	public char getSign(){
		return sign;
	}
	
	public int getTxStart(){
		return txStart;
	} 
	
	public int getTxEnd(){
		return txEnd;
	}
	
	public int getStExtend(){
		return stExtend;
	}
	
	public int getEndExtend(){
		return endExtend;
	}
	
	public String printEle(){
		
		String head = "####genomeMap testing#######\n";
		String ele = "sign: "+sign+",txStart: "+txStart+",txEnd: "+txEnd+",txStartExtend: "+stExtend+",txEndExtend: "+endExtend+"\n";		
		String tail = "############################\n\n";
		return head+ele+tail;
	}
	
}