package de.minetrain.minechat.gui.frames.settings.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;

public class SettingsTab_CountVariables extends SettingsTab{
	private static final long serialVersionUID = 6495719118487859277L;

	public SettingsTab_CountVariables(JFrame parentFrame) {
		super(parentFrame, "Count variables");
		
		TitledBorder border = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "General settings:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		border.setTitleFont(Settings.MESSAGE_FONT);
		border.setTitleColor(ColorManager.FONT);
		
		JPanel formatPanel = new JPanel(new BorderLayout());
		formatPanel.setBackground(new Color(128, 128, 128));
		formatPanel.setBorder(border);
		formatPanel.setBounds(30, 15, 500, 600);
		formatPanel.setBackground(getBackground());
		add(formatPanel);
		
		MineTabel tabel = new MineTabel();
		formatPanel.add(tabel, BorderLayout.CENTER);
		loadMessageHighlights(tabel);
		
	}
	
	private void loadMessageHighlights(MineTabel tabel){
		tabel.clear();
		DatabaseManager.getCountVariableDatabase().getAllValues().entrySet().forEach(entry -> createTableObj(entry.getKey(), entry.getValue(), tabel));
	}
	
	private void createTableObj(String name, long value, MineTabel mineTabel){
		TabelObj tabelObj = new TabelObj(name, mineTabel);
		tabelObj.setIndex(String.valueOf(value));
		
		tabelObj.overrideEditButton(Main.TEXTURE_MANAGER.getCopyButton(), "Copy to clipboard");
		tabelObj.setEditButtonAction(event -> {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//			clipboard.setContents(new StringSelection("Display: {C_D_"+name+"}\n"+"Add 1 and display: {C_1_"+name+"}\n"+"Increase and display: {C_"+name+"}"), null);
			clipboard.setContents(new StringSelection(
					"{C_D_"+name+"} - Display.\n"+
					"{C_1_"+name+"} - Add 1 and display.\n"+
					"{C_"+name+"} - Increase and display."), null);
		});
		
		tabelObj.setDeleteButtonAction(true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(DatabaseManager.getCountVariableDatabase().getValue(name) == 0){
					DatabaseManager.getCountVariableDatabase().delete(name);
				}else{
					DatabaseManager.getCountVariableDatabase().clearValue(name);
				}
				
				loadMessageHighlights(mineTabel);
			}
		});
		
		mineTabel.add(tabelObj);
	}

}
