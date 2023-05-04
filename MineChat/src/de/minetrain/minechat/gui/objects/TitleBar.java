package de.minetrain.minechat.gui.objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TitleBar extends JPanel{
	private static final long serialVersionUID = 2767970625570676160L;
	public static final int tabButtonHight = 9;
	public ChannelTab currentTab;
	public final JFrame frame;
	public final JLabel texture;
	public final ChannelTab mainTab;
	private final ChannelTab secondTab;
	private final ChannelTab thirdTab;
	public JButton tab1;
	public JButton tab2;
	public JButton tab3;
    private int mouseX, mouseY;

	public TitleBar(JFrame frame, JLabel texture) {
		this.frame = frame;
		this.texture = texture;

        // Erstelle den Minimieren-Button
        JButton minimizeButton = new JButton("-");
        minimizeButton.setBackground(Color.GREEN);
        minimizeButton.setPreferredSize(new Dimension(25, 25));
        minimizeButton.setBounds(410, 10, 30, 30);
        minimizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {frame.setExtendedState(JFrame.ICONIFIED); }
        });

        // Erstelle den Schließen-Button
        JButton closeButton = new JButton("X");
        closeButton.setBackground(Color.RED);
        closeButton.setPreferredSize(new Dimension(25, 25));
        closeButton.setBounds(450, 10, 30, 30);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){System.exit(0);}
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

        tab1 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_1).setInvisible(true);
        tab1.setBounds(53, tabButtonHight, 110, 35);
        tab1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_MAIN, mainTab);}
		});
        
        tab2 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_2).setInvisible(true);
        tab2.setBounds(168, tabButtonHight, 115, 35);
        tab2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_SECOND, secondTab);}
		});
        
        tab3 = new MineButton(new Dimension(80, 30), new Point(0, 0), ButtonType.TAB_3).setInvisible(true);
        tab3.setBounds(288, tabButtonHight, 110, 35);
        tab3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_THIRD, thirdTab);}
		});
        
        this.mainTab = new ChannelTab(tab1, new ImageIcon("MineChatTextur.png"), "Sintica", TabButtonType.TAB_MAIN);
		this.secondTab = new ChannelTab(tab2, new ImageIcon("MineChatTextur2.png"), "ThisEguy", TabButtonType.TAB_SECOND);
		this.thirdTab = new ChannelTab(tab3 ,new ImageIcon("MineChatTextur3.png"), "Pferdchen", TabButtonType.TAB_THIRD);

        setLayout(null);
		setOpaque(false);
		setBackground(Color.BLUE);
        setBounds(0, 0, 500, 48);
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());
        
        add(tab1);
        add(tab2);
        add(tab3);
        add(minimizeButton);
        add(settingsButton);
        add(closeButton);
	}

	public void changeTab(TabButtonType buttonType, ChannelTab tab) {
		this.currentTab = tab;
    	texture.setIcon(tab.getTexture());
    	mainTab.offsetButton(buttonType);
    	thirdTab.offsetButton(buttonType);
    	secondTab.offsetButton(buttonType);
	}
	
	public ArrayList<JLabel> getTabNames(){
		ArrayList<JLabel> list = new ArrayList<JLabel>();
		list.add(mainTab.getTabLabelWithData(tab1.getLocation(getLocation()), tab1.getSize()));
		list.add(secondTab.getTabLabelWithData(tab2.getLocation(getLocation()), tab2.getSize()));
		list.add(thirdTab.getTabLabelWithData(tab3.getLocation(getLocation()), tab3.getSize()));
		return list;
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
