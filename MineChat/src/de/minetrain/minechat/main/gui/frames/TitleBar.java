package de.minetrain.minechat.main.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.minetrain.minechat.main.gui.buttons.ButtonType;
import de.minetrain.minechat.main.gui.buttons.MineButton;
import de.minetrain.minechat.main.gui.buttons.TabButtonOffset;
import de.minetrain.minechat.main.gui.buttons.ChannelTab;

public class TitleBar extends JPanel{
	private static final long serialVersionUID = 2767970625570676160L;
	public static final int tabButtonHight = 9;
	public final JFrame frame;
	private final ChannelTab mainTab;
	private final ChannelTab secondTab;
	private final ChannelTab thirdTab;
	public JButton tap1;
	public JButton tap2;
	public JButton tap3;
    private int mouseX, mouseY;

	public TitleBar(JFrame frame, JLabel texture) {
		this.frame = frame;
		this.mainTab = new ChannelTab(new ImageIcon("MineChatTextur.png"), "Sintica");
		this.secondTab = new ChannelTab(new ImageIcon("MineChatTextur2.png"), "ThisEguy");
		this.thirdTab = new ChannelTab(new ImageIcon("MineChatTextur3.png"), "Pferdchen");
		
		setLayout(null);
		setOpaque(false);
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

        tap1 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_1).setInvisible(true);
        tap1.setBounds(53, tabButtonHight, 110, 35);
        tap1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				texture.setIcon(mainTab.getTexture());
				offsetButtons(TabButtonOffset.TAB_1);
			}
		});
        
        tap2 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_2).setInvisible(true);
        tap2.setBounds(168, tabButtonHight, 115, 35);
        tap2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				texture.setIcon(secondTab.getTexture());
				offsetButtons(TabButtonOffset.TAB_2);
			}
		});
        
        tap3 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_3).setInvisible(true);
        tap3.setBounds(288, tabButtonHight, 110, 35);
        tap3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				texture.setIcon(thirdTab.getTexture());
				offsetButtons(TabButtonOffset.TAB_3);
			}
		});
        

        add(tap1);
        add(tap2);
        add(tap3);
        add(minimizeButton);
        add(settingsButton);
        add(closeButton);
        offsetButtons(TabButtonOffset.TAB_1);
	}

	
	
	public void offsetButtons(TabButtonOffset offset) {
		Point location = tap1.getLocation();
		location.setLocation(location.getX(), offset.getOffset(TabButtonOffset.TAB_1, location.y));
		tap1.setLocation(location);
		
		location = tap2.getLocation();
		location.setLocation(location.getX(), offset.getOffset(TabButtonOffset.TAB_2, location.y));
		tap2.setLocation(location);
		
		location = tap3.getLocation();
		location.setLocation(location.getX(), offset.getOffset(TabButtonOffset.TAB_3, location.y));
		tap3.setLocation(location);
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
