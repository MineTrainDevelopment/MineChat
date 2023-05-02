package de.minetrain.minechat.main.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.minetrain.minechat.main.gui.buttons.ButtonType;
import de.minetrain.minechat.main.gui.buttons.MineButton;

public class TitleBar extends JPanel{
	private static final long serialVersionUID = 2767970625570676160L;
	public final JFrame frame;
    private int mouseX, mouseY;

	public TitleBar(JFrame frame) {
		this.frame = frame;
		
		setLayout(null);
		setBackground(Color.BLUE);
        setBounds(0, 0, 500, 48);
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());

        JLabel titleLabel = new JLabel("MineChat");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBounds(0, 0, 440, 30);
        
        // Erstelle den Minimieren-Button
        JButton minimizeButton = new JButton("-");
        minimizeButton.setBackground(Color.GREEN);
        minimizeButton.setPreferredSize(new Dimension(25, 25));
        minimizeButton.setBounds(410, 10, 30, 30);
        minimizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        // Erstelle den Schließen-Button
        JButton closeButton = new JButton("X");
        closeButton.setBackground(Color.RED);
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setBounds(450, 10, 30, 30);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JButton settingsButton = new JButton("X");
        settingsButton.setBackground(Color.YELLOW);
        settingsButton.setPreferredSize(new Dimension(25, 25));
        settingsButton.setBounds(15, 10, 30, 30);
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.err.println("TODO: Settings Menü.");
            }
        });

        // Füge die Komponenten zur Titelleiste hinzu
        add(titleLabel);

        JButton tap1 = new MineButton(new Dimension(80, 30), new Point(17, 55), ButtonType.TAB_1).setInvisible(false);
        tap1.setBounds(53, 9, 110, 35);
        
        JButton tap2 = new MineButton(new Dimension(80, 30), new Point(17, 55), ButtonType.TAB_2).setInvisible(false);
        tap2.setBounds(168, 9, 115, 35);
        
        JButton tap3 = new MineButton(new Dimension(80, 30), new Point(17, 55), ButtonType.TAB_3).setInvisible(false);
        tap3.setBounds(288, 9, 110, 35);
        

        add(tap1);
        add(tap2);
        add(tap3);
        add(minimizeButton);
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
