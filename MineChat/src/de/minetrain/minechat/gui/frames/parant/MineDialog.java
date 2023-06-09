package de.minetrain.minechat.gui.frames.parant;

import java.awt.BorderLayout;
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
	private JButton confirmButton;
	private JPanel contentPanel;
	private int mouseX, mouseY;
	private boolean exitOnCancelButton;
	
	/**
	 * @wbp.parser.constructor
	 */
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
        getContentPanel().setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 2));
        getContentPanel().setBackground(ColorManager.GUI_BACKGROUND);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 7));
        mainPanel.setBackground(ColorManager.GUI_BACKGROUND);
        mainPanel.add(createTitleBar(), BorderLayout.NORTH);
        mainPanel.add(getContentPanel(), BorderLayout.CENTER);

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
		cancelButton.addActionListener(getDefaultWindowCloseAction(true));
		
		confirmButton = new JButton("");
		confirmButton.setIcon(Main.TEXTURE_MANAGER.getConfirmButton());
		confirmButton.setBackground(titleBar.getBackground());
		confirmButton.setMinimumSize(new Dimension(30, 30));
		confirmButton.setBorder(null);
		confirmButton.addActionListener(getDefaultWindowCloseAction(false));

		buttonPanal.add(confirmButton, BorderLayout.WEST);
		buttonPanal.add(Box.createHorizontalStrut(2), BorderLayout.CENTER);
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
						System.exit(1);
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
	
	@Override
	public void setTitle(String title) {
		title = title.isEmpty() ? " " : title;
		super.setTitle(title);
		titleText.setText(title);
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
		getContentPanel().add(comp, borderPosion);
		return this;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}
	
}
