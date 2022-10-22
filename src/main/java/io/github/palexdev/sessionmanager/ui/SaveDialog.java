package io.github.palexdev.sessionmanager.ui;

import com.intellij.openapi.project.Project;
import io.github.palexdev.sessionmanager.model.SessionInfo;
import io.github.palexdev.sessionmanager.utils.PathUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;

public class SaveDialog extends BaseDialog<SessionInfo> {
	//================================================================================
	// Properties
	//================================================================================
	private final Project project;
	private TextField nameField;
	private TextField pathField;
	private TextArea descArea;

	//================================================================================
	// Constructors
	//================================================================================

	public SaveDialog(Project project) {
		this.project = project;
		init();
	}


	//================================================================================
	// Methods
	//================================================================================
	protected void init() {
		dialog.setTitle("Save Session");
		Path path = PathUtils.getStoragePath(project);

		nameField = new TextField();
		nameField.setPromptText("<Required>");

		pathField = new TextField();
		pathField.setPromptText("<Default Path>");

		descArea = new TextArea();
		descArea.setPromptText("<Optional>");

		Button dirButton = new Button("Choose");
		dirButton.setOnAction(e -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(path.toFile());
			chooser.setTitle("Save session to directory...");
			File file = chooser.showDialog(null);
			pathField.setText(file.toString());
		});

		Button okButton = new Button("OK");
		okButton.setOnAction(e -> {
			String name = nameField.getText();
			if (!isNameValid(name)) {
				DialogUtils.showGeneric("Error saving session", "Invalid file name: " + name);
				return;
			}

			String pathTxt = pathField.getText();
			if (pathTxt.trim().isBlank()) pathTxt = path.toString();
			if (!PathUtils.isValidDir(Path.of(pathTxt))) {
				DialogUtils.showGeneric("Error saving session", "Invalid save path: " + pathTxt);
				return;
			}

			ref.set(SessionInfo.of(name, pathTxt, descArea.getText()));
			DialogUtils.forceClose(dialog);
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			ref.set(null);
			DialogUtils.forceClose(dialog);
		});

		HBox pathBox = new HBox(10, pathField, dirButton);
		HBox.setHgrow(pathField, Priority.ALWAYS);

		HBox aBox = new HBox(10, okButton, cancelButton);
		aBox.setAlignment(Pos.BOTTOM_RIGHT);

		VBox root = new VBox(15, nameField, pathBox, descArea, aBox);
		root.setPadding(new Insets(10));
		root.setMinSize(400, 200);

		DialogPane dp = new DialogPane();
		dp.setContent(root);
		dialog.setDialogPane(dp);
		DialogUtils.setAlwaysOnTop(dialog);
		DialogUtils.enableCloseWorkaround(dialog);
		DialogUtils.removeButtonBar(dialog);
	}

	private boolean isNameValid(String name) {
		String trimmed = name.trim();
		boolean contains = trimmed.matches(".*[#%&{}\\/<>*?$!'\":;@+`|=].*");
		boolean starts = trimmed.matches("^[ ,-]");
		return !name.isBlank() && !contains && !starts;
	}
}
