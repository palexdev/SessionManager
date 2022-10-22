package io.github.palexdev.sessionmanager.ui;

import com.intellij.openapi.project.Project;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.model.SessionInfo;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;

public class DialogUtils {
	//================================================================================
	// Static Members
	//================================================================================
	private static final Logger logger = Logger.getLogger(DialogUtils.class.getName());

	//================================================================================
	// Constructors
	//================================================================================
	private DialogUtils() {
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static SessionInfo saveDialog(Project project) {
		SaveDialog saveDialog = new SaveDialog(project);
		return saveDialog.showAndWait();
	}

	public static Session chooseSessionToLoad(Collection<Session> sessions) {
		LoadDialog loadDialog = new LoadDialog(sessions);
		return loadDialog.showAndWait();
	}

	public static Path loadSessionsFile() {
		return new ChooseFileDialog().chooseFile();
	}

	public static void showGeneric(String title, String text) {
		GenericDialog genericDialog = new GenericDialog(title, text);
		genericDialog.showAndWait();
	}

	public static boolean booleanDialog(String title, String text) {
		BooleanDialog booleanDialog = new BooleanDialog(title, text);
		return booleanDialog.showAndWait();
	}

	static void setAlwaysOnTop(Dialog<?> dialog) {
		EventHandler<DialogEvent> onShowing = e -> {
			Stage stage = ((Stage) dialog.getDialogPane().getScene().getWindow());
			stage.setAlwaysOnTop(true);
			stage.toFront();
			dialog.setOnShowing(null);
		};
		EventHandler<DialogEvent> onShown = e -> {
			Stage stage = ((Stage) dialog.getDialogPane().getScene().getWindow());
			PauseTransition pt = new PauseTransition(Duration.seconds(1));
			pt.setOnFinished(end -> stage.toFront());
			pt.play();
			dialog.setOnShown(null);
		};
		dialog.setOnShown(onShown);
		dialog.setOnShowing(onShowing);
	}

	static void forceClose(Dialog<?> dialog) {
		dialog.getDialogPane().getScene().getWindow().hide();
	}

	static void enableCloseWorkaround(Dialog<?> dialog) {
		Window window = dialog.getDialogPane().getScene().getWindow();
		EventHandler<WindowEvent> onCloseRequest = e -> {
			forceClose(dialog);
			window.setOnCloseRequest(null);
		};
		window.setOnCloseRequest(onCloseRequest);
	}

	static void removeButtonBar(Dialog<?> dialog) {
		DialogPane dp = dialog.getDialogPane();
		ButtonBar bar = ((ButtonBar) dp.lookup(".button-bar"));
		dp.getChildren().remove(bar);
	}

	private static boolean isNameValid(String name) {
		String trimmed = name.trim();
		boolean contains = trimmed.matches(".*[#%&{}\\/<>*?$!'\":;@+`|=].*");
		boolean starts = trimmed.matches("^[ ,-]");
		return !name.isBlank() && !contains && !starts;
	}
}
