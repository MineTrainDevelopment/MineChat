package de.minetrain.minechat.gui.obj.panels.tabel;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.minetrain.minechat.gui.frames.settings.editors.AddWordHighlightFrame;
import de.minetrain.minechat.gui.frames.settings.editors.CustomiseTimeFormatFrame;
import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.obj.ScrollBarUI;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

import javax.swing.JScrollBar;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JDesktopPane;
import java.awt.Dimension;
import javax.swing.JToggleButton;

public class MineTabel extends JPanel{
	public long itemIndex = 0;
	public JPanel contentPanel = new JPanel();
	
	public MineTabel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(ColorManager.GUI_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.GUI_BORDER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));
        scrollPane.setColumnHeaderView(createColumnHeaderView());
        scrollPane.getVerticalScrollBar().setBorder(null);
		add(scrollPane, BorderLayout.CENTER);
		
		contentPanel.setBackground(new Color(255, 0, 255));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(contentPanel);

		
//		add(new TabelObj(new JButton("Test Button"), this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen?", this));
//		add(new TabelObj("Pferdchen ist ein ganz besonders feines pferdchen! Oder willst du mir da etwar wiederspreichen?", this));
	}
	
	
	public MineTabel clear() {
		itemIndex = 0;
		contentPanel.removeAll();
		contentPanel.revalidate();
		contentPanel.repaint();
		return this;
	}
	
	public MineTabel add(TabelObj tabelObj) {
		contentPanel.add(tabelObj);
		return this;
	}
	
	@Override
	public Component add(Component comp) {
		return contentPanel.add(comp);
	}
	
	private JPanel createColumnHeaderView(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(2, 3, 5, 3));
		panel.setBackground(ColorManager.GUI_BORDER);
		
		

		JLabel indexText = new JLabel("      ");
		indexText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		indexText.setForeground(ColorManager.FONT);
		indexText.setFont(new Font(null, Font.BOLD, 15));
		indexText.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel indexPanel = new JPanel();
		indexPanel.setBackground(ColorManager.GUI_BACKGROUND);
		indexPanel.add(indexText, BorderLayout.CENTER);
		
		
		
		
		JLabel titleText = new JLabel(" Values ");
		titleText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		titleText.setForeground(ColorManager.FONT);
		titleText.setFont(new Font(null, Font.BOLD, 15));
		titleText.setHorizontalAlignment(SwingConstants.LEFT);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 4, ColorManager.GUI_BORDER));
		titlePanel.setBackground(ColorManager.GUI_BACKGROUND);
		titlePanel.setBackground(ColorManager.GUI_BACKGROUND);
		titlePanel.add(titleText, BorderLayout.CENTER);
		
		
		
		
		JLabel buttonText = new JLabel(" Option  ");
		buttonText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		buttonText.setForeground(ColorManager.FONT);
		buttonText.setFont(new Font(null, Font.BOLD, 15));
		buttonText.setHorizontalAlignment(SwingConstants.LEFT);
		
		JPanel buttonPanal = new JPanel(new BorderLayout());
		buttonPanal.setBackground(ColorManager.GUI_BACKGROUND);
		buttonPanal.add(buttonText, BorderLayout.EAST);
		
		
		panel.add(buttonPanal, BorderLayout.EAST);
		panel.add(titlePanel, BorderLayout.CENTER);
		panel.add(indexPanel, BorderLayout.WEST);
		return panel;
	}

}
