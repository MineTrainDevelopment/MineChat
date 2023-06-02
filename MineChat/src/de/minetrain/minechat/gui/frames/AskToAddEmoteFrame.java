package de.minetrain.minechat.gui.frames;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.minetrain.minechat.gui.utils.ColorManager;

public class AskToAddEmoteFrame extends JDialog{
	private static final long serialVersionUID = -6505488358421909589L;
	private boolean dispose;
	private boolean addEmote;
	
	public AskToAddEmoteFrame(MainFrame mainFrame) {
		super(mainFrame, "MineChat, question frame", true);
        setSize(250, 100);
        setLocationRelativeTo(mainFrame);
        setAlwaysOnTop(true);
		setUndecorated(true);
		setResizable(false);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
		
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        
        
        JLabel description = new JLabel("Do you wanna add an emote?");
        description.setHorizontalAlignment(JTextField.CENTER);
        description.setForeground(Color.WHITE);
        description.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
        

        JButton noButton = new JButton("No.");
        noButton.setBackground(Color.RED);
        noButton.setForeground(Color.WHITE);
        noButton.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
//        noButton.setBorder(null);
        noButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){dispose();}
        });
        
        JButton yesButton = new JButton("Yes");
        yesButton.setBackground(Color.GREEN);
        yesButton.setForeground(Color.WHITE);
        yesButton.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
//        yesButton.setBorder(null);
        yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEmote = true;
				dispose();
			}
		});
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBackground(ColorManager.GUI_BORDER);
        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);
        
        panel.add(description);
        panel.add(buttonPanel);
        add(panel);
        setVisible(true);
	}
	
	@Override
	public void dispose() {
		dispose = true;
		super.dispose();
	}

	public boolean isDispose() {
		return dispose;
	}

	public boolean isAddEmote() {
		return addEmote;
	}

}
