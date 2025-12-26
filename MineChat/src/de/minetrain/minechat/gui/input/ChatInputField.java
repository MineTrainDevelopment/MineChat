package de.minetrain.minechat.gui.input;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.TextOps;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.util.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.frames.emote_selector.EmoteView;
import de.minetrain.minechat.gui.viewmodel.IEmoteViewModel;
import de.minetrain.minechat.main.Main;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.ModifierValue;

public class ChatInputField extends GenericStyledArea<Void, Either<String, IEmoteViewModel>, TextStyle> {

	private static final Logger LOG = LoggerFactory.getLogger(ChatInputField.class);

	private static final TextOps<String, TextStyle> STYLED_TEXT_OPS = SegmentOps.styledTextOps();
	private static final EmoteOps<TextStyle> EMOTE_OPS = new EmoteOps<>();

	private static final TextOps<Either<String, IEmoteViewModel>, TextStyle> EITHER_OPS = STYLED_TEXT_OPS._or(EMOTE_OPS, (_, _) -> Optional.empty());
	private static final Pattern WORD_FINISHER_PATTERN = Pattern.compile("[\\s!.,-]");
	private static final Pattern LAST_WORD_PATTERN = Pattern.compile("([():;|<>\\/\\w]+)[\\s!.,-]?$");

	private ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>(this, "onAction");

	public ChatInputField() {
		super(
			null,
			(_, _) -> {},
			TextStyle.EMPTY,
			EITHER_OPS,
			e -> e.getSegment().unify(
				text -> createStyledTextNode(t -> {
					t.setText(text);
					t.setStyle(e.getStyle().toCss());
				}),
				emote -> {
					LOG.info("Created emote node with name: {}", emote.getName());
					return createEmoteNode(emote);
				}
			)
		);

		Nodes.addInputMap(this, InputMap.consume(EventPattern.keyPressed(new KeyCodeCombination(KeyCode.ENTER, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP)), _ -> fireActionEvent()));
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (WORD_FINISHER_PATTERN.matcher(text).matches()) {
			List<Either<String,IEmoteViewModel>> segments = getDocument().subSequence(0, end).getParagraph(0).getSegments();
			if (!segments.isEmpty() && segments.getLast().isLeft()) {
				String value = segments.getLast().getLeft();
				Matcher matcher = LAST_WORD_PATTERN.matcher(value);
				if (matcher.find()) {
					String lastWord = matcher.group(1);
					LOG.info("Last word detected: {}", lastWord);
					IEmoteViewModel emote = Main.getEmoteManager().getEmoteByName(Main.getChannelManager().getActiveChanneldId(), lastWord);
					if (emote != null) {
						LOG.info("Replacing last word '{}' with emote '{}'", lastWord, emote.getName());
						replaceWithEmote(end - lastWord.length(), end, emote);
						int offset = lastWord.length() - 1;
						replaceText(start - offset, end - offset, text);
						return;
					} else {
						LOG.info("No emote found for name: {}", lastWord);
					}
				}
			}
		}
		StyledDocument<Void, Either<String, IEmoteViewModel>, TextStyle> doc = ReadOnlyStyledDocument.fromString(text,
				getParagraphStyleForInsertionAt(start), getTextStyleForInsertionAt(start), EITHER_OPS);
		replace(start, end, doc);
	}

	public String getPlainText() {
		StringBuilder sb = new StringBuilder();
		getDocument().getParagraphs().forEach(paragraph -> {
			paragraph.getSegments().forEach(segment -> {
				segment.ifLeft(sb::append);
				segment.ifRight(emote -> sb.append(emote.getName()));
			});
			sb.append("\n");
		});
		// Remove the last newline character added
		if (!sb.isEmpty()) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return onAction;
	}


	public final EventHandler<ActionEvent> getOnAction() {
		return onActionProperty().get();
	}

	public final void setOnAction(EventHandler<ActionEvent> value) {
		onActionProperty().set(value);
	}

	protected void fireActionEvent() {
		EventHandler<ActionEvent> handler = getOnAction();
		if (handler != null) {
			handler.handle(new ActionEvent(this, this));
		}
	}

	private void replaceWithEmote(int start, int end, IEmoteViewModel emote) {
		replace(start, end, ReadOnlyStyledDocument.fromSegment(Either.right(emote), null, TextStyle.EMPTY, EITHER_OPS));
	}

	private static TextExt createStyledTextNode(Consumer<TextExt> applySegment) {
		TextExt t = new TextExt();
		applySegment.accept(t);
		return t;
	}

	private static EmoteView createEmoteNode(IEmoteViewModel emote) {
		EmoteView emoteView = new EmoteView(true) ;
		emoteView.setEmote(emote);
		return emoteView;
	}
}
