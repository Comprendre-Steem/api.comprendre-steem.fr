package fr.comprendresteem;

public class UnitConverter {

	// https://steemit.com/steemit/@digitalnotvir/how-reputation-scores-are-calculated-the-details-explained-with-simple-math
	public static double getSimpleReputation(long value) {
		boolean neg = value < 0;
		double rep = Math.log10(Math.abs(value));
		rep = Math.max(rep - 9.0, 0.0);
		rep = (neg ? -1 : 1) * rep;
		rep = (rep * 9) + 25;
		return Math.round(rep * 1000) / 1000.0;
		
		// (first) Naive implementation
//		double rep = (((Math.log10(value)) - 9) * 9) + 25;
//		return Math.round(rep * 1000) / 1000.0;
	}
	
}
