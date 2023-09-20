package de.minetrain.minechat.gui.frames.settings.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.frames.settings.editors.CustomiseTimeFormatFrame;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.gui.utils.ColorManager;

public class SettingsTab_Appearance extends SettingsTab{
	private static final long serialVersionUID = -2903086341948789185L;
	
	public SettingsTab_Appearance(JFrame parentFrame) {
		super(parentFrame, "Appearance");
		
		TitledBorder border = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Time patterns", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		border.setTitleFont(Settings.MESSAGE_FONT);
		border.setTitleColor(ColorManager.FONT);
		
		JPanel timeFormatPanel = new JPanel(new BorderLayout());
		timeFormatPanel.setBackground(new Color(128, 128, 128));
		timeFormatPanel.setBorder(border);
		timeFormatPanel.setBounds(30, 15, 408, 233);
		timeFormatPanel.setBackground(getBackground());
		add(timeFormatPanel);
		
		MineTabel timeTabel = new MineTabel();
		timeFormatPanel.add(timeTabel, BorderLayout.CENTER);
		addTimeFormatItmes(parentFrame, timeTabel);

		
		
		
		TitledBorder colorBorder = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Colors - Changes may require a program restart.", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		colorBorder.setTitleFont(Settings.MESSAGE_FONT);
		colorBorder.setTitleColor(ColorManager.FONT);
		
		JPanel colorsPanel = new JPanel(new BorderLayout());
		colorsPanel.setBorder(colorBorder);
		colorsPanel.setBackground(timeFormatPanel.getBackground());
		colorsPanel.setBounds(30, 256, 408, 315);
		add(colorsPanel);
		
		MineTabel colorTabel = new MineTabel();
		colorsPanel.add(colorTabel, BorderLayout.CENTER);
		addColorFormatItmes(parentFrame, colorTabel);
	}

	private void addTimeFormatItmes(JFrame parentFrame, MineTabel mineTabel) {
		mineTabel.clear();
		mineTabel.add(new TabelObj("Messages: ["+Settings.messageTimeFormat+"]", mineTabel).setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Message-Format", Settings.messageTimeFormat);
				Settings.setMessageTimeFormat(customiser.getCurentInput());
				addTimeFormatItmes(parentFrame, mineTabel);
			}
		}));
		
		mineTabel.add(new TabelObj("Time format: ["+Settings.timeFormat+"]", mineTabel).setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Time-Format", Settings.timeFormat);
				Settings.setTimeFormat(customiser.getCurentInput());
				addTimeFormatItmes(parentFrame, mineTabel);
			}
		}));
		
		mineTabel.add(new TabelObj("Date format: ["+Settings.dateFormat+"]", mineTabel).setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Date-Format", Settings.dateFormat);
				Settings.setDateFormat(customiser.getCurentInput());
				addTimeFormatItmes(parentFrame, mineTabel);
			}
		}));
		
		mineTabel.add(new TabelObj("Day format: ["+Settings.dayFormat+"]", mineTabel).setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CustomiseTimeFormatFrame customiser = new CustomiseTimeFormatFrame(parentFrame, "Day-Format", Settings.dayFormat);
				Settings.setDayFormat(customiser.getCurentInput());
				addTimeFormatItmes(parentFrame, mineTabel);
			}
		}));
	}

	private void addColorFormatItmes(JFrame parentFrame, MineTabel mineTabel) {
		mineTabel.clear();
		createColorTableObj("Font color.", ColorManager.FONT, ColorManager.FONT_DEFAULT, "Font", mineTabel);
		createColorTableObj("Background color.", ColorManager.GUI_BACKGROUND, ColorManager.GUI_BACKGROUND_DEFAULT, "Background", mineTabel);
		createColorTableObj("Background_Light color.", ColorManager.GUI_BACKGROUND_LIGHT, ColorManager.GUI_BACKGROUND_LIGHT_DEFAULT, "BackgroundLight", mineTabel);
		createColorTableObj("Border color.", ColorManager.GUI_BORDER, ColorManager.GUI_BORDER_DEFAULT, "Border", mineTabel);
		createColorTableObj("Button color.", ColorManager.GUI_BUTTON_BACKGROUND, ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT, "ButtonBackground", mineTabel);
		createColorTableObj("Default key word.", ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT, "DefaultKeyHighlight", mineTabel);
	}
	
	private void createColorTableObj(String title, Color curentColor, Color defaultColor, String configName, MineTabel mineTabel){
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		
		JLabel titleLabel = new JLabel("  "+title);
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(ColorManager.FONT);
		contentPanel.add(titleLabel, BorderLayout.CENTER);
		
		JPanel colorLabel = new JPanel();
		colorLabel.setBorder(new LineBorder(contentPanel.getBackground(), 4, false));
		colorLabel.setBackground(curentColor);
		colorLabel.setPreferredSize(new Dimension(36, 28));
		contentPanel.add(colorLabel, BorderLayout.EAST);
		
		TabelObj tabelObj = new TabelObj(contentPanel, mineTabel);
		tabelObj.setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(getParentFrame(), "Change the "+title, curentColor, true);
				saveNewColor(configName, newColor);
				colorLabel.setBackground(newColor == null ? curentColor : newColor);
			}
		});
		
		tabelObj.setDeleteButtonAction(true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNewColor(configName, defaultColor);
				colorLabel.setBackground(defaultColor);
			}
		});
		
		mineTabel.add(tabelObj);
	}
	
	
	private void saveNewColor(String configName, Color newColor) {
		ColorManager.getSettingsConfig().setString("Colors.GUI."+configName, newColor == null ?  ColorManager.encode(Color.WHITE) : ColorManager.encode(newColor), true);
		ColorManager.loadSettings();
	}
}
