package de.minetrain.minechat.gui.obj.buttons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ChannelTabButton extends Button {
	private static final Logger LOG = LoggerFactory.getLogger(ChannelTabButton.class);
	private static final List<ChannelTabButton> buttons = new ArrayList<>();
	private static final Label messageContainer = new Label("This is a test");
	private static final double MIN_VALUE = 34d;
	private static final int ANIMATION_TIME = 150;

	private Channel channel;
	private TitleBarPane parentTitleBar;
	private boolean liveState = false;

	public ChannelTabButton(Channel channel, TitleBarPane titleBarPane) {
		super(channel.getDisplayName(), getProfilePic(channel.getChannelId(), 24));
		setFocusTraversable(false);
		setId("channel-tab");
		setMinWidth(34d);

		this.channel = channel;
		this.parentTitleBar = titleBarPane;

        setOnDragDetected(event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putHtml(channel.getDisplayName());
            content.putString(channel.getDisplayName());
            content.putUrl("https://www.twitch.tv/"+channel.getLoginName());

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            content.putImage(snapshot(snapshotParameters, null));

            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (!Objects.equals(event.getGestureSource(), this) && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        //Hover effect
        setOnDragEntered(event -> {
        	if(!Objects.equals(event.getGestureSource(), this)){
        		setBorder(new Border(new BorderStroke(Color.AQUA, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2), new Insets(-2))));
        	}
        });

        setOnDragExited(event -> {
        	if(!Objects.equals(event.getGestureSource(), this)){
        		setBorder(null);
        	}
        });

        setOnDragDropped(event -> {
        	Dragboard dragboard = event.getDragboard();
        	boolean success = false;


            if (event.getGestureSource() != null && event.getGestureSource() instanceof ChannelTabButton) {
                Object source = event.getGestureSource();
                int sourceIndex = titleBarPane.getTabBar().getChildren().indexOf(source);
                int targetIndex = titleBarPane.getTabBar().getChildren().indexOf(this);
                ArrayList<Node> nodes = new ArrayList<>(titleBarPane.getTabBar().getChildren());
                if (sourceIndex < targetIndex) {
                    Collections.rotate(nodes.subList(sourceIndex, targetIndex + 1), -1);
                } else {
                    Collections.rotate(nodes.subList(targetIndex, sourceIndex + 1), 1);
                }
                titleBarPane.getTabBar().getChildren().clear();
                titleBarPane.getTabBar().getChildren().addAll(nodes);
                success = true;
            }

            if(!success){
            	try {
            		String url = dragboard.getUrl() != null ? dragboard.getUrl() : dragboard.getString();
            		TwitchUserObj twitchUser = TwitchHelper.extracktUserLoginFromUrl(url).get();
            		ChannelManager channelManager = Main.getChannelManager();

            		if(twitchUser != null && !twitchUser.isDummy()){
            			//Collect all ChannelTabButtons from tabBar.
            			List<ChannelTabButton> channelTabButtons = titleBarPane.getTabBar()
            					.getChildren()
            					.stream()
            					.filter(ChannelTabButton.class::isInstance)
            					.map(ChannelTabButton.class::cast)
            					.collect(Collectors.toList());

						// Select the existing channel tab for the provided user, should it exist.
						if (channelManager.getChannel(twitchUser.getUserId()) != null) {
							Optional<ChannelTabButton> existingChannelTabButton = channelTabButtons.stream()
									.filter(button -> button.channel.getChannelId().equals(twitchUser.getUserId()))
									.findFirst();

							if (existingChannelTabButton.isPresent()) {
								existingChannelTabButton.get().select();
								success = true;
							}
						}

            			//Add a new channel tab button.
						if(!success){
							Channel newChannel = channelManager.addChannel(twitchUser.getUserId());
							if (newChannel != null) {
								ChannelTabButton channelTabButton = new ChannelTabButton(newChannel, titleBarPane);
								int targetIndex = titleBarPane.getTabBar().getChildren().indexOf(this);
								channelTabButtons.add(targetIndex + 1, channelTabButton);
								titleBarPane.getTabBar().getChildren().setAll(channelTabButtons);
								Timeline switchAnimation = new Timeline();
								switchAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(20), e -> channelTabButton.select()));
								switchAnimation.play();
							}
						}
            		}

				} catch (Exception e) {
					LOG.error("", e);
				}
            }

            event.setDropCompleted(success);
            event.consume();
        });

        setOnDragDone(DragEvent::consume);


        //NOTE: Right click gets consumed bsc the computePrefWidth behaves strangely.
        setOnMouseClicked(event -> {
        	if (event.getButton() == MouseButton.SECONDARY) {
        		//Open channel editor
                event.consume();
                return;
            }

        	select();
        });


        if(buttons.size() == 2){setLiveState(true);}
        buttons.add(this);
	}


	public void select() {
		buttons.forEach(button -> {
			button.setId("channel-tab");
			button.setLiveState(button.isLiveState());
			if(button.getMinWidth() != MIN_VALUE && !button.equals(this)){
				Timeline buttonAnimation = new Timeline();
				buttonAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(ANIMATION_TIME), new KeyValue(button.minWidthProperty(), MIN_VALUE, Interpolator.EASE_BOTH)));
				buttonAnimation.play();
			}
		});

		if(getId().equals("channel-tab")){
			setId("channel-tab-selected");
			setLiveState(isLiveState());

			double endWidth = computePrefWidth(Double.NEGATIVE_INFINITY);

			Timeline buttonAnimation = new Timeline();
			KeyFrame key1 = new KeyFrame(Duration.millis(ANIMATION_TIME+20), e -> parentTitleBar.scrollToTab(this));
			KeyFrame key2 = new KeyFrame(Duration.millis(ANIMATION_TIME), new KeyValue(minWidthProperty(), endWidth, Interpolator.EASE_BOTH));
			buttonAnimation.getKeyFrames().addAll(key1, key2);
			buttonAnimation.play();
		}

		ChannelManager channelManager = Main.getChannelManager();
		if (channelManager.setActiveChannel(channel.getChannelId())) {
			channelManager.getActiveChannelActions().loadViewPort();
		};
	}


	@Override
	public double computePrefWidth(double height) {
		return super.computePrefWidth(height);
	}

	private static Rectangle getProfilePic(String channelId, int size) {
		ChannelActions channelActions = Main.getChannelManager().getChannelActions(channelId);
		Rectangle profilePic = channelActions.getProfilePic(size);
		profilePic.setTranslateX(-5);
		return profilePic;
	}

	public void setLiveState(boolean liveState){
		this.liveState = liveState;
		if(liveState && getId() != null){
			setStyle("-fx-background-color: "+ (getId().equals("channel-tab-selected") ? ColorManager.encode(Color.RED) : ColorManager.encode(Color.DARKRED.darker())) +";");
		}else{
			setStyle(null);
		}
	}

	public boolean isLiveState() {
		return liveState;
	}

}
