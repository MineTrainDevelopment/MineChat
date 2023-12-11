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
import de.minetrain.minechat.features.autoreply.AutoReply;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.frames.settings.editors.CreateAutoReplyFrame;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class SettingsTab_AutoReply extends SettingsTab{
	private static final long serialVersionUID = 6495719118487859277L;

	public SettingsTab_AutoReply(JFrame parentFrame) {
		super(parentFrame, "AutoReply");
		
		TitledBorder border = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "General settings:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		border.setTitleFont(Settings.MESSAGE_FONT);
		border.setTitleColor(ColorManager.FONT);
		
		JPanel formatPanel = new JPanel(new BorderLayout());
		formatPanel.setBackground(new Color(128, 128, 128));
		formatPanel.setBorder(border);
		formatPanel.setBounds(30, 40, 500, 500);
		formatPanel.setBackground(getBackground());
		add(formatPanel);
		
		MineTabel tabel = new MineTabel();
		formatPanel.add(tabel, BorderLayout.CENTER);
		loadAutoReplys(tabel);
		
		JCheckBox checkBox = new JCheckBox("React only on current focused channel tab.");
		checkBox.setToolTipText("If slected: Only reacting and sending messages, for the selectet channel Iab.");
		checkBox.setBorder(new LineBorder(getBackground(), 4, false));
		checkBox.setSelected(!Settings.autoReplyState.isAktiv());
		checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
//		checkBox.setPreferredSize(new Dimension(30, 30));
		checkBox.setBounds(80, 10, 400, 30);
		checkBox.setFont(Settings.MESSAGE_FONT);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Settings.setAutoReplyState(Settings.autoReplyState.toggle());
				checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
			}
		});
		add(checkBox);

		
		JButton addButton = new JButton("--> Add a new AutoReply <--");
		addButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		addButton.setForeground(ColorManager.FONT);
		addButton.setFont(Settings.MESSAGE_FONT);
		addButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addButton.setVerticalTextPosition(SwingConstants.CENTER);
		addButton.setBounds(80, 540, 400, 50);
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateAutoReplyFrame(Main.MAIN_FRAME).setVisible(true);
				loadAutoReplys(tabel);
			}
		});
		add(addButton);
		
	}
	
	private void loadAutoReplys(MineTabel tabel){
		tabel.clear();
		AutoReplyManager.getAutoReplys().entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entry2 -> createTableObj(entry2.getValue(), tabel)));
	}
	
	private void createTableObj(AutoReply autoReply, MineTabel mineTabel){
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		
		JLabel titleLabel = new JLabel("  "+autoReply.getTrigger());
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(ColorManager.FONT);
		contentPanel.add(titleLabel, BorderLayout.CENTER);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setBorder(new LineBorder(contentPanel.getBackground(), 4, false));
		checkBox.setSelected(autoReply.isAktiv());
		checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
		checkBox.setPreferredSize(new Dimension(36, 28));
		checkBox.setFont(Settings.MESSAGE_FONT);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				autoReply.setAktiv(!autoReply.isAktiv());
				checkBox.setBackground(checkBox.isSelected() ? Color.GREEN : Color.RED);
			}
		});
		contentPanel.add(checkBox, BorderLayout.EAST);
		
		TabelObj tabelObj = new TabelObj(contentPanel, mineTabel);
		tabelObj.setIndex(autoReply.getChannelName());
		tabelObj.setEditButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateAutoReplyFrame(Main.MAIN_FRAME).loadPreset(autoReply).setVisible(true);
				loadAutoReplys(mineTabel);
			}
		});
		
		tabelObj.setDeleteButtonAction(true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutoReplyManager.deleteAutoReply(autoReply);
				loadAutoReplys(mineTabel);
			}
		});
		
		mineTabel.add(tabelObj);
	}

}
