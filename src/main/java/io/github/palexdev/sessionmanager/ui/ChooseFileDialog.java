package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.sessionmanager.utils.StorageUtils;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class ChooseFileDialog {

	//================================================================================
	// Methods
	//================================================================================
	public Path chooseFile() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("SessionManager sessions file", StorageUtils.stgFileName));
		return Optional.ofNullable(fc.showOpenDialog(null)).map(File::toPath).orElse(null);
	}
}
