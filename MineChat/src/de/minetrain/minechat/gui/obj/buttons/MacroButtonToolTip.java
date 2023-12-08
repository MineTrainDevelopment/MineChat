package de.minetrain.minechat.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.obj.ChatWindowMessageComponent;
import de.minetrain.minechat.gui.utils.ColorManager;

public class MacroButtonToolTip extends JToolTip{
	private static final Logger logger = LoggerFactory.getLogger(MacroButtonToolTip.class);
	private static final long serialVersionUID = -523796302336801090L;
	
	public MacroButtonToolTip(JComponent component, MacroObject macro) {
		logger.debug("Build tooltip -> "+macro.getTitle());
		setComponent(component);
		setLayout(new BorderLayout());
		setBorder(null);
		
		JPanel messagePanels = new JPanel();
		messagePanels.setLayout(new BoxLayout(messagePanels, BoxLayout.Y_AXIS));
		if(!macro.getRawOutput().equals(">null<")){
			for(int i=0; i < macro.getOutputArray().length; i++){
				messagePanels.add(new ChatWindowMessageComponent(macro, i));
				messagePanels.add(Box.createHorizontalStrut(5));
			}
			
			JPanel content = new JPanel(new BorderLayout());
			content.add(messagePanels, BorderLayout.CENTER);
			add(content);
			setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 3, true));
		}
		
    }
	
	@Override
    public Dimension getPreferredSize() {
        if (getLayout() != null && !isPreferredSizeSet()) {
            return getLayout().preferredLayoutSize(this);
        }
        return super.getPreferredSize();
    }
	
}
