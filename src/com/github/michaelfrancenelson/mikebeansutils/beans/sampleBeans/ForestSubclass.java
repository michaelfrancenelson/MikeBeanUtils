package com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans;

import java.util.List;

/** Demo class to verify that builders and Swing components work with sub/super classes. */
public class ForestSubclass extends ForestCell
{

	public static void setSlopeOutside(
			List<List<ForestSubclass>> cells) {
		for (List<ForestSubclass> l : cells)
			for (ForestSubclass f : l)
			{
				if (!f.in_border) f.slope = -Double.MAX_VALUE;
			}
		
		// TODO Auto-generated method stub
		
	}

}
