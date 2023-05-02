package de.minetrain.minechat.main.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TitleBar extends JPanel{
	private static final long serialVersionUID = 2767970625570676160L;
	public final JFrame frame;
    private int mouseX, mouseY;

	public TitleBar(JFrame frame) {
		this.frame = frame;
		
		setBackground(Color.BLUE);
        setBounds(0, 0, 500, 48);
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());
        
        JLabel titleLabel = new JLabel("Sintica           |           thisEguy           |           gronkh           |           ffow88");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBounds(0, 0, 440, 30);
        
        // Erstelle den Minimieren-Button
        JButton settingsButton = new JButton("-");
        settingsButton.setBackground(Color.GREEN);
        settingsButton.setPreferredSize(new Dimension(25, 25));
        settingsButton.setBounds(440, 0, 30, 30);
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        // Erstelle den Schließen-Button
        JButton closeButton = new JButton("X");
        closeButton.setBackground(Color.RED);
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setBounds(470, 0, 30, 30);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Füge die Komponenten zur Titelleiste hinzu
        add(titleLabel);
        add(settingsButton);
        add(closeButton);
	}

	
	
	
	private MouseAdapter MoiseListner() {
		return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
	}

	private MouseAdapter mouseMotionListner() {
		return new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int newX = e.getXOnScreen() - mouseX;
                int newY = e.getYOnScreen() - mouseY;

                frame.setLocation(newX, newY);
            }
        };
	}
}
