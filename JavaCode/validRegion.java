package ChipSeqProcess;

/*
 * Point a = new Point(10, 10);
 * Point b = a;
 * a.x = 100;
 */



public class validRegion {
	
	    public int ini, end;
	    public validRegion(int ini, int end) {
	        this.ini = ini;
	        this.end = end;
	    }
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + ini;
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			validRegion other = (validRegion) obj;
			if (end != other.end)
				return false;
			if (ini != other.ini)
				return false;
			return true;
		}	
}
