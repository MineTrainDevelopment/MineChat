package de.minetrain.minechat.utils.message.tokens;

import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.utils.MineTextFlow;
import de.minetrain.minechat.utils.message.Message;
import de.minetrain.minechat.utils.message.MessageToken;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class UserNameToken extends MessageToken{

	public UserNameToken(String userName) {
		super(userName);
	}

	@Override
	public void appendNode(MineTextFlow textFlow) {
		String colorCode = Message.nameColorCache.getColorCode(getRawText().substring(1));
		
		Button button = new Button(getRawText());
		button.setId("message-comp-token-username");
		button.setFocusTraversable(false);
		button.setFont(textFlow.getFont());
		button.setPadding(new Insets(-1, 5, -1, 5));
//		button.setPrefHeight(textFlow.getFontSize() + 4d);
		
		Color nameColor = Color.web(colorCode);
		button.setTextFill(nameColor);
		button.setStyle(
				"-fx-border-color: "+ColorManager.encode(nameColor.darker())+"; "
				+ "-fx-background-color: "+ColorManager.encode(nameColor.darker().darker().darker().darker().darker().darker())+";");
		
		
		textFlow.appendNode(button);
//		textFlow.appendString(getRawText(), nameColor);
	}

}
