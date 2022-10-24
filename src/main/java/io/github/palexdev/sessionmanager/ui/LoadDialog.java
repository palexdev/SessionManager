package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.utils.SessionStringConverter;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Collection;

public class LoadDialog extends BaseDialog<Session> {
	//================================================================================
	// Properties
	//================================================================================
	private final Collection<Session> sessions;
	private ComboBox<Session> sCombo;

	//================================================================================
	// Constructors
	//================================================================================
	public LoadDialog(Collection<Session> sessions) {
		this.sessions = sessions;
		init();
	}

	//================================================================================
	// Methods
	//================================================================================
	@Override
	protected void init() {
		dialog.setTitle("Load Session");
		Label label = new Label("Available Sessions");

		sCombo = new ComboBox<>(FXCollections.observableArrayList(sessions));
		sCombo.setConverter(SessionStringConverter.instance());
		sCombo.setMaxWidth(Double.MAX_VALUE);

		TextArea descArea = new TextArea();
		descArea.setPromptText("<Description>");
		descArea.setEditable(false);
		descArea.setWrapText(true);
		sCombo.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> descArea.setText(n.getDescription()));

		Button okButton = new Button("OK");
		okButton.setOnAction(e -> {
			Session selSession = sCombo.getSelectionModel().getSelectedItem();
			if (selSession == null) {
				DialogUtils.showGeneric("Load Session...", "No session selected");
				return;
			}

			ref.set(selSession);
			DialogUtils.forceClose(dialog);
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			ref.set(null);
			DialogUtils.forceClose(dialog);
		});

		HBox aBox = new HBox(10, okButton, cancelButton);
		aBox.setAlignment(Pos.BOTTOM_RIGHT);
		VBox.setVgrow(aBox, Priority.ALWAYS);

		VBox root = new VBox(15, label, sCombo, descArea, aBox);
		root.setPadding(new Insets(10));
		root.setMinSize(400, 150);

		DialogPane dp = new DialogPane();
		dp.setContent(root);
		dialog.setDialogPane(dp);
		DialogUtils.setAlwaysOnTop(dialog);
		DialogUtils.enableCloseWorkaround(dialog);
		DialogUtils.removeButtonBar(dialog);
	}
}
