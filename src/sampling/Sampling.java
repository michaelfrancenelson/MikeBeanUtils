package sampling;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class Sampling 
{
	public static RandomStream getDefaultRs()
	{
		RandomStream rs = new MRG31k3p();
		int[] seeds = new int[] {11111, 22222, 33333, 44444, 55555, 66666};
		MRG31k3p.setPackageSeed(seeds);
		((MRG31k3p) rs).setSeed(seeds);
		return rs;
	}

}
