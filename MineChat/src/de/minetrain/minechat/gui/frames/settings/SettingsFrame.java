package de.minetrain.minechat.gui.frames.settings;

import java.awt.Dimension;

import javax.swing.JFrame;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.frames.settings.tabs.SettingsTab_Appearance;
import de.minetrain.minechat.gui.frames.settings.tabs.SettingsTab_Chatting;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class SettingsFrame extends MineDialog{
	private JPanel mainPanel;
	private JPanel buttonPanel;

	public SettingsFrame(JFrame parentFrame) {
		super(parentFrame, "Settings", new Dimension(800, 600));
		
		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(255, 0, 255));
		getContentPanel().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);
		
		JPanel sideBar = new JPanel();
		sideBar.setBackground(ColorManager.GUI_BACKGROUND);
		sideBar.setBounds(0, 0, 200, 610);
		mainPanel.add(sideBar);
		sideBar.setLayout(null);
		
		buttonPanel = new JPanel();
		buttonPanel.setBackground(sideBar.getBackground());
		buttonPanel.setBounds(10, 10, 180, 590);
		buttonPanel.setLayout(new GridLayout(15, 1, 10, 10));
		sideBar.add(buttonPanel);
		
		add(new SettingsTab_Chatting(parentFrame));
		add(new SettingsTab_Appearance(parentFrame));
//		mainPanel.add(new SettingsTab("Test"));
		

//		addContent(mainPanel, BorderLayout.CENTER);
		setConfirmButtonAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setVisible(true);
	}
	
	public void add(SettingsTab tab) {
		buttonPanel.add(tab.getTabButton());
		mainPanel.add(tab);
	}
	
	
}
