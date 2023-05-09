package de.minetrain.minechat.gui.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EmoteRenameFrame extends JDialog{
	private static final long serialVersionUID = -5007868617236311658L;
    private JTextField emoteName = new JTextField();
    private JButton okayButton = new JButton();
    private static final int fontSize = 13;
    private String newEmoteName;
    private boolean isClosed = false;

	public EmoteRenameFrame(EmoteDownlodFrame downlodFrame, String defaultName) {
        setSize(300, 50);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(40, 40, 40));
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(14, 14, 14), 5));
        panel.setBackground(new Color(40, 40, 40));
        panel.setLayout(new GridLayout(2, 1));
        
        JLabel description = new JLabel();
        description.setFont(new Font(null, Font.BOLD, fontSize));
        description.setBackground(new Color(40, 40, 40));
        description.setForeground(Color.WHITE);
        description.setHorizontalAlignment(JTextField.CENTER);
        description.setText("Name �ndern:");
        
        emoteName.setBackground(new Color(90, 90, 90));
        emoteName.setFont(new Font(null, Font.BOLD, fontSize));
        emoteName.setForeground(Color.WHITE);
        emoteName.setHorizontalAlignment(JTextField.CENTER);
        emoteName.setText(defaultName);
        
        JPanel textPannels = new JPanel(new GridLayout(1, 2, 8, 10));
        textPannels.setBackground(new Color(40, 40, 40));
        textPannels.setSize(okayButton.getSize());
        textPannels.add(description);
        textPannels.add(emoteName);
        
        okayButton.setBackground(new Color(30, 30, 30));
        okayButton.setFont(new Font(null, Font.BOLD, fontSize));
        okayButton.setForeground(Color.WHITE);
        okayButton.setHorizontalAlignment(JTextField.CENTER);
        okayButton.addActionListener(buttonAction());
        okayButton.setText("Okay");
        
        Point location = downlodFrame.getLocation();
        location.setLocation(location.x+0 , location.y+160);
        setLocation(location);
        
        panel.add(textPannels);
        panel.add(okayButton);
        add(panel);
        setVisible(true);
	}
	
	private ActionListener buttonAction(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(emoteName.getText().length()>0){
					newEmoteName = emoteName.getText();
				}
				
				isClosed = true;
				dispose();
			}
		};
	}
	
	
	public boolean waitForInput(){
		return isClosed || getNewEmoteName() != null;
	}

	public String getNewEmoteName() {
		return newEmoteName;
	}
	
	

}
