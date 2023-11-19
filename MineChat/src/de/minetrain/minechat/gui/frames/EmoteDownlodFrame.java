package de.minetrain.minechat.gui.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class EmoteDownlodFrame extends JDialog{
	private static final Logger logger = LoggerFactory.getLogger(EmoteDownlodFrame.class);
	private static final long serialVersionUID = -975812837763868477L;
    private static final int fontSize = 13;
    public enum EmotePlatform{TWITCH, BTTV}
    private JTextField channelName = new JTextField();
//    private EmoteDownlodFrame thisFrame;
    public StatusBar statusBar = new StatusBar();;
    private int mouseX, mouseY;
    

	public EmoteDownlodFrame(MainFrame mainFrame, String channelName) {
		super(mainFrame, "Emote downloader", ModalityType.MODELESS); //true
		this.channelName.setText(channelName);
		this.statusBar.setProgress("Install emotes?", 0);
		buildingGUI(mainFrame.getLocation());
	}
	
	public EmoteDownlodFrame(MainFrame mainFrame) {
		super(mainFrame, "Emote downloader", ModalityType.MODELESS); //true
		statusBar.setProgress("Waiting for input...", 0);
		buildingGUI(mainFrame.getLocation());
	}
	
	public EmoteDownlodFrame(JDialog dialog) {
		super(dialog, "Emote downloader", ModalityType.MODELESS); //true
		statusBar.setProgress("Waiting for input...", 0);
		buildingGUI(dialog.getLocation());
	}

	private void buildingGUI(Point location) {
//		thisFrame = this;
        setSize(300, 180);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(ColorManager.GUI_BACKGROUND);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        panel.setLayout(new GridLayout(4, 2));
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());
        
		JLabel description = new JLabel();
        description.setFont(new Font(null, Font.BOLD, fontSize));
        description.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
        description.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        description.setForeground(Color.WHITE);
        description.setHorizontalAlignment(JTextField.CENTER);
        description.setText("Channel name:");
        
        channelName.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        channelName.setFont(new Font(null, Font.BOLD, fontSize));
        channelName.setForeground(Color.WHITE);
        channelName.setHorizontalAlignment(JTextField.CENTER);
        

        statusBar.setSize(300, 50);
        statusBar.setForeground(Color.ORANGE);
		statusBar.setFont(new Font(null, Font.BOLD, 10));
		statusBar.setTitleFont(new Font(null, Font.BOLD, 15));
        
        JPanel renamePannel = new JPanel(new GridLayout(1, 2, 8, 5));
        renamePannel.setBorder(BorderFactory.createEmptyBorder(3, 3, 2, 3));
        renamePannel.setBackground(ColorManager.GUI_BORDER);
        renamePannel.add(description);
        renamePannel.add(channelName);
        
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow(false));

//        JButton cancelButton = new JButton("Cancel");
        JButton cancelButton = new JButton("Close");
        cancelButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 3, 3, 3));
        buttonPanel.setBackground(ColorManager.GUI_BORDER);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        location.setLocation(location.x+100, location.y+200);
        setLocation(location);
        
        panel.add(statusBar);
        panel.add(renamePannel);
        panel.add(buttonPanel);
        getContentPane().add(panel);
        setVisible(true);
	}
	
	
	
	private ActionListener closeWindow(Boolean isCanselt){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.warn("Press button");
				if(isCanselt){dispose(); return;}
				
	    		SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						getTwitchUser();
						return null;
					}
					
					private TwitchUserObj getTwitchUser() {
						TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, channelName.getText().replace("!", "").replace("?", ""));
						if(twitchUser.isDummy()){
							channelName.setText("Invalid channel!");
							statusBar.setError("Invalid Twitch channel!");
							new Thread(()->{
								for (int i = 0; i < 15; i++) {
									channelName.setForeground(Color.RED);
									try{Thread.sleep(200);}catch(Exception ex){ }
									channelName.setForeground(Color.YELLOW);
									try{Thread.sleep(200);}catch(Exception ex){ }
								}
								channelName.setForeground(Color.WHITE);
							}).start();
							return twitchUser;
						}
						
						statusBar.setProgress("Download startet...", 50);
					    TextureManager.downloadChannelEmotes(twitchUser.getUserId());
					    TextureManager.downloadBttvEmotes(twitchUser.getUserId());
						statusBar.setDone("Download completed!");
						return twitchUser;
					}
	    		};
	    		
	    		
	    		
	    		swingWorker.execute();
			}
		};
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

                setLocation(newX, newY);
            }
        };
	}
}
