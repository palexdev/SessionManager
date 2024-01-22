package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcore.controls.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

public class GenericDialog extends BaseDialog<Void> {
	//================================================================================
	// Properties
	//================================================================================
	private final String text;

	private Label description;

	//================================================================================
	// Constructors
	//================================================================================
	public GenericDialog(String title, String text) {
		super(title);
		this.text = text;

		content.getStyleClass().add("generic");
		window.initModality(Modality.APPLICATION_MODAL);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void build() {
		super.build();

		description = new Label();
		description.setMaxWidth(Double.MAX_VALUE);
		description.setWrapText(true);
		description.getStyleClass().add("content");
		content.addRow(1, description);

		MFXButton okBtn = new MFXButton("OK").text();
		okBtn.setOnAction(e -> hide());
		content.add(okBtn, 1, 2);
		GridPane.setColumnSpan(okBtn, GridPane.REMAINING);
		GridPane.setMargin(okBtn, ACTIONS_MARGIN);
	}

	@Override
	public Void showAndWait() {
		description.setText(text);
		return super.showAndWait();
	}
}
