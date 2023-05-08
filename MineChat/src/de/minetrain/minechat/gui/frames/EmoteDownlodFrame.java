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
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchCredentials;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.TextureManager;
import kong.unirest.Unirest;

public class EmoteDownlodFrame extends JDialog{
	private static final Logger logger = LoggerFactory.getLogger(EmoteDownlodFrame.class);
	private static final long serialVersionUID = -975812837763868477L;
    private static final int fontSize = 13;
    public enum EmotePlatform{TWITCH, BTTV}
    private JComboBox<String> platformSelector;
    private JTextField channelName = new JTextField();
    public EmoteDownlodFrame thisFrame;
    public StatusBar statusBar = new StatusBar();;
    private int mouseX, mouseY;
    

	public EmoteDownlodFrame(MainFrame mainFrame, String channelName) {
		super(Main.mainFrame, "Emote downloader", ModalityType.MODELESS); //true
		this.channelName.setText(channelName);
		this.statusBar.setProgress("Install emotes?", 0);
		buildingGUI(mainFrame);
		
	}
	
	public EmoteDownlodFrame(MainFrame mainFrame) {
		super(Main.mainFrame, "Emote downloader", ModalityType.MODELESS); //true
		statusBar.setProgress("Waiting for input...", 0);
		buildingGUI(mainFrame);
	}

	private void buildingGUI(MainFrame mainFrame) {
		thisFrame = this;
        setSize(300, 150);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(40, 40, 40));
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(14, 14, 14), 5));
        panel.setBackground(new Color(40, 40, 40));
        panel.setLayout(new GridLayout(3, 2));
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());
        
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        platformSelector = new JComboBox<>(new String[]{"Twitch", "BTTV (Coming soon)"});
        platformSelector.setBackground(new Color(90, 90, 90));
        platformSelector.setFont(new Font(null, Font.BOLD, fontSize));
        platformSelector.setForeground(Color.WHITE);
        platformSelector.setSelectedIndex(0);
        platformSelector.setAlignmentY(CENTER_ALIGNMENT);
        platformSelector.setRenderer(listRenderer);
        
        channelName.setBackground(new Color(90, 90, 90));
        channelName.setFont(new Font(null, Font.BOLD, fontSize));
        channelName.setForeground(Color.WHITE);
        channelName.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 8, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 2, 3));
        inputPanel.setBackground(new Color(14, 14, 14));
        inputPanel.add(platformSelector);
        inputPanel.add(channelName);
        
        statusBar.setSize(300, 50);
        statusBar.setForeground(Color.ORANGE);
		statusBar.setFont(new Font(null, Font.BOLD, 10));
		statusBar.setTitleFont(new Font(null, Font.BOLD, 15));
        
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(new Color(30, 30, 30));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow(false));

//        JButton cancelButton = new JButton("Cancel");
        JButton cancelButton = new JButton("Close");
        cancelButton.setBackground(new Color(30, 30, 30));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 3, 3, 3));
        buttonPanel.setBackground(new Color(14, 14, 14));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        Point location = mainFrame.getLocation();
        location.setLocation(location.x+100, location.y+200);
        setLocation(location);
        
        panel.add(statusBar);
        panel.add(inputPanel);
        panel.add(buttonPanel);
        getContentPane().add(panel);
        setVisible(true);
	}
	
	
	
	private ActionListener closeWindow(Boolean isCanselt){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCanselt){dispose(); return;}
	    		SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() throws Exception {
					
						TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, channelName.getText().replace("!", "").replace("?", ""));
						if(twitchUser.isDummy()){
							channelName.setText("Invalid channel!");
							new Thread(()->{
								for (int i = 0; i < 15; i++) {
									channelName.setForeground(Color.RED);
									try{Thread.sleep(200);}catch(Exception ex){ }
									channelName.setForeground(Color.YELLOW);
									try{Thread.sleep(200);}catch(Exception ex){ }
								}
								channelName.setForeground(Color.WHITE);
							}).start();
							return null;
						}
						
						String userId = twitchUser.getUserId();
						TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), Long.valueOf(userId));

						String helix = "https://api.twitch.tv/helix/";
						String url = (platformSelector.getSelectedItem().toString().equals("Twitch")) ? helix+"chat/emotes?broadcaster_id="+userId : helix+"chat/emotes?broadcaster_id="+userId;
						
						JsonObject fromJson = new Gson().fromJson(Unirest.get(url)// 'https://api.twitch.tv/helix/users?id=141981764&id=4845668'
								.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
								.header("Client-Id", new TwitchCredentials().getClientID())
								.asString()
								.getBody(), JsonObject.class);
						
						System.out.println(fromJson.get("data"));
						JsonArray jsonArray = fromJson.getAsJsonArray("data");
						
				    	for (int i=0; i < jsonArray.size(); i++) {
				    		JsonElement jsonElement = jsonArray.get(i);
				    		JsonObject entry = jsonElement.getAsJsonObject();
				    		
				    		String name = entry.get("name").getAsString();
				    		String fileLocation = "Icons/"+userId+"/"+name+"/";
				    		
				    	    statusBar.setProgress("Downloading: "+name, StatusBar.getPercentage(jsonArray.size(), i));
				    		
				    		ConfigManager config = new ConfigManager(TextureManager.texturePath+fileLocation+name+".yml", true);
				    		config.setString("Name", name);
				    		config.setString("ID", entry.get("id").getAsString());
				    		config.setString("Tier",entry.get("tier").getAsString());
				    		config.setString("EmoteType",entry.get("emote_type").getAsString());
				    		config.setString("EmoteSet_Id",entry.get("emote_set_id").getAsString());
				    		config.setString("format",entry.get("tier").getAsString());
				    		config.setString("Tier",entry.get("tier").getAsString());
				    		config.setString("Format", "static");
				    		config.setString("Theme", "dark");
				    		config.saveConfigToFile();
				    		
				    		try {
				    			TextureManager.downloadImmage(entry.getAsJsonObject("images").get("url_1x").getAsString().replace("light", "dark"), fileLocation, name+"_1.png");
				    			TextureManager.downloadImmage(entry.getAsJsonObject("images").get("url_2x").getAsString().replace("light", "dark"), fileLocation, name+"_2.png");
				    			TextureManager.downloadImmage(entry.getAsJsonObject("images").get("url_4x").getAsString().replace("light", "dark"), fileLocation, name+"_3.png");
				    		} catch (IOException ex) {
				    			logger.error("Error?", ex);
				    		}
				    		
				    	}

			    	    statusBar.setDone("Download completed!");
//				    	dispose();
						return null;
					}
	    		};
	    		
	    		swingWorker.execute();
//						TextureManager.downloadEmotesImage(EmotePlatform.TWITCH, channelName.getText().replace("!", "").replace("?", ""), thisFrame);
//				TextureManager.downloadEmotesImage((platformSelector.getSelectedItem().toString().equals("Twitch") ? EmotePlatform.TWITCH : EmotePlatform.BTTV), channelName.getText().replace("!", "").replace("?", ""), thisFrame);
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
