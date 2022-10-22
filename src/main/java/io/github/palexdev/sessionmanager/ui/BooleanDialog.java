package io.github.palexdev.sessionmanager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BooleanDialog extends BaseDialog<Boolean> {
	//================================================================================
	// Properties
	//================================================================================
	private final String title;
	private final String text;

	//================================================================================
	// Constructors
	//================================================================================
	public BooleanDialog(String title, String text) {
		this.title = title;
		this.text = text;
		init();
	}

	//================================================================================
	// Methods
	//================================================================================
	@Override
	protected void init() {
		dialog.setTitle(title);

		Label label = new Label(text);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setWrapText(true);

		Button okButton = new Button("OK");
		okButton.setOnAction(e -> {
			ref.set(true);
			DialogUtils.forceClose(dialog);
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			ref.set(false);
			DialogUtils.forceClose(dialog);
		});

		HBox aBox = new HBox(10, okButton, cancelButton);
		aBox.setAlignment(Pos.BOTTOM_RIGHT);
		VBox.setVgrow(aBox, Priority.ALWAYS);

		VBox root = new VBox(10, label, aBox);
		root.setPadding(new Insets(10));
		root.setMinSize(400, 150);

		DialogPane dp = new DialogPane();
		dp.setContent(root);
		dialog.setDialogPane(dp);

		DialogUtils.removeButtonBar(dialog);
		DialogUtils.setAlwaysOnTop(dialog);
		DialogUtils.enableCloseWorkaround(dialog);
	}
}
