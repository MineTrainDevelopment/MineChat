package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.minetrain.minechat.gui.utils.ColorManager;

public class InputFrame extends JFrame {
	private static final long serialVersionUID = 1728088408056991401L;
	private final JTextField nameField;
	private final JTextField outputField;
	private String nameInput, outputInput;
	private boolean dispose;
    private int mouseX, mouseY;

	public InputFrame(MainFrame mainFrame, String leftTitle, String leftValue, String rightTitle, String rightValue) {
		setTitle("Mein JFrame");
        setSize(264, 106);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
		setUndecorated(true);
		setResizable(false);
		setShape(new RoundRectangle2D.Double(0, 0, 264, 106, 25, 25));
		
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 7));
        panel.setBackground(ColorManager.BACKGROUND_LIGHT);
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());

        // Erstellen der Texteingabefelder
        nameField = new JTextField(10);
        nameField.setText(leftValue);
        nameField.setBorder(null);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        
        outputField = new JTextField(10);
        outputField.setText(rightValue);
        outputField.setBorder(null);
        outputField.setHorizontalAlignment(JTextField.CENTER);

        // Erstellen der Labels für die Texteingabefelder
        JLabel nameLabel = new JLabel(leftTitle);
        JLabel outputLabel = new JLabel(rightTitle);
        nameLabel.setForeground(Color.WHITE);
        outputLabel.setForeground(Color.WHITE);

        // Hinzufügen der Labels über den Texteingabefeldern
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        panel.add(nameLabel, constraints);

        constraints.gridx = 1;
        panel.add(outputLabel, constraints);

        // Hinzufügen der Texteingabefelder unter den Labels
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(nameField, constraints);

        constraints.gridx = 1;
        panel.add(outputField, constraints);

        // Erstellen der Buttons
        JButton okButton = new JButton("OK");
        okButton.setBackground(ColorManager.BUTTON_BACKGROUND);
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(null);
        okButton.addActionListener(closeWindow(false));
        
        JButton cancelButton = new JButton("reset");
        cancelButton.setBackground(ColorManager.BACKGROUND);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 5));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        buttonPanel.setBackground(ColorManager.BACKGROUND_LIGHT);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.PAGE_END;
        panel.add(buttonPanel, constraints);

        // Setzen der Breite der Buttons auf die Breite der Textfelder
        int buttonWidth = (nameField.getPreferredSize().width + outputField.getPreferredSize().width) / 2;
        okButton.setPreferredSize(new Dimension(buttonWidth, okButton.getPreferredSize().height));
        cancelButton.setPreferredSize(new Dimension(buttonWidth, cancelButton.getPreferredSize().height));

        
        Point location = mainFrame.getLocation();
        location.setLocation(location.x+125, location.y+250);
		setLocation(location);
		
        getContentPane().add(panel, BorderLayout.CENTER);	
		setVisible(true);
	}
	
	private ActionListener closeWindow(boolean close){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!close){
					System.out.println(nameField.getText());
					nameInput = nameField.getText();
					outputInput = outputField.getText();
				}
				
				dispose = true;
				dispose();
			}
		};
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

	public String getNameInput() {
		return nameInput;
	}

	public String getOutputInput() {
		return outputInput;
	}

	public boolean isDispose() {
		return dispose;
	}

}
