package de.minetrain.minechat.main.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.minetrain.minechat.main.gui.objects.ButtonType;
import de.minetrain.minechat.main.gui.objects.MineButton;
import de.minetrain.minechat.main.gui.objects.TabButtonType;
import de.minetrain.minechat.main.gui.objects.TitleBar;

public class MainFrame extends JFrame{
    public static boolean debug = false;
    
    public MainFrame() {
    	//Textrue label.
 		JLabel textureLabel = new JLabel();
	    textureLabel.setSize(500, 700);

        //Macro row one
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(17, 55), ButtonType.MACRO_1).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(112, 55), ButtonType.MACRO_2).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(207, 55), ButtonType.MACRO_3).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 55), ButtonType.GREET).setInvisible(!debug));
        
        //profile button.
		getContentPane().add(new MineButton(new Dimension(80, 70), new Point(302, 55), ButtonType.TWITCH_PROFILE).setInvisible(!debug));

        //Macro row tow.
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(17, 95), ButtonType.MACRO_4).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(112, 95), ButtonType.MACRO_5).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(207, 95), ButtonType.MACRO_6).setInvisible(!debug));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 95), ButtonType.SPAM).setInvisible(!debug));
		
        TitleBar titleBar = new TitleBar(this, textureLabel);
        titleBar.getTabNames().forEach(panal -> textureLabel.add(panal)); //Add TabTexts
        titleBar.changeTab(TabButtonType.TAB_MAIN, titleBar.mainTab);
        
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

}