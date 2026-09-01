package de.minetrain.minechat.gui.viewmodel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;

public class AppInfoViewModel {

	private static final DateTimeFormatter FORMATTER_BUILD_TIMESTAMP = DateTimeFormatter
		.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
		.withLocale(Locale.getDefault())
		.withZone(ZoneId.systemDefault());


	private ReadOnlyStringWrapper appNameProperty;
	private ReadOnlyStringWrapper appVersionProperty;
	private ReadOnlyObjectWrapper<Instant> buildTimestampProperty;
	private ReadOnlyStringWrapper javaVersionProperty;
	private ReadOnlyStringWrapper osInfoProperty;
	private ReadOnlyStringWrapper gitCommitProperty;
	private ReadOnlyStringWrapper gitBranchProperty;
	private ReadOnlyStringWrapper repoUrlProperty;
	private ReadOnlyStringWrapper copyrightStartProperty;
	private ReadOnlyStringWrapper copyrightHoldersProperty;
	private ObservableValue<String> formattedBuildTimestampBinding;
	private ObservableValue<String> copyrightBinding;

	public AppInfoViewModel(String appName, String appVersion, Instant buildTimestamp, String javaVersion, String osInfo, String gitCommit, String gitBranch, String repoUrl, String copyrightStart, String copyrightHolders) {
		appNamePropertyInternal().set(appName);
		appVersionPropertyInternal().set(appVersion);
		buildTimestampPropertyInternal().set(buildTimestamp);
		javaVersionPropertyInternal().set(javaVersion);
		osInfoPropertyInternal().set(osInfo);
		gitCommitPropertyInternal().set(gitCommit);
		gitBranchPropertyInternal().set(gitBranch);
		repoUrlPropertyInternal().set(repoUrl);
		copyrightStartPropertyInternal().set(copyrightStart);
		copyrightHoldersPropertyInternal().set(copyrightHolders);
	}

	public ReadOnlyStringProperty appNameProperty() {
		return appNamePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper appNamePropertyInternal() {
		if (appNameProperty == null) {
			appNameProperty = new ReadOnlyStringWrapper(this, "appName");
		}
		return appNameProperty;
	}

	public ReadOnlyStringProperty appVersionProperty() {
		return appVersionPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper appVersionPropertyInternal() {
		if (appVersionProperty == null) {
			appVersionProperty = new ReadOnlyStringWrapper(this, "appVersion");
		}
		return appVersionProperty;
	}

	public ReadOnlyObjectProperty<Instant> buildTimestampProperty() {
		return buildTimestampPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<Instant> buildTimestampPropertyInternal() {
		if (buildTimestampProperty == null) {
			buildTimestampProperty = new ReadOnlyObjectWrapper<>(this, "buildTimestamp");
		}
		return buildTimestampProperty;
	}

	public ReadOnlyStringProperty javaVersionProperty() {
		return javaVersionPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper javaVersionPropertyInternal() {
		if (javaVersionProperty == null) {
			javaVersionProperty = new ReadOnlyStringWrapper(this, "javaVersion");
		}
		return javaVersionProperty;
	}

	public ReadOnlyStringProperty osInfoProperty() {
		return osInfoPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper osInfoPropertyInternal() {
		if (osInfoProperty == null) {
			osInfoProperty = new ReadOnlyStringWrapper(this, "osInfo");
		}
		return osInfoProperty;
	}

	public ReadOnlyStringProperty gitCommitProperty() {
		return gitCommitPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper gitCommitPropertyInternal() {
		if (gitCommitProperty == null) {
			gitCommitProperty = new ReadOnlyStringWrapper(this, "gitCommit");
		}
		return gitCommitProperty;
	}

	public ReadOnlyStringProperty gitBranchProperty() {
		return gitBranchPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper gitBranchPropertyInternal() {
		if (gitBranchProperty == null) {
			gitBranchProperty = new ReadOnlyStringWrapper(this, "gitBranch");
		}
		return gitBranchProperty;
	}

	public ReadOnlyStringProperty repoUrlProperty() {
		return repoUrlPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper repoUrlPropertyInternal() {
		if (repoUrlProperty == null) {
			repoUrlProperty = new ReadOnlyStringWrapper(this, "repoUrl");
		}
		return repoUrlProperty;
	}

	public ObservableValue<String> formattedBuildTimestampProperty() {
		if (formattedBuildTimestampBinding == null) {
			formattedBuildTimestampBinding = buildTimestampProperty().map(FORMATTER_BUILD_TIMESTAMP::format).orElse("?");
		}
		return formattedBuildTimestampBinding;
	}

	public ObservableValue<String> copyrightProperty() {
		if (copyrightBinding == null) {
			copyrightBinding = buildTimestampProperty()
				.map(ts -> ts.atZone(ZoneId.systemDefault()).getYear())
				.map(year -> copyrightStartPropertyInternal().get() + " - " + year + "  " + copyrightHoldersPropertyInternal().get())
				.orElse(copyrightStartPropertyInternal().get() + "  " + copyrightHoldersPropertyInternal().get());
		}
		return copyrightBinding;
	}

	public ReadOnlyStringProperty copyrightStartProperty() {
		return copyrightStartPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper copyrightStartPropertyInternal() {
		if (copyrightStartProperty == null) {
			copyrightStartProperty = new ReadOnlyStringWrapper(this, "copyrightStart");
		}
		return copyrightStartProperty;
	}

	public ReadOnlyStringProperty copyrightHoldersProperty() {
		return copyrightHoldersPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper copyrightHoldersPropertyInternal() {
		if (copyrightHoldersProperty == null) {
			copyrightHoldersProperty = new ReadOnlyStringWrapper(this, "copyrightHolders");
		}
		return copyrightHoldersProperty;
	}

	public String getCopyright() {
		return copyrightProperty().getValue();
	}

	public String getAppName() {
		return appNameProperty().get();
	}

	public String getAppVersion() {
		return appVersionProperty().get();
	}

	public Instant getBuildTimestamp() {
		return buildTimestampProperty().get();
	}

	public String getJavaVersion() {
		return javaVersionProperty().get();
	}

	public String getOsInfo() {
		return osInfoProperty().get();
	}

	public String getGitCommit() {
		return gitCommitProperty().get();
	}

	public String getGitBranch() {
		return gitBranchProperty().get();
	}

	public String getRepoUrl() {
		return repoUrlProperty().get();
	}

	public String getFormattedBuildTimestamp() {
		return formattedBuildTimestampProperty().getValue();
	}
}
