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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.minetrain.minechat.main.gui.buttons.MineButton;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class MainFrame extends JFrame{
    ImageIcon texture = new ImageIcon("MineChatTextur.png");
    
    public MainFrame() {
        // Deaktiviere den Layout-Manager
        setLayout(null);
        
     // Füge die Komponenten zum JFrame hinzu
 		JLabel jLabel = new JLabel(texture);
	    jLabel.setSize(500, 700);
	    getContentPane().add(jLabel);

        // Erstelle einen Test-Button
        MineButton button = new MineButton(new Dimension(60	, 30), new Point(200, 650), null, null);
        button.setColors(Color.BLACK, Color.CYAN);
        button.setText("test");
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jLabel.setIcon(jLabel.getIcon() == null ? texture : null);
			}
		});
		
		getContentPane().add(new TitleBar(this));
		getContentPane().add(button);

        // Setze die Größe und Position des Frames
//		getContentPane().setBackground(new Color(48, 48, 48));
		getContentPane().setBackground(new Color(173, 96, 164));
		
		setSize(500, 700);
		setUndecorated(true);
//		setFocusable(true);
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 50, 50));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
    }
	
//    public MainFrame() {
//        // Create the title bar
//        titleBar = new JPanel(new BorderLayout());
//        titleBar.setBackground(Color.BLUE);
//        
//        // Add mouse listeners to the title bar
//        titleBar.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                // Get the current mouse position
//                mouseX = e.getX();
//                mouseY = e.getY();
//            }
//        });
//        
//        titleBar.addMouseMotionListener(new MouseAdapter() {
//            public void mouseDragged(MouseEvent e) {
//                // Get the new mouse position
//                int newX = e.getXOnScreen() - mouseX;
//                int newY = e.getYOnScreen() - mouseY;
//                
//                // Move the frame
//                setLocation(newX, newY);
//            }
//        });
//        
//        //Create the title label
//        titleLabel = new JLabel("Sintica           |           thisEguy           |           gronkh           |           ffow88");
//        titleLabel.setForeground(Color.WHITE);
//        titleLabel.setHorizontalAlignment(JLabel.CENTER);
//        
//        //Create the minimize button
//        settingsButton = new JButton("-");
//        settingsButton.setBackground(Color.GREEN);
//        settingsButton.setPreferredSize(new Dimension(30, 30));
//        settingsButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                setExtendedState(JFrame.ICONIFIED);
//            }
//        });
//        
//        //Create the close button
//        closeButton = new JButton("X");
//        closeButton.setBackground(Color.RED);
//        closeButton.setPreferredSize(new Dimension(30, 30));
//        closeButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                System.exit(0);
//            }
//        });
//        
//        //Add the components to the title bar
//        titleBar.add(titleLabel, BorderLayout.CENTER);
//        titleBar.add(settingsButton, BorderLayout.WEST);
//        titleBar.add(closeButton, BorderLayout.EAST);
//        titleBar.setLocation(100, 100);
//        
//        JButton button = new JButton();
//        ImageIcon icon = new ImageIcon("2.png");
////        button.setIcon(icon);
////        button.setPreferredSize(new Dimension(30, 10));
//        button.setSize(30, 15);
//        button.setText("test");
//        button.setBorder(BorderFactory.createEmptyBorder());
//        button.setBackground(Color.BLACK);
//        button.setLocation(100, 100);
////        add(button);
//       
//        getContentPane().add(titleBar, BorderLayout.NORTH);
//        setUndecorated(true);
//        
//        
//        //Set the size and position of the frame
////		getContentPane().setBackground(new Color(48, 48, 48));
//		getContentPane().setBackground(new Color(173, 96, 164));
//		
//		setSize(500, 700);
//		setUndecorated(true);
//		setFocusable(true);
////		setLayout(null);
//		setResizable(false);
//		setLocationRelativeTo(null);
//		setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 50, 50));
//		
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setVisible(true);
//        
//    }

}