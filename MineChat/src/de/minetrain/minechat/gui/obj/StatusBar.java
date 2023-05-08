package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class StatusBar extends JProgressBar {
	private static final long serialVersionUID = -2442655331085376873L;
	TitledBorder titledBorder;

	public StatusBar() {
    	super(0, 100);
        
        titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2), "Loading");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitleColor(Color.WHITE);
        titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
        
        setStringPainted(true); //Don´t sow the text inside the bar.
        setBackground(new Color(40, 40, 40));
        setForeground(Color.GREEN);
		setBorder(titledBorder);
    }
	
	public void setTitleFont(Font font){
        titledBorder.setTitleFont(font);
	}
    
    public StatusBar setProgress(String message, int percent) {
        SwingUtilities.invokeLater(() -> {
            setString("");
            titledBorder.setTitle(message);
            setValue(percent);
        });
        
        return this;
    }
    
    public StatusBar setError(String message) {
        SwingUtilities.invokeLater(() -> {
            setString("ERROR");
            setForeground(Color.RED);
            titledBorder.setTitle(message);
            setValue(100);
        });
        
        return this;
    }
    
    public StatusBar setDone(String message) {
        SwingUtilities.invokeLater(() -> {
            setString("DONE");
            setForeground(Color.GREEN);
            titledBorder.setTitle(message);
            setValue(100);
        });
        
        return this;
    }
    
    
    public static Integer getPercentage(long arraySize, long index) {
	    if (arraySize <= 0 || index < 0 || index >= arraySize) {
	        throw new IllegalArgumentException("Invalid array size or index.");
	    }
	    
	    return (int) Math.round(((double) (index + 1) / (double) arraySize) * 100.0);
	}
    
    
}