package de.minetrain.minechat.gui.frames.settings.channel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboBoxUI;

import de.minetrain.minechat.gui.frames.parant.RoundedJPanel;
import de.minetrain.minechat.gui.obj.CustomJSliderUI;
import de.minetrain.minechat.gui.obj.RoundedLineBorder;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.audio.AudioPath;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.scene.media.AudioClip;

public class ChannelSettingsChannel extends RoundedJPanel{
	private static final long serialVersionUID = 719785712710357457L;
	private Color componentBackground = Color.decode("#8a8a8a");
	
	private JTextField displaynameInputField;
	private JComboBox<AudioPath> audioSelector;
	private JComboBox<String> roleSelector;
	private JSlider volumeSlider;
	private boolean blockAudio = false;
	private HashMap<String, AudioPath> audioPathCache = new HashMap<String, AudioPath>(); //Key: audioPath.getFilePathAsString();
	
	private AudioClip lastAudioClip = null;
	public record ChannelSettingsChannelData(String displayName, String role, AudioPath audioPath, AudioVolume volume){};

	public ChannelSettingsChannel(ChannelSettingsFrame parent, String name) {
		super(parent.getBackground(), parent.getBackground(), 534, 281, 30);
		this.setName(name);
		setBounds(28, 181, getWidth(), getHeight());
		setLayout(null);
		


		
		//Display name
		JPanel displayNameContainer = new RoundedJPanel(componentBackground, componentBackground, 220, 50, 30);
		displayNameContainer.setBorder(new RoundedLineBorder(parent.borderColor, 3, 22));
		displayNameContainer.setBounds(20, 35, 220, 50);
		displayNameContainer.setLayout(null);
		add(displayNameContainer);
		
		JLabel displayNameDescription = new JLabel("Display Name:", SwingConstants.LEFT);
		displayNameDescription.setBounds(23, 15, 214, 15);
		displayNameDescription.setForeground(parent.fontColor);
		displayNameDescription.setFont(new Font("Inter", Font.BOLD, 15));
		add(displayNameDescription);

		displaynameInputField = new JTextField("<NAME>", SwingConstants.CENTER);// 20
		displaynameInputField.setBounds(10, 3, 200, 44);
		displaynameInputField.setForeground(parent.fontColor);
		displaynameInputField.setBackground(componentBackground);
		displaynameInputField.setFont(new Font("Inter", Font.BOLD, 18));
		displaynameInputField.setBorder(BorderFactory.createEmptyBorder());
		displaynameInputField.setHorizontalAlignment(SwingConstants.CENTER);
		displayNameContainer.add(displaynameInputField);
		
		displaynameInputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent event) {
				if(displaynameInputField.getText().length() >= 20){
					event.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				ChannelSettingsFrame parent = (ChannelSettingsFrame) getParent().getParent().getParent().getParent().getParent().getParent();
				parent.setChannelName(displaynameInputField.getText());
			}
		});
		
		
		
		
		
		//Role selector
		JPanel roleSelectorContainer = new RoundedJPanel(componentBackground, componentBackground, 220, 50, 30);
		roleSelectorContainer.setBorder(new RoundedLineBorder(parent.borderColor, 3, 22));
		roleSelectorContainer.setBounds(294, 35, 220, 50);
		roleSelectorContainer.setLayout(null);
		add(roleSelectorContainer);
		
		JLabel roleSelectorDescription = new JLabel("Chat Permissions", SwingConstants.RIGHT);
		roleSelectorDescription.setBounds(294, 15, 214, 15);
		roleSelectorDescription.setForeground(parent.fontColor);
		roleSelectorDescription.setFont(new Font("Inter", Font.BOLD, 15));
		add(roleSelectorDescription);
		
		roleSelector = new JComboBox<String>(new String[]{"Viewer", "Moderator", "VIP"});
		roleSelector.setBounds(10, 3, 200, 44);
		roleSelector.setForeground(parent.fontColor);
		buildComboBox(roleSelector);
		roleSelectorContainer.add(roleSelector);
		
		
		
		
		
		//Audio file selector.
		JPanel audioSelectorContainer = new RoundedJPanel(componentBackground, componentBackground, 494, 50, 30);
		audioSelectorContainer.setBorder(new RoundedLineBorder(parent.borderColor, 3, 22));
		audioSelectorContainer.setBounds(20, 119, 494, 50);
		audioSelectorContainer.setLayout(null);
		add(audioSelectorContainer);


		JLabel audioInfoText = new JLabel("Select a sound for Stream start notification:", SwingConstants.CENTER);
		audioInfoText.setBounds(20, 95, audioSelectorContainer.getWidth(), 25);
		audioInfoText.setForeground(ColorManager.FONT);
		audioInfoText.setFont(new Font("Inter", Font.BOLD, 15));
		add(audioInfoText);
		
		audioSelector = new JComboBox<AudioPath>();
		audioSelector.setBounds(10, 3, 473, 44);
		audioSelector.setForeground(parent.fontColor);
		audioSelector.addItem(new AudioPath());
		AudioManager.scrapeAudioFiles().forEach(path -> {
			AudioPath audioPath = new AudioPath(path);
			audioSelector.addItem(audioPath);
			audioPathCache.put(audioPath.getFilePathAsString(), audioPath);
		});
		
		audioSelector.addActionListener(e -> playTestSound());
		buildComboBox(audioSelector);
		audioSelectorContainer.add(audioSelector);
		
		
		
		
		
		//Volune slider
		JPanel volumeContainer = new RoundedJPanel(parent.backgroundColorDark, parent.borderColor, 494, 34, 25);
		volumeContainer.setBounds(20, 198, volumeContainer.getWidth(), volumeContainer.getHeight());
		volumeContainer.setBorder(new RoundedLineBorder(parent.borderColor, 3, 20));
		volumeContainer.setLayout(null);
		add(volumeContainer);
		
		JLabel volumeIconPanel = new JLabel(new ImageIcon(TextureManager.texturePath + "settingsMenu/volume.png"));
		volumeIconPanel.setBounds(4, 1, 30, 30);
		volumeIconPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){playTestSound();}
		});
		volumeContainer.add(volumeIconPanel);

		JLabel volumeInfoText = new JLabel("Notification Volume: 69%", SwingConstants.CENTER);
		volumeInfoText.setBounds(20, 175, volumeContainer.getWidth(), 25);
		volumeInfoText.setForeground(ColorManager.FONT);
		volumeInfoText.setFont(new Font("Inter", Font.BOLD, 15));
		add(volumeInfoText);
		
		Timer volumeTestTimer = new Timer(1_000, e -> playTestSound());
		volumeTestTimer.setRepeats(false);
		
		volumeSlider = new JSlider(0, 100, 69) {
			private static final long serialVersionUID = -3227651953695921828L;

			@Override
            public void updateUI() {
                CustomJSliderUI sliderUI = new CustomJSliderUI(this);
                sliderUI.setSliderColor(ColorManager.GUI_ACCENT_DEFAULT, ColorManager.GUI_BACKGROUND_LIGHT);
				sliderUI.setBackgroundColor(componentBackground);
				setUI(sliderUI);
            }
        };
		volumeSlider.setBorder(null);
		volumeSlider.setBounds(39, 3, 444, 28);
		volumeSlider.setBackground(volumeContainer.getBackground());
		volumeSlider.addChangeListener(e -> changeVolumeInfoText(volumeInfoText, volumeTestTimer));
		volumeContainer.add(volumeSlider);
		
	}
	
	public void buildComboBox(JComboBox<?> comboBox){
		comboBox.setBorder(null);
		comboBox.setFocusable(false);
		comboBox.setFont(new Font("Inter", Font.BOLD, 20));
		comboBox.setBackground(componentBackground);
		comboBox.setUI(new BasicComboBoxUI() {
			@Override
	        protected void installDefaults() {
	            super.installDefaults();
	            LookAndFeel.uninstallBorder(comboBox);
	        }
			
			@Override
	        protected JButton createArrowButton() {
	            final JButton button = new JButton();
	            button.setName("ComboBox.arrowButton");
	            return button;
	        }


	        @Override
	        public void configureArrowButton() {
	            super.configureArrowButton();
	            arrowButton.setSize(20, 30);
	            arrowButton.setFocusable(false);
	            arrowButton.setOpaque(false);
	            arrowButton.setContentAreaFilled(false);
	            arrowButton.setBorderPainted(false);
	            arrowButton.setIcon(new ImageIcon(Path.of(TextureManager.texturePath+"/settingsMenu/dropdownButton.png").toString()));
	        }
		});
		
		((JLabel) comboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
	}
	
	public ChannelSettingsChannel setData(String displayName, String chatRole, String audioPath, AudioVolume volume){
		blockAudio = true;
		displaynameInputField.setText(displayName);
		roleSelector.setSelectedItem(chatRole);
		audioSelector.setSelectedItem(audioPathCache.computeIfAbsent(audioPath, AudioPath::new));
		volumeSlider.setValue(volume.getIntValue());
		return this;
	}

	public ChannelSettingsChannelData getData(){
		return new ChannelSettingsChannelData(
				displaynameInputField.getText(),
				String.valueOf(roleSelector.getSelectedItem()),
				(AudioPath) audioSelector.getSelectedItem(),
				AudioVolume.get(volumeSlider.getValue()));
	}
	

	private void playTestSound() {
		AudioPath audioPath = (AudioPath) audioSelector.getSelectedItem();
		if(!audioPath.isDummy() && !blockAudio){
			AudioManager.stopAudioClip(lastAudioClip);
			AudioVolume audioVolume = AudioVolume.get(volumeSlider.getValue());
			lastAudioClip = Main.getAudioManager().playAudioClip(audioPath.getUri(), audioVolume);
			System.err.println(audioVolume);
		}
	}

	public void stopTestSound(){
		AudioManager.stopAudioClip(lastAudioClip);
	}
	
	private void changeVolumeInfoText(JLabel volumeInfoText, Timer volumeTestTimer) {
		String volume = String.valueOf(((Math.round((volumeSlider.getValue() / 10.0) * 2) / 2.0) * 10.0)).replace(".0", "");
		volumeInfoText.setText("Notification Volume: "+(volume.equals("0") ? 5 : volume)+"%");
		
		if(blockAudio == true) {
			blockAudio = false;
			return;
		}
		
		volumeTestTimer.restart();
	}
	

}
