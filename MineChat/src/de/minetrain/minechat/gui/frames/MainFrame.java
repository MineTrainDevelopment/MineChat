package de.minetrain.minechat.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.minetrain.minechat.gui.obj.TabButtonType;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MacroButton;
import de.minetrain.minechat.gui.obj.buttons.MacroEmoteButton;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.main.Main;

public class MainFrame extends JFrame{
	private static final long serialVersionUID = 1795247033275460890L;
	public static boolean debug = false;
    private final JLabel textureLabel;
    private final TitleBar titleBar;
    public MineButton profileButton;
    public MacroEmoteButton emoteButton0, emoteButton1, emoteButton2, emoteButton3, emoteButton4, emoteButton5, emoteButton6;
    
    public MainFrame() {
    	super("MineChat "+Main.VERSION);
    	setIconImage(Main.TEXTURE_MANAGER.getProgramIcon().getImage());
 		textureLabel = new JLabel();
	    textureLabel.setSize(500, 700);

        //Macro row one
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(17, 55), ButtonType.MACRO_1).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(112, 55), ButtonType.MACRO_2).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(207, 55), ButtonType.MACRO_3).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(402, 55), ButtonType.GREET).setInvisible(!debug));
        
        //profile button.
		profileButton = new MineButton(new Dimension(80, 70), new Point(302, 55), ButtonType.TWITCH_PROFILE).setInvisible(!debug);
		getContentPane().add(profileButton);

        //Macro row tow.
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(17, 95), ButtonType.MACRO_4).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(112, 95), ButtonType.MACRO_5).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(207, 95), ButtonType.MACRO_6).setInvisible(!debug));
        getContentPane().add(new MacroButton(new Dimension(80, 30), new Point(402, 95), ButtonType.SPAM).setInvisible(!debug));
        
        //Emote row.
        
        emoteButton0 = new MacroEmoteButton(new Dimension(28, 28), new Point(19, 136), ButtonType.EMOTE_1).setInvisible(!debug);
        emoteButton1 = new MacroEmoteButton(new Dimension(28, 28), new Point(59, 136), ButtonType.EMOTE_2).setInvisible(!debug);
        emoteButton2 = new MacroEmoteButton(new Dimension(28, 28), new Point(99, 136), ButtonType.EMOTE_3).setInvisible(!debug);
        emoteButton3 = new MacroEmoteButton(new Dimension(28, 28), new Point(139, 136), ButtonType.EMOTE_4).setInvisible(!debug);
        emoteButton4 = new MacroEmoteButton(new Dimension(28, 28), new Point(179, 136), ButtonType.EMOTE_5).setInvisible(!debug);
        emoteButton5 = new MacroEmoteButton(new Dimension(28, 28), new Point(219, 136), ButtonType.EMOTE_6).setInvisible(!debug);
        emoteButton6 = new MacroEmoteButton(new Dimension(28, 28), new Point(259, 136), ButtonType.EMOTE_7).setInvisible(!debug);
        getContentPane().add(emoteButton0);
        getContentPane().add(emoteButton1);
        getContentPane().add(emoteButton2);
        getContentPane().add(emoteButton3);
        getContentPane().add(emoteButton4);
        getContentPane().add(emoteButton5);
        getContentPane().add(emoteButton6);
        
        //Stop QUEUE button.
        getContentPane().add(new MineButton(new Dimension(175, 30), new Point(305, 135), ButtonType.STOP_QUEUE).setInvisible(!debug));
		
        this.titleBar = new TitleBar(this, textureLabel);
        titleBar.getTabNames().forEach(panal -> textureLabel.add(panal)); //Add TabTexts
        titleBar.changeTab(TabButtonType.TAB_MAIN, titleBar.getMainTab());
        
		getContentPane().add(titleBar);
        getContentPane().add(textureLabel);
        
        setLayout(null);
		setSize(500, 700);
		setUndecorated(true);
//		setFocusable(true);
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setBackground(new Color(173, 96, 164));
		setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 50, 50));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
    }
    
    public void displayInfo(String s) {
		System.err.println(s);
	}

	public TitleBar getTitleBar() {
		return titleBar;
	}

}