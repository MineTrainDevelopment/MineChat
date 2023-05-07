package de.minetrain.minechat.gui.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

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
        
        setStringPainted(true); //Don�t sow the text inside the bar.
        setBackground(new Color(40, 40, 40));
        setForeground(Color.GREEN);
		setBorder(titledBorder);
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
    
    
    
}