package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcore.controls.Label;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;

public class BooleanDialog extends BaseDialog<Boolean> {
	//================================================================================
	// Properties
	//================================================================================
	private final String action;
	private final String text;

	private Label description;
	private MFXButton okBtn;

	//================================================================================
	// Constructors
	//================================================================================
	public BooleanDialog(String title, String action, String text) {
		super(title);
		this.action = action;
		this.text = text;
		content.getStyleClass().add("boolean");
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void build() {
		super.build();

		description = new Label(text);
		description.setMaxWidth(Double.MAX_VALUE);
		description.setWrapText(true);
		description.getStyleClass().add("content");
		content.addRow(1, description);

		okBtn = new MFXButton().text();
		okBtn.setOnAction(e -> {
			ref.set(true);
			hide();
		});
		content.add(okBtn, 1, 2);
		GridPane.setHalignment(okBtn, HPos.RIGHT);
		GridPane.setMargin(okBtn, ACTIONS_MARGIN);
		MFXButton cancelBtn = new MFXButton("Cancel").text();
		cancelBtn.setOnAction(e -> {
			ref.set(false);
			hide();
		});
		content.add(cancelBtn, 2, 2);
		GridPane.setHalignment(cancelBtn, HPos.RIGHT);
		GridPane.setMargin(cancelBtn, ACTIONS_MARGIN);
	}

	@Override
	public Boolean showAndWait() {
		description.setText(text);
		okBtn.setText(action);
		return super.showAndWait();
	}
}
