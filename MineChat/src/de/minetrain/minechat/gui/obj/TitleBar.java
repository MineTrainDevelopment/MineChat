package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.emotes.RoundetImageIcon;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.frames.settings.SettingsFrame;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class TitleBar extends JPanel{
	private static final Logger logger = LoggerFactory.getLogger(TitleBar.class);
	private static final long serialVersionUID = 2767970625570676160L;
	public static final int tabButtonHight = 9;
	public static ChannelTab currentTab;
	public final MainFrame mainFrame;
	public final JLabel texture;
	public ChannelTab mainTab;
	public ChannelTab secondTab;
	public ChannelTab thirdTab;
	private JButton tab1;
	private JButton tab2;
	private JButton tab3;
	public ChannelTab channelTabRow_Main;
	public ChannelTab channelTabRow_Second;
	public ChannelTab channelTabRow_Third;
	
	private JLabel mainTabName = new JLabel("", SwingConstants.CENTER);
    private JLabel secondTabName = new JLabel("", SwingConstants.CENTER);
    private JLabel thirdTabName = new JLabel("", SwingConstants.CENTER);
    
	public static int titleRowIndex = 0;
    private int mouseX, mouseY;

	public TitleBar(MainFrame mainFrame, JLabel texture) {
		this.mainFrame = mainFrame;
		this.texture = texture;

        // Erstelle den Minimieren-Button
        JButton minimizeButton = new MineButton(new Dimension(25, 25), null, ButtonType.MINIMIZE).setInvisible(!MainFrame.debug);
        minimizeButton.setBounds(410, 9, 30, 30);
        minimizeButton.setIcon(Main.TEXTURE_MANAGER.getProgramMinimize());
        minimizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {mainFrame.setExtendedState(JFrame.ICONIFIED);}
        });

        // Erstelle den Schließen-Button
        JButton closeButton = new MineButton(new Dimension(25, 25), null, ButtonType.CLOSE).setInvisible(!MainFrame.debug);
        closeButton.setIcon(Main.TEXTURE_MANAGER.getProgramClose());
        closeButton.setBounds(448, 9, 30, 30);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){System.exit(0);}
        });
        
        JButton settingsButton = new MineButton(new Dimension(25, 25), null, ButtonType.SETTINGS).setInvisible(!MainFrame.debug);
