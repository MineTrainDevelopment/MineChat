package de.minetrain.minechat.gui.frames.parant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class MineDialog extends JDialog{
	private static final long serialVersionUID = 4562021738118686842L;
	private final JLabel titleText = new JLabel(ChatStatusPanel.getMineChatStatusText().toString());;
	private JButton infoButton;
	private JButton confirmButton;
	private JPanel contentPanel;
	private int mouseX, mouseY;
	private boolean exitOnCancelButton;
	
	public MineDialog(JFrame parentFrame, String title) {
		super(parentFrame, title, true);
		createPanal(title, new Dimension(400, 100));
	}
	
	public MineDialog(JFrame parentFrame, String title, Dimension dimension) {
		super(parentFrame, title, true);
		createPanal(title, dimension);
	}
	
	public MineDialog(JFrame parentFrame, String title, Dimension dimension, ActionListener confirmButtonAction) {
		super(parentFrame, title, true);
		createPanal(title, dimension);
		setConfirmButtonAction(confirmButtonAction);
	}
	
	public MineDialog(JFrame parentFrame, String title, Dimension dimension, boolean setVisible, ActionListener confirmButtonAction) {
		super(parentFrame, title, true);
		createPanal(title, dimension);
		setConfirmButtonAction(confirmButtonAction);
		setVisible(setVisible);
	}

	private void createPanal(String title, Dimension dimension) {
		setTitle(title);
		setSize(dimension);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 2));
        contentPanel.setBackground(ColorManager.GUI_BACKGROUND);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 7));
        mainPanel.setBackground(ColorManager.GUI_BACKGROUND);
        mainPanel.add(createTitleBar(), BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createTitleBar() {
		titleText.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		titleText.setForeground(ColorManager.FONT);
		titleText.setFont(new Font(null, Font.BOLD, 15));
		titleText.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ColorManager.GUI_BACKGROUND);
		titleBar.add(titleText, BorderLayout.CENTER);
        titleBar.add(Box.createHorizontalStrut(4), BorderLayout.SOUTH);
        
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 3, 2));
		mainPanel.setBackground(ColorManager.GUI_BORDER);
		mainPanel.add(titleBar, BorderLayout.CENTER);
		
		JPanel buttonPanal = new JPanel(new BorderLayout());
		buttonPanal.setBorder(new EmptyBorder(2, 2, 2, 2));
		buttonPanal.setBackground(titleBar.getBackground());
		titleBar.add(buttonPanal, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("");
		cancelButton.setIcon(Main.TEXTURE_MANAGER.getCancelButton());
		cancelButton.setBackground(titleBar.getBackground());
		cancelButton.setMinimumSize(new Dimension(30, 30));
		cancelButton.setBorder(null);
		cancelButton.setToolTipText("Cancel");
		cancelButton.addActionListener(getDefaultWindowCloseAction(true));

		JPanel confirmButtonPanal = new JPanel(new BorderLayout());
		confirmButtonPanal.setBorder(new EmptyBorder(0, 3, 0, 3));
		confirmButtonPanal.setBackground(titleBar.getBackground());
		
		confirmButton = new JButton("");
		confirmButton.setIcon(Main.TEXTURE_MANAGER.getConfirmButton());
		confirmButton.setBackground(titleBar.getBackground());
		confirmButton.setMinimumSize(new Dimension(30, 30));
		confirmButton.setBorder(null);
		confirmButton.setToolTipText("Confirm");
		confirmButton.addActionListener(getDefaultWindowCloseAction(false));
		confirmButtonPanal.add(confirmButton, BorderLayout.CENTER);
		
		infoButton = new JButton("");
		infoButton.setIcon(Main.TEXTURE_MANAGER.getInfoButton());
		infoButton.setBackground(titleBar.getBackground());
		infoButton.setMinimumSize(new Dimension(30, 30));
		infoButton.setBorder(null);
		infoButton.setVisible(false);
		infoButton.setToolTipText("Info");
		infoButton.addActionListener(getDefaultWindowCloseAction(false));

		buttonPanal.add(infoButton, BorderLayout.WEST);
		buttonPanal.add(confirmButtonPanal, BorderLayout.CENTER);
		buttonPanal.add(cancelButton, BorderLayout.EAST);
		mainPanel.addMouseListener(MoiseListner());
		mainPanel.addMouseMotionListener(mouseMotionListner());
        return mainPanel;
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
	
	private ActionListener getDefaultWindowCloseAction(Boolean isCanselt){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCanselt){
					dispose(); 
					if(exitOnCancelButton){
						System.exit(0);
					}
				}
			}
		};
	}
	
	public MineDialog setExitOnCancelButton(boolean state){
		exitOnCancelButton = state;
		return this;
	}

	public MineDialog setConfirmButtonAction(ActionListener actionListener) {
		Arrays.asList(confirmButton.getActionListeners()).forEach(listner -> confirmButton.removeActionListener(listner));
		confirmButton.addActionListener(actionListener);
		return this;
	}

	public MineDialog setInfoButtonAction(ActionListener actionListener) {
		Arrays.asList(infoButton.getActionListeners()).forEach(listner -> infoButton.removeActionListener(listner));
		infoButton.addActionListener(actionListener);
		infoButton.setVisible(true);
		return this;
	}
	
	@Override
	public void setTitle(String title) {
		title = title.isEmpty() ? " " : title;
		super.setTitle(title);
		titleText.setText(title);
	}
	
	public void setTitleColor(Color color){
		titleText.setForeground(color);
	}
	
	@Override
	public void setSize(Dimension d) {
		super.setSize(new Dimension(d.width+18, d.height+53));
	}
	
	/**
	 * @param comp
	 * @param borderPosion Needs to be {@link BorderLayout} for examle {@link BorderLayout#CENTER}
	 */
	public MineDialog addContent(Component comp, String borderPosion) {
		contentPanel.add(comp, borderPosion);
		return this;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}
	
}
