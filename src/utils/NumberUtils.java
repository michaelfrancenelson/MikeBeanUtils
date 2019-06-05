package utils;

public class NumberUtils 
{
	public static String roundNumeric(String lab, int nSigFig, String dblFmt)
	{
		double tol = 1E-8;
		double d = 0.0, d2, d3, d4, d5, d6;
		try { d = Double.parseDouble(lab); }
		catch (Exception e) { } // logger.debug("could not parse input"); }
		d2 = Math.abs(d);

		double sign = 1.0;
		if (d < 0) sign = -1.0;
		if (d2 < tol) { return("0"); }

		double log10 = Math.log10(d2);
		int pow1 = (int) log10;
		d3 = d2 * Math.pow(10.0, (double)(-pow1));
		d4 = d3 * Math.pow(10.0, nSigFig);
		d5 = Math.round(d4);
		d6 = d5 * Math.pow(10.0, -((double) -pow1 + nSigFig));
		
		return (String.format(dblFmt, sign * d6));
	}
}
