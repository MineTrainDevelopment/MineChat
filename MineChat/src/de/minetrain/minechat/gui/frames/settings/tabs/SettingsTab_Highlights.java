package de.minetrain.minechat.gui.frames.settings.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.frames.settings.editors.CreateWordHighlightFrame;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class SettingsTab_Highlights extends SettingsTab{
	private static final long serialVersionUID = 6495719118487859277L;

	public SettingsTab_Highlights(JFrame parentFrame) {
		super(parentFrame, "Highlights");
		
		TitledBorder border = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "General settings:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		border.setTitleFont(Settings.MESSAGE_FONT);
		border.setTitleColor(ColorManager.FONT);
		
		JPanel formatPanel = new JPanel(new BorderLayout());
		formatPanel.setBackground(new Color(128, 128, 128));
		formatPanel.setBorder(border);
		formatPanel.setBounds(30, 15, 500, 500);
		formatPanel.setBackground(getBackground());
		add(formatPanel);
		
		MineTabel tabel = new MineTabel();
		formatPanel.add(tabel, BorderLayout.CENTER);
		loadMessageHighlights(tabel);

		
		JButton addButton = new JButton("--> Add a new Message Highlight <--");
		addButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		addButton.setForeground(ColorManager.FONT);
		addButton.setFont(Settings.MESSAGE_FONT);
		addButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addButton.setVerticalTextPosition(SwingConstants.CENTER);
		addButton.setBounds(80, 530, 400, 50);
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateWordHighlightFrame(Main.MAIN_FRAME).setVisible(true);
				loadMessageHighlights(tabel);
			}
		});
		add(addButton);
		
	}
	
	private void loadMessageHighlights(MineTabel tabel){
		tabel.clear();
		Settings.highlightStrings.values().forEach(highlightString -> createTableObj(highlightString, tabel));
	}
	
	private void createTableObj(HighlightString highlightString, MineTabel mineTabel){
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		
		JLabel titleLabel = new JLabel("  "+highlightString.getWord());
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(Color.decode(highlightString.getWordColorCode()));
		contentPanel.add(titleLabel, BorderLayout.CENTER);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setBorder(new LineBorder(contentPanel.getBackground(), 4, false));
		checkBox.setSelected(highlightString.isAktiv());
		checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
		checkBox.setPreferredSize(new Dimension(36, 28));
		checkBox.setFont(Settings.MESSAGE_FONT);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				highlightString.setAktiv(!highlightString.isAktiv());
				checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
			}
		});
		contentPanel.add(checkBox, BorderLayout.EAST);
		
		TabelObj tabelObj = new TabelObj(contentPanel, mineTabel);
//		tabelObj.setIndex(autoReply.getChannelName());
		tabelObj.setIndexColor(Color.decode(highlightString.getBorderColorCode()));
		tabelObj.setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateWordHighlightFrame(Main.MAIN_FRAME).loadPreset(highlightString).setVisible(true);
				loadMessageHighlights(mineTabel);
			}
		});
		
		tabelObj.setDeleteButtonAction(true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightString.delete();
				loadMessageHighlights(mineTabel);
			}
		});
		
		mineTabel.add(tabelObj);
	}

}
