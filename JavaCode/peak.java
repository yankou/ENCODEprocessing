package ChipSeqProcess;

public class peak {
	
	/*
	 * read in lines from .bed file in format: [chr start end readCount]
	 */

	String chr;
	int peakStart;
	int peakEnd;
	int readCount;
	int peakLength;
	double pkScore;
	int disTSS;
	double fe;

	public peak(String _bedFileLine) {

		String temp[] = _bedFileLine.split("\t");
		
//		System.out.print(_bedFileLine+"\n");
//		System.out.print(temp.length);

		if ((!temp[0].equalsIgnoreCase("chrM"))) {
			// System.out.println("testing..."+_bedFileLine);
			chr = temp[0].toUpperCase();
			peakStart = Integer.parseInt(temp[1]);
			peakEnd = Integer.parseInt(temp[2]);
			
			if(temp[3].toUpperCase().contains("MACS")){
				pkScore = Double.parseDouble(temp[4]);
				readCount = 0;
			}
			
			else if(temp[3].contains(".")) {
//				pkScore = Double.parseDouble(temp[3]);
				pkScore = 0;
				readCount = 0;
			} else {
//				readCount = Integer.parseInt(temp[3]);
				readCount = 0;
				pkScore = 0.0;
			}			
			
			peakLength = peakEnd - peakStart;
			disTSS = 0;
			fe = 0;
			if (peakLength < 0) {
				System.out.println("peaks in reverse coordinates, rewrite 'loadPeak in class peak'.");
			}
		} else {
			System.out.println("Error: peaks in chrM, clean input file. "
					+ _bedFileLine);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chr == null) ? 0 : chr.hashCode());
		result = prime * result + peakEnd;
		result = prime * result + peakStart;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		peak other = (peak) obj;
		if (chr == null) {
			if (other.chr != null)
				return false;
		} else if (!chr.equals(other.chr))
			return false;
		if (peakEnd != other.peakEnd)
			return false;
		if (peakStart != other.peakStart)
			return false;
		return true;
	}

	public void putTSS(int _DistoTSS) {
		this.disTSS = _DistoTSS;
	}

	public void putFE(String _FE){
		this.fe = Double.parseDouble(_FE);
	}

	public String getChr() {
		return chr;
	}

	public int getPeakStart() {
		return peakStart;
	}

	public int getPeakEnd() {
		return peakEnd;
	}

	public int getPeakLength() {
		return peakLength;
	}

	public int getReadCount() {
		return readCount;
	}

	public int getDisTSS() {
		return disTSS;
	}

	public double getPkScore() {
		return pkScore;
	}
	
	public double getFE(){
		return fe;
	}

	public String printTestEle() {

		String head = "########PEAK###########\n";
		String ele = "chr: " + chr + ",peakStart: " + peakStart + ",peakEnd: "
				+ peakEnd + ",peakLength: " + peakLength + ",peakReadCount: "
				+ readCount + ",peakScore: " + pkScore + ",disTSS: " + disTSS
				+ "\n";
		String tail = "############################\n\n";
		return head + ele + tail;
	}

	public String printEle() {

		String ele = chr + "," + peakStart + "," + peakEnd + "," + peakLength
				+ "," + readCount + "," + pkScore + "," + disTSS;

		return ele;
	}

	public String printEleNoDis() {

		String ele = chr + "," + peakStart + "," + peakEnd + "," + peakLength
				+ "," + readCount + "," + pkScore;

		return ele;
	}

}
