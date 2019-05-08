package demos;

import beans.sampleBeans.AllFlavorBean;
import imaging.imagers.ArrayData;
import swing.SwingUtils;
import swing.stretchAndClick.PanelFactory;
import swing.stretchAndClick.ObjectImagePanel;

public class RotationDemo extends DemoConsts
{

	public static void main(String[] args) 
	{
		arrayDemo(21, 14, 1900, 1700, true);
	}

	static void arrayDemo(int nRow, int nCol, int fWidth, int fHeight, boolean show)
	{
		flavorArray = new AllFlavorBean[nRow][nCol];

		for (int row = 0; row < nRow; row++) for (int col = 0; col < nCol; col++)
		{
			AllFlavorBean a = new AllFlavorBean();
			a.setIntPrim(row + col);
			flavorArray[row][col] = a;
		}

		String field = "intPriM";
		trueFalse = new boolean[] {false, true};

		f1 = SwingUtils.frameFactory(
				fWidth, fHeight, "Array Data Rotation Demo", 4, 2);

		ObjectImagePanel<AllFlavorBean> pan;
		ArrayData<AllFlavorBean> arr;
		
		for (int flipY = 0; flipY < 2; flipY++) 	
		for (int trans = 0; trans < 2; trans++)
				for (int flipX = 0; flipX < 2; flipX++)
				{
					arr = new ArrayData<>(flavorArray, 
							trueFalse[flipX], trueFalse[flipY], trueFalse[trans]);

					pan = PanelFactory.buildPanel(
							arr, AllFlavorBean.class, null, field,
							gradCols, boolCols, null, null, null,
							null, null,
							true, 0, 0, ptSize);
					pan.setBorder(border);
					pan.labelPixels(font, null);
					f1.add(pan);
				}

		f2 = SwingUtils.frameFactory(fWidth, fHeight, "Array Data Rotation Demo - untransformed");
		f2.add(PanelFactory.buildPanel(
				new ArrayData<>(flavorArray, false, false, false),
				AllFlavorBean.class, null, field,
				gradCols, boolCols, null, null, null,
				null, null,
				true, 0, 0, ptSize));
		f1.setVisible(show);
		f2.setLocation(fWidth, 0);
		f2.setVisible(show);
	}
}
