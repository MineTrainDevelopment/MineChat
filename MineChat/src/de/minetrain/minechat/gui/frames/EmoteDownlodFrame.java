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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import kong.unirest.Unirest;

public class EmoteDownlodFrame extends JDialog{
	private static final Logger logger = LoggerFactory.getLogger(EmoteDownlodFrame.class);
	private static final long serialVersionUID = -975812837763868477L;
    private static final int fontSize = 13;
    public enum EmotePlatform{TWITCH, BTTV}
    private JComboBox<String> platformSelector;
    private JTextField channelName = new JTextField();
    private JTextField customEmoteName = new JTextField();
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
        
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        platformSelector = new JComboBox<>(new String[]{"Twitch (Name)", "BTTV (URL)", "BTTV (ChannelName)"});
        platformSelector.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        platformSelector.setFont(new Font(null, Font.BOLD, fontSize));
        platformSelector.setForeground(Color.WHITE);
        platformSelector.setSelectedIndex(0);
        platformSelector.setAlignmentY(CENTER_ALIGNMENT);
        platformSelector.setRenderer(listRenderer);
        
        channelName.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        channelName.setFont(new Font(null, Font.BOLD, fontSize));
        channelName.setForeground(Color.WHITE);
        channelName.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 8, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 2, 3));
        inputPanel.setBackground(ColorManager.GUI_BORDER);
        inputPanel.add(platformSelector);
        inputPanel.add(channelName);
        
        statusBar.setSize(300, 50);
        statusBar.setForeground(Color.ORANGE);
		statusBar.setFont(new Font(null, Font.BOLD, 10));
		statusBar.setTitleFont(new Font(null, Font.BOLD, 15));
		
		
		
		
		
		JLabel description = new JLabel();
        description.setFont(new Font(null, Font.BOLD, fontSize));
        description.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));
        description.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        description.setForeground(Color.WHITE);
        description.setHorizontalAlignment(JTextField.CENTER);
        description.setText("Custom name:");
        
        customEmoteName.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        customEmoteName.setFont(new Font(null, Font.BOLD, fontSize));
        customEmoteName.setForeground(Color.WHITE);
        customEmoteName.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel renamePannel = new JPanel(new GridLayout(1, 2, 8, 5));
        renamePannel.setBorder(BorderFactory.createEmptyBorder(3, 3, 2, 3));
        renamePannel.setBackground(ColorManager.GUI_BORDER);
        renamePannel.add(description);
        renamePannel.add(customEmoteName);
        
        
        
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
        panel.add(inputPanel);
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
						
						switch (platformSelector.getSelectedItem().toString()) {
						case "Twitch (Name)":
							logger.warn("GetTwitchEmotes");
							getTwitchEmotes(getTwitchUser());
							break;

						case "BTTV (ChannelName)":
							getBTTVByChannel(getTwitchUser());
							break;
							
						case "BTTV (URL)":
							getBTTVbyURL();
							break;
						}
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
						}
						return twitchUser;
					}
	    		};
	    		
	    		
	    		
	    		swingWorker.execute();
			}
		};
	}
	
	
	private void getTwitchEmotes(TwitchUserObj twitchUser) {
	    statusBar.setProgress("Get the UserID", 40);
		String userId = twitchUser.getUserId();
		
	    statusBar.setProgress("Get the emote set", 60);
		JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/emotes?broadcaster_id="+userId)// 'https://api.twitch.tv/helix/users?id=141981764&id=4845668'
				.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
				.header("Client-Id", TwitchManager.credentials.getClientID())
				.asString()
				.getBody(), JsonObject.class);
		
		JsonArray jsonArray = fromJson.getAsJsonArray("data");
		String downloadURL = fromJson.get("template").getAsString();
		List<String> emoteList = new ArrayList<String>();
		List<String> emoteTier2List = new ArrayList<String>();
		List<String> emoteTier3List = new ArrayList<String>();
		List<String> emoteBitsList = new ArrayList<String>();
		List<String> emoteFollowList = new ArrayList<String>();
		
		for (int i=0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject entry = jsonElement.getAsJsonObject();
			
			String name = entry.get("name").getAsString();
			String format = (entry.get("format").toString().contains("animated") ? "animated" : "static");
			String fileFormat = ((format.length()>6) ? ".gif" : ".png");
			String emoteID = entry.get("id").getAsString();
			String fileLocation = "Icons/"+userId+"/"+name+"/";
			
		    statusBar.setProgress("Downloading: "+name, StatusBar.getPercentage(jsonArray.size(), i));
			YamlManager config = new YamlManager(TextureManager.texturePath+fileLocation+name+".yml");
			
			String borderImageTyp = "";
			String indexName = name+"%&%"+fileFormat;
			switch (entry.get("tier").getAsString()) {
				case "1000": emoteList.add(indexName); break;
				case "2000": emoteTier2List.add(indexName); borderImageTyp="2"; break;
				case "3000": emoteTier3List.add(indexName); borderImageTyp="3"; break;
				default:
					if(entry.get("emote_type").getAsString().equals("bitstier")){
						 emoteBitsList.add(indexName); borderImageTyp="Bits";
					}else{
						emoteFollowList.add(indexName); borderImageTyp="Follow";
					}
					break;
			}
			
			config.setString("Name", name);
			config.setString("ID", emoteID);
			config.setString("Tier", entry.get("tier").getAsString());
			config.setString("EmoteType", entry.get("emote_type").getAsString());
			config.setString("EmoteSet_Id",entry.get("emote_set_id").getAsString());
			config.setString("Tier",entry.get("tier").getAsString());
			config.setString("Format", format);
			config.setString("Theme", "dark");
			config.saveConfigToFile();
			
			try {
//				"https://static-cdn.jtvnw.net/emoticons/v2/{{id}}/{{format}}/{{theme_mode}}/{{scale}}"
//				downloadURL = downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark");
				System.out.println("Emote -> "+name+" --- "+downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark"));
				for (int index = 1; index < 4; index++) {
					TextureManager.downloadImage(downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark").replace("{{scale}}", index+".0"), fileLocation, name+"_"+index+fileFormat);
				}
				
				TextureManager.mergeEmoteImages(fileLocation, name+"_1"+fileFormat, "emoteBorder"+borderImageTyp+".png", fileFormat);
			} catch (IOException ex) {
				logger.error("Error?", ex);
			}
			
		}

		statusBar.setProgress("Saving data...", 99);
		Collections.sort(emoteList);
		Collections.sort(emoteTier2List);
		Collections.sort(emoteTier2List);
		Collections.sort(emoteBitsList);
		Collections.sort(emoteFollowList);
		emoteList.addAll(emoteTier2List);
		emoteList.addAll(emoteTier3List);
		emoteList.addAll(emoteBitsList);
		emoteList.addAll(emoteFollowList);
		
		if(jsonArray.size()>0){
			List<String> indexList = Main.EMOTE_INDEX.getStringList("index");
			if(!indexList.contains("Channel_"+twitchUser.getUserId())){
				indexList.add("Channel_"+twitchUser.getUserId());
			}
			Main.EMOTE_INDEX.setStringList("index", indexList, false);
			Main.EMOTE_INDEX.setStringList("Channel_"+twitchUser.getUserId(), emoteList, true);
		}
		statusBar.setDone("Download completed!");
	}
	
	private void getBTTVByChannel(TwitchUserObj twitchUser) {
		System.out.println("bttv");
		statusBar.setProgress("Request channel data from BTTV.", 5);
		
		JsonObject responseBody = new Gson().fromJson(Unirest.get("https://api.betterttv.net/3/cached/users/twitch/" + twitchUser.getUserId())
		    .asString()
		    .getBody(), JsonObject.class);
		
		System.out.println("Responds: "+responseBody.getAsString());
		statusBar.setProgress("Downloading data...", 10);
		JsonObject bttvJson1 = responseBody.getAsJsonObject();
		
		// Zugriff auf die Werte im JSON
		JsonArray channelEmotes = bttvJson1.getAsJsonArray("channelEmotes");
		
		// Schleife durch die Kanalemotes und drucke den Code jedes Emotes
		for (JsonElement emote : channelEmotes) {
			String emoteCode = emote.getAsJsonObject().get("code").getAsString();
			System.out.println("Kanalemote-Code: " + emoteCode);
		}
		
		JsonArray sharedEmotes = bttvJson1.getAsJsonArray("sharedEmotes");
		
		// Schleife durch die geteilten Emotes und drucke den Namen jedes Benutzers, der
		// das Emote erstellt hat
		for (JsonElement emote : sharedEmotes) {
			JsonObject emoteObj = emote.getAsJsonObject();
			String creatorName = emoteObj.getAsJsonObject("user").get("displayName").getAsString();
			System.out.println("Erstellt von: " + creatorName);
		}
		//End of BTTV API.
	}
	
	
	private boolean getBTTVbyURL() throws InterruptedException, MalformedURLException, ProtocolException, FileNotFoundException, IOException{
		String BTTV_URL = "https://betterttv.com/emotes/";
		String BTTV_EMOTE_URL = "https://cdn.betterttv.net/emote/";
		if(!channelName.getText().toLowerCase().startsWith(BTTV_URL) || channelName.getText().lastIndexOf("https")>0){
			statusBar.setError("Invalid URL!");
			return false;
		}
		
		String url = channelName.getText().replace(BTTV_URL, BTTV_EMOTE_URL);
		
		String newEmoteName = (customEmoteName.getText().length()>0) ? customEmoteName.getText() : url.replace(BTTV_EMOTE_URL, "");
		String bttvEmotePath = "Icons/bttv/"+newEmoteName+"/";
		
		statusBar.setProgress("Downloading: "+newEmoteName+"_1x", StatusBar.getPercentage(5, 1));
		TextureManager.downloadImage(url+"/1x", bttvEmotePath, newEmoteName+"_1.gif");
		TextureManager.mergeEmoteImages(bttvEmotePath, newEmoteName+"_1.gif", "emoteBorder.png", "gif");
//		TextureManager.downloadImmage(url+"/1x", bttvEmotePath+"png/", newEmoteName+"_1.png");
//		TextureManager.downloadImmage(url+"/1x", bttvEmotePath+"gif/", newEmoteName+"_1.gif");
		
		statusBar.setProgress("Downloading: "+newEmoteName+"_2x", StatusBar.getPercentage(5, 2));
		TextureManager.downloadImage(url+"/2x", bttvEmotePath, newEmoteName+"_2.gif");
//		TextureManager.downloadImmage(url+"/2x", bttvEmotePath+"png/", newEmoteName+"_2.png");
//		TextureManager.downloadImmage(url+"/2x", bttvEmotePath+"gif/", newEmoteName+"_2.gif");
		
		statusBar.setProgress("Downloading: "+newEmoteName+"_3x", StatusBar.getPercentage(5, 3));
		TextureManager.downloadImage(url+"/3x", bttvEmotePath, newEmoteName+"_3.gif");
//		TextureManager.downloadImmage(url+"/3x", bttvEmotePath+"png/", newEmoteName+"_3.png");
//		TextureManager.downloadImmage(url+"/3x", bttvEmotePath+"gif/", newEmoteName+"_3.gif");


		statusBar.setProgress("Saving emote...", StatusBar.getPercentage(5, 4));
		List<String> indexList = Main.EMOTE_INDEX.getStringList("index");
		if(!indexList.contains("Channel_bttv")){
			indexList.add("Channel_bttv");
		}
		
		List<String> emoteList = Main.EMOTE_INDEX.getStringList("Channel_bttv");
		if(!emoteList.contains(newEmoteName+"%&%.gif")){
			emoteList.add(newEmoteName+"%&%.gif");
		}

		Collections.sort(emoteList);
		Main.EMOTE_INDEX.setStringList("index", indexList, false);
		Main.EMOTE_INDEX.setStringList("Channel_bttv", emoteList, true);

		statusBar.setDone("Download completed!");
//		https://betterttv.com/emotes/5d7eefb7c0652668c9e4d394
//		https://cdn.betterttv.net/emote/5d7eefb7c0652668c9e4d394/1x
		return true;
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
