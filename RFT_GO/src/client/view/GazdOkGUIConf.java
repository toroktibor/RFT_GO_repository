package client.view;

import java.awt.Color;
import java.awt.Dimension;

public class GazdOkGUIConf {
 
	public static final Dimension CONTENTPANE_PREF_DIM = new Dimension(1000, 700);
	public static final Color CONTENTPANE_BGCOLOR = new Color(0, 0, 0); 
	public static final int CONTENTPANE_BORDER_WEIGHT = 2;
	
	public static final Dimension GAMETABLE_PREF_DIM = new Dimension(690, 445);
	public static final Color GAMETABLE_BGCOLOR = new Color(0, 255, 0);
	public static final int GAMETABLE_BORDER_WEIGHT = 2;
	
	public static final Dimension HISTORY_PREF_DIM = new Dimension(306, CONTENTPANE_PREF_DIM.height);
	public static final Color HISTORY_BGCOLOR = new Color(220, 220, 220);
	public static final int HISTORY_BORDER_WEIGHT = 2;
	
	public static final Dimension HISTORY_LABEL_DIM = new Dimension(HISTORY_PREF_DIM.width, 30);
	public static final int HISTORY_LABEL_BORDER_WEIGHT = 1;
	
	public static final Dimension HISTORY_TEXT_DIM = new Dimension(HISTORY_PREF_DIM.width, HISTORY_PREF_DIM.height - HISTORY_LABEL_DIM.height);
	public static final int HISTORY_TEXT_BORDER_WEIGHT = 1;
	public static final Color HISTORY_TEXT_COLOR = new Color(200, 200, 200);
	
	public static final Dimension STATUSBAR_PREF_DIM = new Dimension(CONTENTPANE_PREF_DIM.width - HISTORY_PREF_DIM.width, CONTENTPANE_PREF_DIM.height - GAMETABLE_PREF_DIM.height);
	public static final Color STATUSBAR_BGCOLOR = new Color(0, 255, 255);
	public static final int STATUSBAR_BORDER_WEIGHT = 2;

	public static final String DEFAULT_NAME = "Paradi Csoma";
	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final String DEFAULT_PORT = "6000";

}
