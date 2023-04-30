package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import de.minetrain.minechat.main.gui.frames.MainFrame;

public class Main {

	public static void main(String[] args) {
//		JFrame frame = new JFrame();
//		frame.setSize(500, 700);
////		frame.getContentPane().setBackground(new Color(48, 48, 48));
//		frame.getContentPane().setBackground(new Color(173, 96, 164));
//		
//		frame.setUndecorated(true);
//		frame.setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 50, 50));
//		
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
		new MainFrame();
	}

}
