package de.minetrain.minechat.gui.obj.panels.tabel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class TabelObj extends JPanel{
	private static final long serialVersionUID = -665903097350753514L;
	private JPanel contentPanel = new JPanel(new BorderLayout());
	private JLabel indexText = new JLabel("Index");
	private JButton deleteButton = new JButton("");
	private JButton editButton = new JButton("");
	private MineTabel parentTabel;
	private JPanel buttonPanal;

	public TabelObj(Component component, MineTabel parentTabel) {
		super(new BorderLayout());
		contentPanel.add(component, BorderLayout.CENTER);
		this.parentTabel = parentTabel;
		createPanel();
	}
	
	public TabelObj(String text, MineTabel parentTabel) {
		super(new BorderLayout());
		this.parentTabel = parentTabel;
		
		JTextArea titleText = new JTextArea("  "+text+"");
		titleText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		titleText.setForeground(ColorManager.FONT);
		titleText.setFont(new Font(null, Font.BOLD, 16));
//		titleText.setHorizontalAlignment(SwingConstants.LEFT);
		titleText.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		titleText.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
		titleText.setEditable(false);
		titleText.setLineWrap(true);
		titleText.setWrapStyleWord(true);
		titleText.setBorder(null);

	    contentPanel.add(titleText, BorderLayout.CENTER);
		createPanel();
	}
	
	private TabelObj setIndex(MineTabel parentPanel){
		setIndex(String.valueOf((parentPanel.itemIndex++) + 1));
		return this;
	}
	
	public TabelObj setIndex(String text){
		indexText.setText(text.length() < 3 ? (text.length() == 1 ? "  "+text+"  " : " "+text+" ") : text);
		return this;
	}
	
	/**
	 * 
	 * @param removeListeners WARNING: This removes ALL Action listner.
	 * @param actionListener
	 * @return
	 */
	public TabelObj setDeleteButtonAction(Boolean removeListeners, ActionListener actionListener) {
		if(removeListeners){
			Arrays.asList(deleteButton.getActionListeners()).forEach(listner -> deleteButton.removeActionListener(listner));
		}
		deleteButton.addActionListener(actionListener);
		deleteButton.setVisible(true);
		return this;
	}
	
	public TabelObj setEditButtonAction(ActionListener actionListener) {
		Arrays.asList(editButton.getActionListeners()).forEach(listner -> editButton.removeActionListener(listner));
		editButton.addActionListener(actionListener);
		return this;
	}
	
	public TabelObj overrideOptionPanel(Component component) {
		buttonPanal.removeAll();
		buttonPanal.add(component);
		contentPanel.revalidate();
		contentPanel.repaint();
		return this;
	}
	
	private void createPanel(){
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 3, 2));
		this.setBackground(ColorManager.GUI_BORDER);
		
		contentPanel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 4, ColorManager.GUI_BORDER));
		contentPanel.setBackground(ColorManager.GUI_BACKGROUND);
		

		setIndex(parentTabel);
		indexText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		indexText.setForeground(ColorManager.FONT);
		indexText.setFont(new Font(null, Font.BOLD, 15));
		indexText.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel indexPanel = new JPanel();
		indexPanel.setBackground(ColorManager.GUI_BACKGROUND);
		indexPanel.add(indexText, BorderLayout.CENTER);
		
		
		
		
		buttonPanal = new JPanel(new BorderLayout());
		buttonPanal.setBorder(new EmptyBorder(5, 3, 5, 3));
		buttonPanal.setBackground(contentPanel.getBackground());
		
		deleteButton.setIcon(Main.TEXTURE_MANAGER.getCancelButton());
		deleteButton.setBackground(contentPanel.getBackground());
		deleteButton.setMinimumSize(new Dimension(30, 30));
		deleteButton.setBorder(null);
		deleteButton.setToolTipText("Delte this item");
		deleteButton.setVisible(false);
		deleteButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){removeTabelObj();}
		});
		
		editButton = new JButton("");
		editButton.setIcon(Main.TEXTURE_MANAGER.getEditButton());
		editButton.setBackground(contentPanel.getBackground());
		editButton.setMinimumSize(new Dimension(30, 30));
		editButton.setBorder(null);
		editButton.setToolTipText("edit");

		buttonPanal.add(editButton, BorderLayout.WEST);
		buttonPanal.add(Box.createHorizontalStrut(2), BorderLayout.CENTER);
		buttonPanal.add(deleteButton, BorderLayout.EAST);
		
		
		add(buttonPanal, BorderLayout.EAST);
		add(contentPanel, BorderLayout.CENTER);
		add(indexPanel, BorderLayout.WEST);
	}
	
	private void removeTabelObj() {
		//Maymbe add a second listner to remove the item from config?
		parentTabel.contentPanel.remove(this);
		parentTabel.itemIndex--;
		parentTabel.contentPanel.revalidate();
		parentTabel.contentPanel.repaint();
	}
	
}
