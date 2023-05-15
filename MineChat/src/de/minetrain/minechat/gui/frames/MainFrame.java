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
    public MineButton profileButton, queueButton;
    public MacroEmoteButton emoteButton0, emoteButton1, emoteButton2, emoteButton3, emoteButton4, emoteButton5, emoteButton6;
    public MacroButton macroButton0, macroButton1, macroButton2, macroButton3, macroButton4, macroButton5;
    
    public MainFrame() {
    	super("MineChat "+Main.VERSION);
    	setIconImage(Main.TEXTURE_MANAGER.getProgramIcon().getImage());
 		textureLabel = new JLabel();
	    textureLabel.setSize(500, 700);
	    
	    //profile button.
	    profileButton = new MineButton(new Dimension(80, 70), new Point(302, 55), ButtonType.TWITCH_PROFILE).setInvisible(!debug);
	    getContentPane().add(profileButton);

        //Macro row
	    macroButton0 = new MacroButton(new Dimension(80, 30), new Point(17, 55), ButtonType.MACRO_1, this).setInvisible(!debug);
		macroButton1 = new MacroButton(new Dimension(80, 30), new Point(112, 55), ButtonType.MACRO_2, this).setInvisible(!debug);
	    macroButton2 = new MacroButton(new Dimension(80, 30), new Point(207, 55), ButtonType.MACRO_3, this).setInvisible(!debug);
	    macroButton3 = new MacroButton(new Dimension(80, 30), new Point(17, 95), ButtonType.MACRO_4, this).setInvisible(!debug);
	    macroButton4 = new MacroButton(new Dimension(80, 30), new Point(112, 95), ButtonType.MACRO_5, this).setInvisible(!debug);
	    macroButton5 = new MacroButton(new Dimension(80, 30), new Point(207, 95), ButtonType.MACRO_6, this).setInvisible(!debug);
        getContentPane().add(macroButton0);
        getContentPane().add(macroButton1);
        getContentPane().add(macroButton2);
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 55), ButtonType.GREET).setInvisible(!debug));
        getContentPane().add(macroButton3);
        getContentPane().add(macroButton4);
        getContentPane().add(macroButton5);
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 95), ButtonType.SPAM).setInvisible(!debug));
        
        //Emote row.
        emoteButton0 = new MacroEmoteButton(new Dimension(28, 28), new Point(19, 136), ButtonType.EMOTE_1, this).setInvisible(!debug);
        emoteButton1 = new MacroEmoteButton(new Dimension(28, 28), new Point(59, 136), ButtonType.EMOTE_2, this).setInvisible(!debug);
        emoteButton2 = new MacroEmoteButton(new Dimension(28, 28), new Point(99, 136), ButtonType.EMOTE_3, this).setInvisible(!debug);
        emoteButton3 = new MacroEmoteButton(new Dimension(28, 28), new Point(139, 136), ButtonType.EMOTE_4, this).setInvisible(!debug);
        emoteButton4 = new MacroEmoteButton(new Dimension(28, 28), new Point(179, 136), ButtonType.EMOTE_5, this).setInvisible(!debug);
        emoteButton5 = new MacroEmoteButton(new Dimension(28, 28), new Point(219, 136), ButtonType.EMOTE_6, this).setInvisible(!debug);
        emoteButton6 = new MacroEmoteButton(new Dimension(28, 28), new Point(259, 136), ButtonType.EMOTE_7, this).setInvisible(!debug);
        getContentPane().add(emoteButton0);
        getContentPane().add(emoteButton1);
        getContentPane().add(emoteButton2);
        getContentPane().add(emoteButton3);
        getContentPane().add(emoteButton4);
        getContentPane().add(emoteButton5);
        getContentPane().add(emoteButton6);
        
        //Stop QUEUE button.
        queueButton = new MineButton(new Dimension(175, 30), new Point(305, 135), ButtonType.STOP_QUEUE).setInvisible(!debug);
        queueButton.setForeground(Color.WHITE);
		getContentPane().add(queueButton);
		
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