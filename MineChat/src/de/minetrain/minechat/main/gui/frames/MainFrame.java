package de.minetrain.minechat.main.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Array;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.minetrain.minechat.main.gui.buttons.ButtonType;
import de.minetrain.minechat.main.gui.buttons.MineButton;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class MainFrame extends JFrame{
    ImageIcon texture = new ImageIcon("MineChatTextur.png");
    public static boolean debug = false;
    
    public MainFrame() {
        // Deaktiviere den Layout-Manager
        setLayout(null);
        
     // Füge die Komponenten zum JFrame hinzu
 		JLabel jLabel = new JLabel(texture);//
	    jLabel.setSize(500, 700);
	    getContentPane().add(jLabel);

        // Erstelle einen Test-Button
        MineButton button = new MineButton(new Dimension(60	, 30), new Point(200, 650), null);
        button.setColors(Color.BLACK, Color.CYAN);
        button.setText("test");
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jLabel.setIcon(jLabel.getIcon() == null ? texture : null);
				debug = (debug == false) ? true : false;
			}
		});
        
        
        
        
       
        getContentPane().add(new TitleBar(this, jLabel));
        getContentPane().add(button);

        //Macro row one
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(17, 55), ButtonType.MACRO_1).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(112, 55), ButtonType.MACRO_2).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(207, 55), ButtonType.MACRO_3).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 55), ButtonType.GREET).setInvisible(false));
        
        //profile button.
		getContentPane().add(new MineButton(new Dimension(80, 70), new Point(302, 55), ButtonType.TWITCH_PROFILE).setInvisible(false));

        //Macro row tow.
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(17, 95), ButtonType.MACRO_4).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(112, 95), ButtonType.MACRO_5).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(207, 95), ButtonType.MACRO_6).setInvisible(false));
        getContentPane().add(new MineButton(new Dimension(80, 30), new Point(402, 95), ButtonType.SPAM).setInvisible(false));
		
		
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