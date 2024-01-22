package io.github.palexdev.sessionmanager.ui;

import com.intellij.openapi.project.Project;
import io.github.palexdev.sessionmanager.SessionManager;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.model.SessionInfo;

import java.nio.file.Path;
import java.util.Collection;

public class DialogUtils {

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

	public static Session viewSessions(SessionManager manager, Collection<Session> sessions) {
		LoadDialog loadDialog = new LoadDialog(manager, sessions);
		return loadDialog.showAndWait();
	}

	public static Path loadSessionsFile() {
		return new ChooseFileDialog().chooseFile();
	}

	public static void showGeneric(String title, String text) {
		GenericDialog genericDialog = new GenericDialog(title, text);
		genericDialog.showAndWait();
	}

	public static boolean booleanDialog(String title, String action, String text) {
		BooleanDialog booleanDialog = new BooleanDialog(title, action, text);
		return booleanDialog.showAndWait();
	}
}