//        settingsButton.setBackground(Color.YELLOW);
        settingsButton.setBounds(12, 9, 30, 30);
        settingsButton.setIcon(Main.TEXTURE_MANAGER.getProgramSettings());
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	System.err.println("TODO: Settings Menü.");
//            	new EmoteDownlodFrame(mainFrame);
//            	new EmoteSelector(mainFrame, false);
//            	new AddWordHighlightFrame(mainFrame);
            	new SettingsFrame(mainFrame);
            }
        });
        

        JButton changeRowButton = new MineButton(new Dimension(25, 25), null, ButtonType.MINIMIZE).setInvisible(!MainFrame.debug);
        changeRowButton.setBounds(410, 9, 30, 30);
        changeRowButton.setIcon(Main.TEXTURE_MANAGER.getRowArrowRight());
        changeRowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	toggleTitleRowIndex();
            	changeRowButton.setIcon(titleRowIndex == 0 ? Main.TEXTURE_MANAGER.getRowArrowRight() : Main.TEXTURE_MANAGER.getRowArrowLeft());
            	reloadTabs();
        	}
        });

        tab1 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_1).setInvisible(!MainFrame.debug);
        tab1.setBounds(53, tabButtonHight, 110, 35);
        tab1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(getMainTab());}
		});
        
        tab2 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_2).setInvisible(!MainFrame.debug);
        tab2.setBounds(168, tabButtonHight, 115, 35);
        tab2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(getSecondTab());}
		});
        
        tab3 = new MineButton(new Dimension(80, 30), null, ButtonType.TAB_3).setInvisible(!MainFrame.debug);
        tab3.setBounds(288, tabButtonHight, 110, 35);
        tab3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){changeTab(getThirdTab());}
		});
        
        HashMap<TabButtonType, String> allChannelTabs = DatabaseManager.getChannelTabIndexDatabase().getAll();
		List<TwitchUserObj> twitchUsers = TwitchManager.getTwitchUsers(TwitchApiCallType.ID, 
				allChannelTabs.get(TabButtonType.TAB_MAIN),
				allChannelTabs.get(TabButtonType.TAB_SECOND),
				allChannelTabs.get(TabButtonType.TAB_THIRD),
				allChannelTabs.get(TabButtonType.TAB_MAIN_ROW_2),
				allChannelTabs.get(TabButtonType.TAB_SECOND_ROW_2),
				allChannelTabs.get(TabButtonType.TAB_THIRD_ROW_2));
		
		HashMap<String, ChannelData> allChannels = DatabaseManager.getChannel().getAllChannels();
        
		twitchUsers.stream()
			.filter(channel -> !channel.isDummy() && allChannels.containsKey(channel.getUserId()) && !channel.getLoginName().equals(allChannels.get(channel.getUserId()).getLoginName()))
			.forEach(channel -> DatabaseManager.getChannel().updateChannelLoginName(channel.getUserId(), channel.getLoginName()));
		DatabaseManager.commit();
		
		
        this.mainTab = new ChannelTab(mainFrame, tab1, TabButtonType.TAB_MAIN, mainTabName);
        this.channelTabRow_Main = new ChannelTab(mainFrame, tab1, TabButtonType.TAB_MAIN_ROW_2, mainTabName);

        this.secondTab = new ChannelTab(mainFrame, tab2, TabButtonType.TAB_SECOND, secondTabName);
        this.channelTabRow_Second = new ChannelTab(mainFrame, tab2, TabButtonType.TAB_SECOND_ROW_2, secondTabName);

        this.thirdTab = new ChannelTab(mainFrame, tab3, TabButtonType.TAB_THIRD, thirdTabName);
		this.channelTabRow_Third = new ChannelTab(mainFrame, tab3, TabButtonType.TAB_THIRD_ROW_2, thirdTabName);
		
		mainFrame.profileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://www.twitch.tv/popout/"+currentTab.getChannelName()+"/chat?popout="));
					Thread.sleep(250);
					java.awt.Desktop.getDesktop().browse(new URI("https://twitch.tv/"+currentTab.getChannelName()));
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
//        add(minimizeButton);
        add(changeRowButton);
        add(settingsButton);
        add(closeButton);
	}
	
	public void reloadTabs(){
		HashMap<TabButtonType, String> allChannelTabs = DatabaseManager.getChannelTabIndexDatabase().getAll();
		
		getMainTab().reload(allChannelTabs.get(getMainTab().getTabType()));
		getSecondTab().reload(allChannelTabs.get(getSecondTab().getTabType()));
		getThirdTab().reload(allChannelTabs.get(getThirdTab().getTabType()));
		
		changeTab(getMainTab());
	}

	public void changeTab(ChannelTab tab) {
		currentTab = tab;
		mainFrame.setTitle(tab.getDisplayName()+" -- MineChat "+Main.VERSION);
		mainFrame.profileButton.setIcon(new RoundetImageIcon(tab.getProfileImagePath(), 20));
		
		tab.loadMacroRow(null);
		
    	texture.setIcon(tab.getTexture());
    	
		getMainTab().offsetButton(tab.getTabType());
    	getSecondTab().offsetButton(tab.getTabType());
    	getThirdTab().offsetButton(tab.getTabType());

		mainTab.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_MAIN)) ? true : false);
		secondTab.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_SECOND)) ? true : false);
		thirdTab.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_THIRD)) ? true : false);
		
    	channelTabRow_Main.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_MAIN_ROW_2)) ? true : false);
    	channelTabRow_Second.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_SECOND_ROW_2)) ? true : false);
    	channelTabRow_Third.getChatWindow().setVisible((tab.getTabType().equals(TabButtonType.TAB_THIRD_ROW_2)) ? true : false);
    	
    	MineTextArea.setStaticChannelEmoteDictionary(tab.getConfigID());
	}
	
	public ArrayList<JLabel> getTabNames(){
		ArrayList<JLabel> list = new ArrayList<JLabel>();
		list.add(getTabLabelWithData(mainTabName, tab1.getLocation(getLocation()), tab1.getSize()));
		list.add(getTabLabelWithData(secondTabName, tab2.getLocation(getLocation()), tab2.getSize()));
		list.add(getTabLabelWithData(thirdTabName, tab3.getLocation(getLocation()), tab3.getSize()));
		return list;
	}
	
	public JLabel getTabLabelWithData(JLabel nameLabel, Point point, Dimension size) {
		nameLabel.setLocation(point);
		nameLabel.setSize(size);
		return nameLabel;
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
	
	public static void toggleTitleRowIndex(){
		titleRowIndex = (titleRowIndex > 0) ? 0:1;
	}
	
	public ChannelTab getMainTab() {
		return titleRowIndex == 0 ? mainTab : channelTabRow_Main;
	}

	public ChannelTab getSecondTab() {
		return titleRowIndex == 0 ? secondTab : channelTabRow_Second;
	}

	public ChannelTab getThirdTab() {
		return titleRowIndex == 0 ? thirdTab : channelTabRow_Third;
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
