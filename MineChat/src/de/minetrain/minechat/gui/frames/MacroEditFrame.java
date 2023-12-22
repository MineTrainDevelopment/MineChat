package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.gui.emotes.RoundetImageIcon;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.frames.parant.RoundedJPanel;
import de.minetrain.minechat.gui.obj.RoundedLineBorder;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class MacroEditFrame extends MineDialog{
	private static final long serialVersionUID = -8604989674605248499L;
	private Color componentBackground = ColorManager.decode("141414");
	private JTextArea listInputField;
	private JTextField titleInput;
	private JLabel emoteIcon;

	private MacroObject macro;
	private Emote emote;
	private boolean emoteOnly;
	
	private static final String defaultTitle = "Title...";
	private static final String defaultListText = "Output text\r\n"
			+ "Line split (ENTER key) == Random output text list\r\n\r\n"
			+ "You can use all message variables.\r\n"
			+ "  - Like {TIME}, {TOTAL_MESSAGES}...";
	

	public MacroEditFrame(JFrame parentFrame, String title) {
		super(parentFrame, title, new Dimension(400, 250));
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(null);
		contentPanel.setBackground(ColorManager.GUI_BACKGROUND);
		addContent(contentPanel, BorderLayout.CENTER);
		
		JPanel emoteBorder = new RoundedJPanel(ColorManager.GUI_BORDER, ColorManager.GUI_BORDER, 60, 60, 20);
		emoteBorder.setBounds(15, 15, emoteBorder.getWidth(), emoteBorder.getHeight());
		emoteBorder.setLayout(null);
		contentPanel.add(emoteBorder);

//		emoteIcon = new JLabel(new RoundetImageIcon(Path.of("data\\texture\\Icons\\99351845\\emotesv2_662fe5cfd480497f98bd3ec7b953817a\\emotesv2_662fe5cfd480497f98bd3ec7b953817a_2.png"), 20, ColorManager.GUI_BUTTON_BACKGROUND));
//		emote = new JLabel(new RoundetImageIcon(Path.of("data\\texture\\Icons\\99351845\\emotesv2_a164fa4a5298492f8850ae43bbda2bc4\\emotesv2_a164fa4a5298492f8850ae43bbda2bc4_2.gif"), 20, ColorManager.GUI_BUTTON_BACKGROUND));
//		emote = new JLabel(new RoundetImageIcon(Path.of("data\\texture\\Icons\\99351845\\emotesv2_2f6e7f957a37440e92fc33c66be7c0c2\\emotesv2_2f6e7f957a37440e92fc33c66be7c0c2_2.png"), 20, componentBackground));
//		emote = new JLabel(new RoundetImageIcon(Path.of("data/texture/Icons/bttv/5e9c6c187e090362f8b0b9e8/5e9c6c187e090362f8b0b9e8_2.png"), 20, ColorManager.GUI_BUTTON_BACKGROUND));
		emoteIcon = new JLabel();
		emoteIcon.setBounds(2, 2, 56, 56);
		emoteIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setEmoteIcon(null);
			}
		});
		emoteBorder.add(emoteIcon);
		
		
		
		JPanel titlePanel = new RoundedJPanel(componentBackground, ColorManager.GUI_BORDER, 300, 60, 20);
		titlePanel.setBorder(new RoundedLineBorder(ColorManager.GUI_BORDER, 4, 18));
		titlePanel.setBounds(85, 15, titlePanel.getWidth(), titlePanel.getHeight());
		titlePanel.setLayout(null);
		contentPanel.add(titlePanel);
		
		titleInput = new JTextField(defaultTitle);
		titleInput.setBounds(6, 6, titlePanel.getWidth()-12, titlePanel.getHeight()-12);
		titleInput.setBackground(titlePanel.getBackground());
		titleInput.setBorder(null);
		titleInput.setFont(new Font("Inter", Font.BOLD, 30));
		titleInput.setHorizontalAlignment(SwingConstants.CENTER);
		titleInput.setForeground(ColorManager.FONT);
		titleInput.setCaretColor(ColorManager.FONT);
		titlePanel.add(titleInput);
		
		JPanel tablePanel = new RoundedJPanel(componentBackground, ColorManager.GUI_BORDER, 370, 150, 20);
		tablePanel.setBorder(new RoundedLineBorder(ColorManager.GUI_BORDER, 4, 18));
		tablePanel.setBounds(15, 85, tablePanel.getWidth(), tablePanel.getHeight());
		tablePanel.setLayout(null);
		contentPanel.add(tablePanel);
		
		listInputField = new JTextArea(defaultListText);
		listInputField.setForeground(ColorManager.FONT);
		listInputField.setBackground(tablePanel.getBackground());
		listInputField.setFont(new Font("Inter", Font.PLAIN, 12));
		listInputField.setColumns(10);
		listInputField.setCaretColor(listInputField.getForeground());
		listInputField.setBorder(BorderFactory.createEmptyBorder());
		listInputField.setVisible(true);
		
		JScrollPane listTextScrollPane = new JScrollPane(listInputField);
		listTextScrollPane.setBounds(6, 6, tablePanel.getWidth()-12, tablePanel.getHeight()-12);
		listTextScrollPane.setBackground(listInputField.getBackground());
		listTextScrollPane.setBorder(null);
		listTextScrollPane.setBackground(ColorManager.GUI_BACKGROUND);
		listTextScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		listTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listTextScrollPane.getVerticalScrollBar().setBackground(listTextScrollPane.getBackground());
		listTextScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, tablePanel.getHeight()));
		listTextScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		listTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listTextScrollPane.getHorizontalScrollBar().setBackground(listTextScrollPane.getBackground());
		listTextScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(tablePanel.getHeight(), 12));
		tablePanel.add(listTextScrollPane);
		
		setConfirmButtonAction(event -> saveToDatabase());
		setExitOnCancelButton(false);
	}
	
	private void setEmoteIcon(Emote emote) {
		if(emote == null || emote.isDummyData()){
			Emote newEmote = new EmoteSelector((JFrame) this.getParent(), true).getSelectedEmote();
			
			if(newEmote == null){
				if(this.emote == null){return;}
				setEmoteIcon(this.emote);
			}
			
			setEmoteIcon(newEmote);
			return;
		}
		
		if(titleInput.getText().equals(defaultTitle)){
			titleInput.setText(emote.getName());
		}
		
		//If the macro is emote only, add the current emote name to the input field.
		//   This dose not apply shout the output text already have the emote name as a single line.
		if(emoteOnly){
			if(listInputField.getText().equals(defaultListText)){
				listInputField.setText(emote.getName());
			}else{
				if(Arrays.stream(listInputField.getText().split("\n")).filter(s -> s.equals(emote.getName())).toList().isEmpty()){
					listInputField.setText(listInputField.getText()+" \n"+emote.getName());
				}
			}
		}
		
		this.emote = emote;
		if(!emote.getFileFormat().equals(".gif")){
			this.emoteIcon.setIcon(new RoundetImageIcon(emote.getImageIcon(EmoteSize.MEDIUM), 20, ColorManager.GUI_BUTTON_BACKGROUND));
		}else{
			this.emoteIcon.setIcon(emote.getImageIcon(EmoteSize.MEDIUM));
		}
	}
	
	/**
	 * 
	 * @param macro
	 * @param isEmoteOnly
	 * @return
	 */
	public MacroEditFrame setData(MacroObject macro, boolean isEmoteOnly){
		this.listInputField.setText(macro.getRawOutput().equals(">null<") ? defaultListText : macro.getRawOutput());
		this.titleInput.setText(macro.getTitle().equals("null") ? defaultTitle : macro.getTitle());
		this.macro = macro;
		this.emoteOnly = isEmoteOnly;
		
		setEmoteIcon(macro.getEmote());
		if(this.emote != null){
			setVisible(true);
		}
		return this;
	}

	

	private void saveToDatabase() {
		DatabaseManager.getMacro().insert(
				macro.getMacroId(),
				TitleBar.currentTab.getConfigID(),
				macro.getButtonType(),
				macro.getButtonType().name(),
				TitleBar.currentTab.getMacros().getCurrentMacroRow().name(),
				titleInput.getText(),
				emote.getEmoteId(),
				listInputField.getText().strip().trim().split("\\r?\\n"));
		
		DatabaseManager.commit();
		TitleBar.currentTab.loadMacros(TitleBar.currentTab.getConfigID());
		Main.MAIN_FRAME.getTitleBar().changeTab(TitleBar.currentTab);
		setVisible(false);
	}

}
