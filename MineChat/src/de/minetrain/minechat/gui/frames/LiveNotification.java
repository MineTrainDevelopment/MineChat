package de.minetrain.minechat.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Taskbar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.emotes.RoundetImageIcon;
import de.minetrain.minechat.gui.frames.parant.RoundedJPanel;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class LiveNotification extends JDialog {
	private static final long serialVersionUID = 1581028519588072500L;
	private int mouseX, mouseY;
	private JLabel iconPanel, channelName, title, categoryName;
	private JPanel strut;
	private Timer timer;
	private ChannelTab tab;
	
    private record UpdateData(ChannelTab tab, String category, String title, String... thumbnailUri){}
    private List<UpdateData> updateQueue = new ArrayList<UpdateData>();

	public LiveNotification() {
		super(Main.MAIN_FRAME, "<title>", false);

		Color backgroundColor = ColorManager.GUI_BACKGROUND;
		Color borderColor = ColorManager.GUI_BORDER;
		Color fontColor = ColorManager.FONT;
		
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBackground(borderColor);
        setUndecorated(true);
        setSize(510, 110);
        setShape(new RoundRectangle2D.Double(0, 0, 510, 110, 25, 25));
//		addMouseListener(MoiseListner()); Disable mouse movement listner.
//		addMouseMotionListener(mouseMotionListner());
		setAlwaysOnTop(true);
		
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Arrays.stream(graphicsEnvironment.getScreenDevices()).forEach(device -> {
        	if (device == graphicsEnvironment.getDefaultScreenDevice()) {
            	Dimension screenSize = device.getDefaultConfiguration().getBounds().getSize();
            	setLocation(screenSize.width - this.getWidth() - 10, screenSize.height - this.getHeight() - 50);
            }
        });

        
        JPanel borderPanel = new JPanel();
        borderPanel.setBackground(borderColor);
        getContentPane().add(borderPanel);
        borderPanel.setLayout(null);
        
		JPanel contentPanel = new RoundedJPanel(backgroundColor, borderColor, 500, 100);
		contentPanel.setBackground(backgroundColor);
		contentPanel.setBounds(5, 5, 500, 100);
		contentPanel.setLayout(null);
		borderPanel.add(contentPanel);//, BorderLayout.CENTER);
		
		iconPanel = new JLabel();
		iconPanel.setBounds(10, 10, 80, 80);
		contentPanel.add(iconPanel);
		
		JLabel liveIconPanel = new JLabel(Main.TEXTURE_MANAGER.getLiveIcon());
		liveIconPanel.setBounds(440, 10, 50, 20);
		contentPanel.add(liveIconPanel);
		
		channelName = new JLabel();
		channelName.setBounds(95, 10, 300, 33);
		channelName.setForeground(fontColor);
		channelName.setFont(new Font("Inter", Font.BOLD, 30));
		contentPanel.add(channelName);
		
		strut = new JPanel();
		strut.setBackground(fontColor);
		contentPanel.add(strut);
		setName("<ChannelName>");
		
		title = new JLabel("<StreamTitle>");
		title.setBounds(95, 44, 400, 15);
		title.setForeground(fontColor);
		title.setFont(new Font("Inter", Font.PLAIN, 12));
		contentPanel.add(title);
		
		JLabel category = new JLabel("Category:");
		category.setBounds(95, 63, 120, 30);
		category.setForeground(ColorManager.decode("#8F8F8F"));
		category.setFont(new Font("Inter", Font.PLAIN, 20));
		contentPanel.add(category);

		categoryName = new JLabel("<StreamCategory>");
		categoryName.setBounds(188, 63, 222, 30);
		categoryName.setForeground(fontColor);
		categoryName.setFont(new Font("Inter", Font.PLAIN, 20));
		contentPanel.add(categoryName);
		
		JButton  button = new JButton();
		button.setFocusable(false);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setIcon(Main.TEXTURE_MANAGER.getNotificationButton());
		button.setBounds(420, 63, 70, 30);
		button.addMouseListener(new MouseAdapter() {
		    public void mouseEntered(MouseEvent event) {
				button.setIcon(Main.TEXTURE_MANAGER.getNotificationButtonHover());
		    }

		    public void mouseExited(MouseEvent event) {
				button.setIcon(Main.TEXTURE_MANAGER.getNotificationButton());
		    }
		    
		    public void mousePressed(MouseEvent event) {
		    	if(tab != null){
		    		Main.MAIN_FRAME.getTitleBar().changeTab(tab);
		    		tab.getChatWindow().chatStatusPanel.requestFocus();
		    		tab = null;
		    	}
		    	setVisible(false);
		    }
		});
		contentPanel.add(button);
		
		timer = new Timer(15_000, e -> setVisible(false));
		timer.setRepeats(false);
		addMouseListener(new MouseAdapter() {
		    public void mouseEntered(MouseEvent event) {timer.stop();}
		    public void mouseExited(MouseEvent event) {timer.restart();}
		});
	}
	
	/**
	 * NOTE: Message might apear later if anther Notification is alrady on display.
	 * @param tab
	 * @param categoryName
	 * @param title
	 * @param thumbnailUri
	 * @return {@link LiveNotification} for method chaining.
	 */
	public LiveNotification setData(ChannelTab tab, String categoryName, String title, String... thumbnailUri){
		updateQueue.add(new UpdateData(tab, categoryName, title, thumbnailUri));
		if(!isVisible()){update();}
		return this;
	}
	
	private boolean update(){
		if(!updateQueue.isEmpty()){
			UpdateData data = updateQueue.remove(0);
			setIcon(data.tab.getProfileImagePath80());
			setName(data.tab.getDisplayName());
			setTitle(data.title);
			setCategory(data.category);
			
			if(iconPanel.getIcon().getIconHeight() == 1 && data.thumbnailUri != null){
				try {
					setIcon(new ImageIcon(ImageIO.read(new URL(data.thumbnailUri[0]))));
				} catch (IOException ex){
					LoggerFactory.getLogger(LiveNotification.class).warn("Can´t load image from uri -> "+Arrays.toString(data.thumbnailUri));
				}
			}
			
			setVisible(true);
			this.timer.restart();
			this.tab = data.tab;
			
			Taskbar taskbar = Taskbar.getTaskbar();
			if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION_WINDOW)) {
				taskbar.requestWindowUserAttention(Main.MAIN_FRAME);
			}
			
			return true;
		}
		return false;
	}
	
	public void setIcon(Path path) {
		iconPanel.setIcon(new RoundetImageIcon(path, 15));
	}
	
	public void setIcon(ImageIcon icon) {
		iconPanel.setIcon(new RoundetImageIcon(icon, 15));
	}
	
	@Override
	public void setName(String name) {
		channelName.setText(name);
		int textWidth = channelName.getFontMetrics(channelName.getFont()).stringWidth(channelName.getText());
		strut.setBounds(95, 42, textWidth <= 300 ? textWidth : 300, 1);
	}
	
	@Override
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	public void setCategory(String categoryName) {
		this.categoryName.setText(categoryName);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b == true ? true : update());
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
