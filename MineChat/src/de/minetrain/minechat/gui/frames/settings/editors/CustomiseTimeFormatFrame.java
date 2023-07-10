package de.minetrain.minechat.gui.frames.settings.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.utils.ColorManager;

public class CustomiseTimeFormatFrame extends MineDialog{
	private static final long serialVersionUID = -7981088422237604226L;
	private static final Logger logger = LoggerFactory.getLogger(CustomiseTimeFormatFrame.class);
	private static final String infoUrl = "https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns";
	private final JTextField preview;
	private JComboBox<String> comboBox;
	private String previousFormat;
	private boolean isInputValid;

	public CustomiseTimeFormatFrame(JFrame parentFrame, String name, String curentFormat) {
		super(parentFrame, "Customise the time format for: "+name, new Dimension(400, 80));		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(getContentPanel().getBackground());
		this.previousFormat = curentFormat;
		
		preview = new JTextField();
		preview.setBorder(new LineBorder(new Color(40, 40, 40), 8));
		preview.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		preview.setForeground(ColorManager.FONT);
		preview.setEditable(false);
		preview.setFont(Settings.MESSAGE_FONT);
		preview.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(preview, BorderLayout.NORTH);
		
		comboBox = new JComboBox<String>();
		comboBox.setEditable(true);
		comboBox.addItem("");
		comboBox.addItem(curentFormat);
		comboBox.addItem("HH:mm");
		comboBox.addItem("HH:mm:ss");
		comboBox.addItem("eeee");
		comboBox.addItem("dd.MM.yyy");
		comboBox.addItem("dd.MM.yyy - HH:mm");
		comboBox.addItem("eeee - dd.MM.yyy - HH:mm:ss");
		comboBox.setSelectedItem(curentFormat);
		comboBox.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e){refresh();}
		});
		comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
		    public void keyReleased(KeyEvent e){refresh();}
		});
		
		
		JButton infoButton = new JButton("Info");
		infoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(infoUrl));
				} catch (IOException | URISyntaxException ex) {
					logger.warn("Can´t open a web side.", ex);
				}
			}
		});
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBackground(mainPanel.getBackground());
		inputPanel.setLayout(new FlowLayout());
		inputPanel.add(comboBox, BorderLayout.WEST);
		inputPanel.add(infoButton);
		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		

		getContentPanel().add(mainPanel, BorderLayout.CENTER);
		setConfirmButtonAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				if(isInputValid){
					dispose();
				}
			}
		});
		
		refresh();
		setVisible(true);
		setExitOnCancelButton(true);
	}
	
	private String getCurentTime(String fromat) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(fromat,
				new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}
	
	private void refresh() {
		try {
			preview.setText(getCurentTime(getCurentInput()));
			isInputValid = true;
        } catch (Exception ex) {
            preview.setText(ex.getLocalizedMessage());
            isInputValid = false;
        }
	}
	
	@Override
	public void dispose() {
		isInputValid = preview.getText().isEmpty() ? false : isInputValid;
		super.dispose();
	}

	public String getCurentInput() {
		String input = String.valueOf(comboBox.getEditor().getItem());
		return !isInputValid ? previousFormat : input;
	}

}
