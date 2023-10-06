package de.minetrain.minechat.gui.frames.settings.channel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.main.Main;

public class ChannelSettings_Greeting extends SettingsTab {
	private static final long serialVersionUID = -8669912541714767073L;
	private final List<String> configEntrys;
	private final String configPath;


	public ChannelSettings_Greeting(JFrame parentFrame, ChannelTab tab, String configIndex) {
		super(parentFrame, configIndex);
		
		configPath = "Channel_"+tab.getConfigID()+"."+configIndex;
		configEntrys = Main.CONFIG.getStringList(configPath);
		
		JPanel conentPanel = new JPanel(new BorderLayout());
		conentPanel.setSize(this.getSize());
		
		MineTabel tabel = new MineTabel();
		configEntrys.forEach(entry -> tabel.add(createObj(entry, tabel)));

		JButton button = new JButton();
		button.setText("Test");
		
		
		conentPanel.add(button, BorderLayout.SOUTH);
		conentPanel.add(tabel, BorderLayout.CENTER);
		add(conentPanel, BorderLayout.CENTER);
	}
	
	
	private TabelObj createObj(String entry, MineTabel tabel){
		TabelObj obj = new TabelObj(entry, tabel);
		obj.setDeleteButtonAction(false, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				configEntrys.remove(entry);
			}
		});
		
		
		return obj;
	}

	public void save(){
		Main.CONFIG.setStringList(configPath, configEntrys);
	}
}
