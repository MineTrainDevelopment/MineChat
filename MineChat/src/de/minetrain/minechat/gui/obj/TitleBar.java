package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.main.Main;

public class TitleBar extends JPanel{
	private static final Logger logger = LoggerFactory.getLogger(TitleBar.class);
	private static final long serialVersionUID = 2767970625570676160L;
	public static final int tabButtonHight = 9;
	public static ChannelTab currentTab;
	public final MainFrame mainFrame;
	public final JLabel texture;
	private ChannelTab mainTab;
	private ChannelTab secondTab;
	private ChannelTab thirdTab;
	private JButton tab1;
	private JButton tab2;
	private JButton tab3;
    private int mouseX, mouseY;

	public TitleBar(MainFrame mainFrame, JLabel texture) {
		this.mainFrame = mainFrame;
		this.texture = texture;

        // Erstelle den Minimieren-Button
        JButton minimizeButton = new MineButton(new Dimension(25, 25), null, ButtonType.MINIMIZE);
        minimizeButton.setBackground(Color.GREEN);
        minimizeButton.setBounds(410, 10, 30, 30);
        minimizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {mainFrame.setExtendedState(JFrame.ICONIFIED);}
        });

        // Erstelle den Schließen-Button
        JButton closeButton = new MineButton(new Dimension(25, 25), null, ButtonType.CLOSE);
        closeButton.setBackground(Color.RED);
        closeButton.setBounds(450, 10, 30, 30);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){System.exit(0);}
        });
        
        JButton settingsButton = new MineButton(new Dimension(25, 25), null, ButtonType.SETTINGS);
        settingsButton.setBackground(Color.YELLOW);
        settingsButton.setBounds(15, 10, 30, 30);
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	System.err.println("TODO: Settings Menü.");
//            	new EmoteDownlodFrame(mainFrame);
            	new EmoteSelector(mainFrame, false);
            }
        });

        tab1 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_1).setInvisible(!MainFrame.debug);
        tab1.setBounds(53, tabButtonHight, 110, 35);
        tab1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_MAIN, mainTab);}
		});
        
        tab2 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_2).setInvisible(!MainFrame.debug);
        tab2.setBounds(168, tabButtonHight, 115, 35);
        tab2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_SECOND, secondTab);}
		});
        
        tab3 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_3).setInvisible(!MainFrame.debug);
        tab3.setBounds(288, tabButtonHight, 110, 35);
        tab3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(TabButtonType.TAB_THIRD, thirdTab);}
		});
        
        this.mainTab = new ChannelTab(mainFrame, tab1, TabButtonType.TAB_MAIN);
		this.secondTab = new ChannelTab(mainFrame, tab2, TabButtonType.TAB_SECOND);
		this.thirdTab = new ChannelTab(mainFrame, tab3, TabButtonType.TAB_THIRD);
		
		mainFrame.profileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://twitch.tv/"+currentTab.getChannelName()));
					Thread.sleep(250);
					java.awt.Desktop.getDesktop().browse(new URI("https://www.twitch.tv/popout/"+currentTab.getChannelName()+"/chat?popout="));
				} catch (IOException | URISyntaxException | InterruptedException ex) {
					logger.error("Something went wrong, while opening a Website!", ex);
				}
			}
		});
		
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
		currentTab = tab;
		mainFrame.setTitle(tab.getDisplayName()+" -- MineChat "+Main.VERSION);
		mainFrame.profileButton.setIcon(new ImageIcon(tab.getProfileImagePath()));
		
//		MacroRow currentMacroRow = tab.getMacros().getCurrentMacroRow();
		tab.loadMacroRow(null);
		
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

                mainFrame.setLocation(newX, newY);
            }
        };
	}

	public ChannelTab getMainTab() {
		return mainTab;
	}

	public ChannelTab getSecondTab() {
		return secondTab;
	}

	public ChannelTab getThirdTab() {
		return thirdTab;
	}

	public JButton getTab1() {
		return tab1;
	}

	public JButton getTab2() {
		return tab2;
	}

	public JButton getTab3() {
		return tab3;
	}
}
