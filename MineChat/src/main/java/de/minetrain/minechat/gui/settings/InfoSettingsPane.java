package de.minetrain.minechat.gui.settings;

import de.minetrain.minechat.gui.viewmodel.AppInfoViewModel;
import de.minetrain.minechat.main.Main;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class InfoSettingsPane extends SettingsContentPane {

	public InfoSettingsPane() {
		setTitle("Info");

		AppInfoViewModel info = Main.getAppInfoViewModel();

		Region icon = new Region();
		icon.getStyleClass().add("info-app-icon");

		Label nameLabel = new Label(info.getAppName());
		nameLabel.getStyleClass().add("info-app-name");

		Label versionLabel = new Label(info.getAppVersion());
		versionLabel.getStyleClass().add("info-version");

		VBox nameBox = new VBox(4, nameLabel, versionLabel);
		nameBox.setAlignment(Pos.CENTER_LEFT);

		HBox headerBox = new HBox(12, icon, nameBox);
		headerBox.setAlignment(Pos.CENTER_LEFT);
		headerBox.setPadding(new Insets(0, 0, 10, 0));

		GridPane details = new GridPane();
		details.setHgap(6);
		details.setVgap(6);

		details.add(headerBox, 1, 0);

		addDetailRow(details, 1, "Java", info.getJavaVersion());
		addDetailRow(details, 2, "OS", info.getOsInfo());
		addDetailRow(details, 3, "Build", info.getFormattedBuildTimestamp());
		addDetailRow(details, 4, "Commit", info.getGitCommit());
		addDetailRow(details, 5, "Branch",  info.getGitBranch());

		Label copyKey = new Label("©");
		GridPane.setHalignment(copyKey, HPos.RIGHT);
		details.add(copyKey, 0, 6);
		details.add(new Label(info.getCopyright()), 1, 6);

		String repoUrl = info.getRepoUrl();
		Hyperlink githubLink = new Hyperlink(repoUrl);
		githubLink.setOnAction(_ -> Main.getApplication().getHostServices().showDocument(repoUrl));
		GridPane.setColumnSpan(githubLink, 2);
		GridPane.setHalignment(githubLink, HPos.CENTER);
		details.add(githubLink, 0, 7);

		VBox content = new VBox(8, details);
		content.setPadding(new Insets(5));
		content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		StackPane centerWrapper = new StackPane(content);
		StackPane.setAlignment(content, Pos.CENTER);
		setCenter(centerWrapper);
	}

	private void addDetailRow(GridPane grid, int row, String key, String value) {
		Label keyLabel = new Label(key + ":");
		GridPane.setHalignment(keyLabel, HPos.RIGHT);
		grid.add(keyLabel, 0, row);
		grid.add(new Label(value), 1, row);
	}
}
