package demos;

import beans.sampleBeans.AllFlavorBean;
import imaging.imagers.ImagerFactory;
import imaging.imagers.PrimitiveImager;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import swing.stretchAndClick.ObjectImagePanel;

public class RotationDemo extends DemoConsts
{

	public static void main(String[] args) 
	{
		arrayDemo(6, 4, 1900, 1700, true);
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

		f1 = SwingUtils.frameFactory(
				fWidth, fHeight, "Array Data Rotation Demo", 1, 2);
		for (int trans = 1; trans < 2; trans++)
		for (int flipY = 0; flipY < 1; flipY++) {	
			for (int flipX = 0; flipX < 1; flipX++)
		{
				PrimitiveImager imgr = ImagerFactory.quickFactory(
						null, flavorArray, 100,
						trueFalse[flipX], trueFalse[flipY], trueFalse[trans],
						true, true,
						field, AllFlavorBean.class,
						gradCols, boolCols);
				
				
			ObjectImagePanel<AllFlavorBean> pan = ImagePanelFactory.buildPanel(
					,
					field, true, 0, 0, 0.1);
			pan.labelPixels(font, null);
			f1.add(pan);

		}}




		f1.setVisible(show);



	}




}
