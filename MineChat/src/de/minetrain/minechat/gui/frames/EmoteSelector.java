package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.OverlayLayout;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.MirroredImageIcon;
import de.minetrain.minechat.gui.emotes.BackgroundImageIcon;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class EmoteSelector extends JDialog{
	private static final long serialVersionUID = 8337999985260635877L;
	private static final Logger logger = LoggerFactory.getLogger(EmoteSelector.class);
	private final EmoteSelector thisObect;
    private JPanel emotePanel;
    private JScrollPane scrollPane;
    private static final int MAX_EMOTES_PER_ROW = 10;
    public static final int BUTTON_SIZE = 36;
    private int mouseX, mouseY;
    private Emote selectedEmote, selectetEmoteFormat;
    private boolean disposed;
    private boolean disposOnSelect;
    private JTextArea textFieldToEdit;
    HashMap<String, ChannelEmotes> emotes;

	public EmoteSelector(MainFrame mainFrame, boolean disposOnSelect) {
		super(mainFrame, "Emotes", true);
		this.disposOnSelect = disposOnSelect;
		thisObect = this;
		emotes = EmoteManager.getChannelEmotes();
		
        setResizable(false);
        setUndecorated(true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        createEmotePanel(mainFrame);
        add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);
	}
	
	private void createEmotePanel(MainFrame mainFrame) {
        emotePanel = new JPanel();
        emotePanel.setSize(new Dimension(400, 400));
        emotePanel.setLayout(new BoxLayout(emotePanel, BoxLayout.Y_AXIS)); 
        emotePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        emotePanel.setBackground(ColorManager.GUI_BORDER);
        
        // Erstellen der Buttons
        JButton installButton = new JButton("Install more.");
        installButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        installButton.setForeground(Color.WHITE);
        installButton.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 3));
        installButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){new EmoteDownlodFrame(thisObect);}
		});

        JButton refreshButton = new JButton("refresh");
        refreshButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 3));
        refreshButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		dispose();
        		new EmoteSelector(mainFrame, disposOnSelect);
        	}
		});
        
        JButton cancelButton = new JButton("Close");
        cancelButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 3));
        cancelButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){disposed = true; dispose();}
		});

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel optionPanel = new JPanel(new GridLayout(1, 2));
        optionPanel.setSize(getWidth(), 40);
        optionPanel.add(installButton);
        optionPanel.add(refreshButton);
        optionPanel.add(cancelButton);
        optionPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        emotePanel.add(optionPanel);
        

        if(!EmoteManager.getFavoriteEmotes().isEmpty()){
        	addEmoteSet(EmoteManager.getDefaultEmotes().values(), "Favorites");
        }
        
    	addEmoteSet(EmoteManager.getDefaultEmotes().values(), "Default");
    	
        for (ChannelEmotes channel : emotes.values()) {
        	addEmoteSet(channel.values(), channel.getChannelName());
        }
        
        scrollPane = new JScrollPane(emotePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.GUI_BORDER);
        scrollPane.getVerticalScrollBar().setForeground(ColorManager.GUI_BUTTON_BACKGROUND);
        scrollPane.getVerticalScrollBar().setBorder(null);
        scrollPane.setPreferredSize(emotePanel.getSize());
        scrollPane.addMouseListener(MoiseListner());
        scrollPane.addMouseMotionListener(mouseMotionListner());
    }

	private void addEmoteSet(Collection<Emote> collection, String setName) {
		List<JButton> buttons = new ArrayList<JButton>();
		
		for (Emote emote : collection) {
			MineButton mineButton = new MineButton(new Dimension(BUTTON_SIZE, BUTTON_SIZE), null, ButtonType.NON).setInvisible(!MainFrame.debug);
			mineButton.setPreferredSize(mineButton.getSize());
		    mineButton.setIcon(new BackgroundImageIcon(emote));
//		    mineButton.setIcon(new MirroredImageIcon(emote.getFilePath()));
		    mineButton.setToolTipText(emote.getName());
		    mineButton.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
					selectedEmote = emote;
		            if(disposOnSelect){
		            	disposed = true;
		            	addSelectetEmoteToText(null);
		            	dispose();
		            }
		        }
		    });
		    

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new OverlayLayout(buttonPanel));
			buttonPanel.setSize(mineButton.getSize());
			buttonPanel.setLocation(mineButton.getLocation());
			
			JLabel background = new JLabel(Main.TEXTURE_MANAGER.getEmoteBorder());
			background.setSize(mineButton.getSize());
			background.setLocation(mineButton.getLocation());
			
			buttonPanel.add(background);
			buttonPanel.add(mineButton);
			buttons.add(mineButton);
		}
		
		int numDummyButtons = MAX_EMOTES_PER_ROW - (buttons.size() % MAX_EMOTES_PER_ROW);
		for(int i = 0; i < numDummyButtons; i++){
			MineButton dummyButton = new MineButton(new Dimension(BUTTON_SIZE, BUTTON_SIZE), null, ButtonType.NON).setInvisible(!MainFrame.debug);
//			JPanel dummyButton = new JPanel();
//			dummyButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
		    dummyButton.setPreferredSize(dummyButton.getSize());
		    dummyButton.add(new JLabel(Main.TEXTURE_MANAGER.getEmoteBorder()));
		    buttons.add(dummyButton);
		}

//			if(buttons.size() < MAX_EMOTES_PER_ROW){
//				for(int i = buttons.size(); i < MAX_EMOTES_PER_ROW; i++){
//					MineButton dummyButton = new MineButton(new Dimension(BUTTON_SIZE, BUTTON_SIZE), null, ButtonType.NON).setInvisible(!MainFrame.debug);
//					dummyButton.setPreferredSize(dummyButton.getSize());
//					dummyButton.setIcon(Main.TEXTURE_MANAGER.getEmoteBorder());
//					buttons.add(dummyButton);
//					System.out.println("add - "+buttons.size());
//				}
			
//			}

		int numRows = (int) Math.ceil((double) buttons.size() / MAX_EMOTES_PER_ROW);
		JPanel buttonPanel = new JPanel(new GridLayout(numRows, MAX_EMOTES_PER_ROW));
		buttonPanel.setBackground(ColorManager.GUI_BACKGROUND);
		buttons.forEach(button -> buttonPanel.add(button));
		
		TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2), setName);
		titledBorder.setTitleJustification(TitledBorder.CENTER);
		titledBorder.setTitleColor(Color.WHITE);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		buttonPanel.setBorder(titledBorder);
		
		buttonPanel.setSize((numRows*BUTTON_SIZE), (MAX_EMOTES_PER_ROW*BUTTON_SIZE));
		emotePanel.add(buttonPanel);
		emotePanel.add(Box.createVerticalStrut(5));
	}
	
	public void addSelectetEmoteToText(JTextArea text){
		if(text != null){textFieldToEdit = text;}
		if(textFieldToEdit == null){return;}
		
		try {
			int position = text.getCaretPosition();
			text.getDocument().insertString(position, " "+selectedEmote.getName()+" ", null);
		} catch (BadLocationException ex) {
			logger.error("Can´t add emote to text. ",ex);
		} catch (NullPointerException ex) { }
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

	public boolean isDisposed() {
		return disposed;
	}

	public Emote getSelectedEmote() {
		return selectedEmote;
	}
}


