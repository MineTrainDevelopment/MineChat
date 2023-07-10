package de.minetrain.minechat.gui.frames.settings.tabs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.frames.settings.editors.CustomiseTimeFormatFrame;
import de.minetrain.minechat.gui.utils.ColorManager;

public class SettingsTab_Appearance extends SettingsTab{
	private static final long serialVersionUID = -2903086341948789185L;
	private JPanel timeFormatConentPanel, colorsPanelConentPanel;
	
	public SettingsTab_Appearance(JFrame parentFrame) {
		super(parentFrame, "Appearance");
		
		TitledBorder border = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Time patterns", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		border.setTitleFont(Settings.MESSAGE_FONT);
		border.setTitleColor(ColorManager.FONT);
		
		JPanel timeFormatPanel = new JPanel();
		timeFormatPanel.setBackground(new Color(128, 128, 128));
		timeFormatPanel.setBorder(border);
		timeFormatPanel.setBounds(30, 15, 408, 190);
		timeFormatPanel.setBackground(getBackground());
		timeFormatPanel.setLayout(null);
		add(timeFormatPanel);
		
		timeFormatConentPanel = new JPanel();
		timeFormatConentPanel.setBackground(new Color(98, 98, 98));
		timeFormatConentPanel.setBorder(null);
		timeFormatConentPanel.setBounds(10, 25, 385, 156);
		timeFormatConentPanel.setLayout(null);
		timeFormatPanel.add(timeFormatConentPanel);
		
		addTimeFormatLabel("Messages: ["+Settings.messageTimeFormat+"]", 2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Message-Format", Settings.messageTimeFormat);
				Settings.setMessageTimeFormat(customiser.getCurentInput());
			}
		});
		
		addTimeFormatLabel("Time format: ["+Settings.timeFormat+"]", 41, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Time-Format", Settings.timeFormat);
				Settings.setTimeFormat(customiser.getCurentInput());
			}
		});
		
		addTimeFormatLabel("Date format: ["+Settings.dateFormat+"]", 80, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Date-Format", Settings.dateFormat);
				Settings.setDateFormat(customiser.getCurentInput());
			}
		});
		
		addTimeFormatLabel("Day format: ["+Settings.dayFormat+"]", 119, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Day-Format", Settings.dayFormat);
				Settings.setDayFormat(customiser.getCurentInput());
			}
		});
		
		
		
		TitledBorder colorBorder = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Colors", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		colorBorder.setTitleFont(Settings.MESSAGE_FONT);
		colorBorder.setTitleColor(ColorManager.FONT);
		
		JPanel colorsPanel = new JPanel();
		colorsPanel.setLayout(null);
		colorsPanel.setBorder(colorBorder);
		colorsPanel.setBackground(timeFormatPanel.getBackground());
		colorsPanel.setBounds(30, 216, 408, 260);
		add(colorsPanel);
		
		colorsPanelConentPanel = new JPanel();
		colorsPanelConentPanel.setLayout(null);
		colorsPanelConentPanel.setBorder(null);
		colorsPanelConentPanel.setBackground(timeFormatConentPanel.getBackground());
		colorsPanelConentPanel.setBounds(10, 25, 385, 230);
		colorsPanel.add(colorsPanelConentPanel);

		addColorChangeLabel("Font color.", 2, ColorManager.FONT, ColorManager.FONT_DEFAULT, "Font");
		addColorChangeLabel("Background color.", 41, ColorManager.GUI_BACKGROUND, ColorManager.GUI_BACKGROUND_DEFAULT, "Background");
		addColorChangeLabel("Background_Light color.", 80, ColorManager.GUI_BACKGROUND_LIGHT, ColorManager.GUI_BACKGROUND_LIGHT_DEFAULT, "BackgroundLight");
		addColorChangeLabel("Border color.", 119, ColorManager.GUI_BORDER, ColorManager.GUI_BORDER_DEFAULT, "Border");
		addColorChangeLabel("Button color.", 158, ColorManager.GUI_BUTTON_BACKGROUND, ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT, "ButtonBackground");
		addColorChangeLabel("Default key word.", 197, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT, "DefaultKeyHighlight");
		
		colorsPanelConentPanel.add(new JLabel("NOTE: Changing color may need a program restart!"));
	}
	
	private void addTimeFormatLabel(String title, int y, ActionListener changeButtonAction){
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(ColorManager.FONT);
		titleLabel.setBounds(2, y, 282, 32);
		timeFormatConentPanel.add(titleLabel);
		
		JButton changeButton = new JButton("Change");
		changeButton.setBounds(283, y, 100, 32);
		changeButton.setFont(Settings.MESSAGE_FONT);
		changeButton.setForeground(ColorManager.FONT);
		changeButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		changeButton.addActionListener(changeButtonAction);
		timeFormatConentPanel.add(changeButton);
	}
	
	private void addColorChangeLabel(String title, int y, Color curentColor, Color defaultColor, String configName){
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(ColorManager.FONT);
		titleLabel.setBounds(2, y, 200, 32);
		colorsPanelConentPanel.add(titleLabel);
		
		JPanel colorLabel = new JPanel();
		colorLabel.setBackground(curentColor);
		colorLabel.setBounds(207, y, 32, 32);
		colorsPanelConentPanel.add(colorLabel);
		
		JButton changeButton = new JButton("Change");
		changeButton.setBounds(242, y, 95, 32);
		changeButton.setFont(Settings.MESSAGE_FONT);
		changeButton.setForeground(ColorManager.FONT);
		changeButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(getParentFrame(), "Change the "+title, curentColor, true);
				saveNewColor(configName, newColor);
				colorLabel.setBackground(newColor == null ? curentColor : newColor);
			}
		});
		colorsPanelConentPanel.add(changeButton);
		
		JButton defaultButton = new JButton("X");
		defaultButton.setBounds(339, y, 45, 32);
		defaultButton.setFont(Settings.MESSAGE_FONT);
		defaultButton.setForeground(ColorManager.FONT);
		defaultButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		defaultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNewColor(configName, defaultColor);
				colorLabel.setBackground(defaultColor);
			}
		});
		colorsPanelConentPanel.add(defaultButton);
	}
	
	private void saveNewColor(String configName, Color newColor) {
		ColorManager.getSettingsConfig().setString("Colors.GUI."+configName, newColor == null ?  ColorManager.encode(Color.WHITE) : ColorManager.encode(newColor), true);
		ColorManager.loadSettings();
	}
}
