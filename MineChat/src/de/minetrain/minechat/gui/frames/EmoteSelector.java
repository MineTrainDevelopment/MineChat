package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

public class EmoteSelector extends JDialog{
	private static final long serialVersionUID = 8337999985260635877L;
	private String folderPath = TextureManager.texturePath+"Icons/";
	private final EmoteSelector thisObect;
    private JPanel emotePanel;
    private MainFrame mainFrame;
    private JScrollPane scrollPane;
    private static final int MAX_EMOTES_PER_ROW = 10;
    private static final int BUTTON_SIZE = 36;
    private int mouseX, mouseY;
    HashMap<String, List<String>> emotes = new HashMap<String, List<String>>();

	public EmoteSelector(MainFrame mainFrame) {
		super(mainFrame, "Emotes", true);
		this.mainFrame = mainFrame;
		thisObect = this;
		
		Main.EMOTE_INDEX.getStringList("index").forEach(channel -> {
			List<String> tempList = new ArrayList<String>();
			Main.EMOTE_INDEX.getStringList(channel).forEach(emote -> {
				tempList.add(folderPath + channel.replace("Channel_", "") +"/"+ emote+"/"+emote+"_1_BG.png");
			});
			
			System.out.println(channel+" -- "+tempList);
			emotes.put(channel, tempList);
		});
		
		System.out.println(emotes);
		

        setResizable(false);
//        setAlwaysOnTop(true);
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
        emotePanel.setBackground(ColorManager.BORDER);
        
        // Erstellen der Buttons
        JButton installButton = new JButton("Install more.");
        installButton.setBackground(ColorManager.BACKGROUND_LIGHT);
        installButton.setForeground(Color.WHITE);
        installButton.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 3));
        installButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){new EmoteDownlodFrame(thisObect);}
		});

        JButton refreshButton = new JButton("refresh");
        refreshButton.setBackground(ColorManager.BACKGROUND_LIGHT);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 3));
        refreshButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		dispose();
        		new EmoteSelector(mainFrame);
        	}
		});
        
        JButton cancelButton = new JButton("Close");
        cancelButton.setBackground(ColorManager.BACKGROUND_LIGHT);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 3));
        cancelButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){dispose();}
		});

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel optionPanel = new JPanel(new GridLayout(1, 2));
        optionPanel.setSize(getWidth(), 40);
        optionPanel.add(installButton);
        optionPanel.add(refreshButton);
        optionPanel.add(cancelButton);
        optionPanel.setBackground(ColorManager.BACKGROUND_LIGHT);
        emotePanel.add(optionPanel);
        
        
        for (Map.Entry<String, List<String>> entry : emotes.entrySet()) {
            List<JButton> buttons = new ArrayList<JButton>();
			for (String emote : entry.getValue()) {
				MineButton mineButton = new MineButton(new Dimension(BUTTON_SIZE, BUTTON_SIZE), null, ButtonType.NON).setInvisible(!MainFrame.debug);
				mineButton.setPreferredSize(mineButton.getSize());
                mineButton.setIcon(new ImageIcon(emote));
                mineButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Emote selected: " + emote);
                    }
                });
                buttons.add(mineButton);
            }
			
			System.out.println(buttons.size() + " - " + MAX_EMOTES_PER_ROW);
			if(buttons.size() < MAX_EMOTES_PER_ROW){
				for(int i = buttons.size(); i < MAX_EMOTES_PER_ROW; i++){
					MineButton dummyButtom = new MineButton(new Dimension(BUTTON_SIZE, BUTTON_SIZE), null, ButtonType.NON).setInvisible(!MainFrame.debug);
					dummyButtom.setPreferredSize(dummyButtom.getSize());
					dummyButtom.setIcon(Main.TEXTURE_MANAGER.getEmoteBorder());
					buttons.add(dummyButtom);
					System.out.println("add - "+buttons.size());
				}
				
			}

			int numRows = (int) Math.ceil((double) buttons.size() / MAX_EMOTES_PER_ROW);
			JPanel buttonPanel = new JPanel(new GridLayout(numRows, MAX_EMOTES_PER_ROW));
			buttonPanel.setBackground(ColorManager.BACKGROUND);
			buttons.forEach(button -> buttonPanel.add(button));
			
			TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2), entry.getKey());
			titledBorder.setTitleJustification(TitledBorder.CENTER);
			titledBorder.setTitleColor(Color.WHITE);
			titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
			buttonPanel.setBorder(titledBorder);
			
//            emotePanel.setSize(getPreferredSize());
//            emotePanel.setLayout(new GridLayout(index/10, MAX_EMOTES_PER_ROW));
            buttonPanel.setSize((numRows*BUTTON_SIZE), (MAX_EMOTES_PER_ROW*BUTTON_SIZE));
            emotePanel.add(buttonPanel);
            emotePanel.add(Box.createVerticalStrut(5));
        }
        
        scrollPane = new JScrollPane(emotePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.BORDER);
        scrollPane.getVerticalScrollBar().setForeground(ColorManager.BUTTON_BACKGROUND);
        scrollPane.getVerticalScrollBar().setBorder(null);
        scrollPane.setPreferredSize(emotePanel.getSize());
        scrollPane.addMouseListener(MoiseListner());
        scrollPane.addMouseMotionListener(mouseMotionListner());
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
	
}



//
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.insets = new Insets(5, 5, 5, 5);
//
//		File folder = new File(folderPath);
//
//        // Überprüfen, ob der Ordner existiert und ob es Channel-Ordner gibt
//        if (!folder.exists() || !folder.isDirectory()) {
//            JOptionPane.showMessageDialog(this, "Der Ordner existiert nicht oder ist kein Ordner: " + folderPath);
//            return;
//        }
//
//        File[] channelFolders = folder.listFiles(File::isDirectory);
//
//        if (channelFolders == null) {
//            JOptionPane.showMessageDialog(this, "Es gibt keine Channel-Ordner im Ordner: " + folderPath);
//            return;
//        }
//
//        for (File channelFolder : channelFolders) {
//            JLabel channelLabel = new JLabel(channelFolder.getName());
//            panel.add(channelLabel, constraints);
//            constraints.gridy++;
//
//            File[] emotes = channelFolder.listFiles();
//            int count = 0;
//
//            for (File emote : emotes) {
//                if (!emote.isFile()) {
//                    continue;
//                }
//
//                JLabel emoteLabel = new JLabel(new ImageIcon(emote.getAbsolutePath()));
//                panel.add(emoteLabel, constraints);
//                constraints.gridx++;
//                count++;
//
//                if (count == 10) {
//                    count = 0;
//                    constraints.gridy++;
//                    constraints.gridx = 0;
//                }
//            }
//
//            if (count > 0) {
//                constraints.gridy++;
//                constraints.gridx = 0;
//            }
//        }
//
//        add(panel);
//        pack();
//        setLocationRelativeTo(mainFrame);