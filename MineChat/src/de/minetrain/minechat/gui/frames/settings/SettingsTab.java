package de.minetrain.minechat.gui.frames.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.utils.ColorManager;

public class SettingsTab extends JScrollPane{
	private static final long serialVersionUID = -1072072987781069643L;
	private static final List<SettingsTab> tabList = new ArrayList<SettingsTab>();
	private JButton tabButton;
	private JFrame parentFrame;

	public SettingsTab(JFrame parentFrame, String title) {
		tabList.add(this);
		this.parentFrame = parentFrame;
		setBounds(200, 0, 600, 610);
		setLayout(null);
		setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		
		tabButton = new JButton(title);
		tabButton.setFont(Settings.MESSAGE_FONT);
		tabButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){buttonPresed();}
		});
		
		setVisible(true);
	}
	
	public JButton getTabButton(){
		return tabButton;
	}
	
	public JFrame getParentFrame(){
		return parentFrame;
	}
	
	public static final void setVisibility(SettingsTab settingsTab){
		tabList.forEach(tab -> tab.setVisible(tab.equals(settingsTab)));
	}
	
	public void buttonPresed() {
		setVisibility(this);
	}
}
