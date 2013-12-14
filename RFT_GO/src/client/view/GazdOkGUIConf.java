package client.view;

import java.awt.Color;
import java.awt.Dimension;

public class GazdOkGUIConf {

	public static final Dimension WINDOW_PREF_DIM = new Dimension(1000, 768);
	public static final Color CONTENTPANE_BGCOLOR = new Color(255, 0, 0); 
	public static final int CONTENTPANE_BORDER_WEIGHT = 2;
	
	public static final Dimension GAMETABLE_PREF_DIM = new Dimension(690, 445);
	public static final Color GAMETABLE_BGCOLOR = new Color(0, 255, 0);
	public static final int GAMETABLE_BORDER_WEIGHT = 2;
	
	public static final Dimension HISTORY_PREF_DIM = new Dimension(306, 445);
	public static final Color HISTORY_BGCOLOR = new Color(255, 255, 0);
	public static final int HISTORY_BORDER_WEIGHT = 2;
	
	public static final Dimension STATUSBAR_PREF_DIM = new Dimension(WINDOW_PREF_DIM.width, WINDOW_PREF_DIM.height-GAMETABLE_PREF_DIM.height);
	public static final Color STATUSBAR_BGCOLOR = new Color(0, 255, 255);
	public static final int STATUSBAR_BORDER_WEIGHT = 2;


}
