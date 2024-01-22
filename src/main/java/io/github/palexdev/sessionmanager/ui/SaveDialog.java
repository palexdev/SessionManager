package io.github.palexdev.sessionmanager.ui;

import com.intellij.openapi.project.Project;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import io.github.palexdev.sessionmanager.model.SessionInfo;
import io.github.palexdev.sessionmanager.ui.components.FloatingField;
import io.github.palexdev.sessionmanager.ui.components.TextArea;
import io.github.palexdev.sessionmanager.utils.PathUtils;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;

public class SaveDialog extends BaseDialog<SessionInfo> {
	//================================================================================
	// Properties
	//================================================================================
	private final Project project;
	private FloatingField nameField;
	private FloatingField pathField;
	private TextArea descArea;

	//================================================================================
	// Constructors
	//================================================================================

	public SaveDialog(Project project) {
		super("Save Session");
		this.project = project;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void build() {
		super.build();

		ColumnConstraints cc0 = cConstraint(Priority.SOMETIMES, HPos.LEFT);
		ColumnConstraints cc1 = cConstraint(Priority.NEVER, HPos.CENTER);
		ColumnConstraints cc2 = cConstraint(Priority.NEVER, HPos.LEFT);
		content.getColumnConstraints().setAll(cc0, cc1, cc2);

		Path path = PathUtils.getStoragePath(project);
		nameField = new FloatingField("", "Name");
		nameField.setMaxWidth(Double.MAX_VALUE);
		When.onInvalidated(nameField.focusWithinProperty())
				.condition(v -> !v)
				.then(v -> {
					boolean valid = isNameValid(nameField.field().getText());
					PseudoClasses.ERROR.setOn(nameField, !valid);
				})
				.listen();
		When.onInvalidated(nameField.field().textProperty())
				.then(t -> PseudoClasses.ERROR.setOn(nameField, !isNameValid(t)))
				.listen();
		content.add(nameField, 0, 1);

		pathField = new FloatingField(path.toString(), "Path");
		pathField.setEditable(false);
		pathField.setMaxWidth(Double.MAX_VALUE);
		content.add(pathField, 0, 2);
		MFXIconButton dirBtn = new MFXIconButton(new MFXFontIcon(FontAwesomeSolid.FOLDER_OPEN));
		dirBtn.setOnAction(e -> selectPath());
		content.add(dirBtn, 1, 2);

		descArea = new TextArea("", "Description");
		content.add(descArea, 0, 3);

		MFXButton saveBtn = new MFXButton("Save").text();
		saveBtn.setOnAction(e -> save());
		content.add(saveBtn, 0, 4);
		GridPane.setHalignment(saveBtn, HPos.RIGHT);
		GridPane.setMargin(saveBtn, ACTIONS_MARGIN);
		MFXButton cancelBtn = new MFXButton("Cancel").text();
		cancelBtn.setOnAction(e -> hide());
		content.add(cancelBtn, 1, 4);
		GridPane.setColumnSpan(cancelBtn, GridPane.REMAINING);
		GridPane.setMargin(cancelBtn, ACTIONS_MARGIN);

		nameField.getPseudoClassStates().addListener((SetChangeListener<? super PseudoClass>) l ->
				saveBtn.setDisable(PseudoClasses.ERROR.isActiveOn(nameField) || nameField.field().getText().isBlank())
		);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void selectPath() {
		Path defPath = PathUtils.getStoragePath(project);
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Save session to directory...");
		dc.setInitialDirectory(defPath.toFile());

		File f = dc.showDialog(window);
		if (f == null) return;
		pathField.field().setText(f.getAbsolutePath());
	}

	private void save() {
		String pathTxt = pathField.field().getText();
		if (!PathUtils.isValidDir(Path.of(pathTxt))) {
			DialogUtils.showGeneric("Error saving session", "Invalid save path: " + pathTxt);
			return;
		}

		ref.set(SessionInfo.of(nameField.field().getText(), pathTxt, descArea.getText()));
		hide();
	}

	private boolean isNameValid(String name) {
		String trimmed = name.trim();
		boolean contains = trimmed.matches(".*[#%&{}\\/<>*?$!'\":;@+`|=].*");
		boolean starts = trimmed.matches("^[ ,-]");
		return !name.isBlank() && !contains && !starts;
	}

	private ColumnConstraints cConstraint(Priority priority, HPos hPos) {
		return new ColumnConstraints(-1, -1, -1, priority, hPos, true);
	}
}
