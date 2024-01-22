package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.utils.fx.StageUtils;
import io.github.palexdev.sessionmanager.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseDialog<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final Stage window = new Stage(StageStyle.TRANSPARENT);
	protected final Scene scene;
	protected final GridPane content;
	protected final Region draggingArea;

	private Label titleLabel;
	private final String title;
	protected final Insets ACTIONS_MARGIN = InsetsBuilder.top(24);

	protected final AtomicReference<T> ref = new AtomicReference<>();

	//================================================================================
	// Constructors
	//================================================================================
	protected BaseDialog(String title) {
		this.title = title;

		content = new GridPane() {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				double daW = getWidth();
				double daH = snappedTopInset() + titleLabel.getHeight();
				draggingArea.resizeRelocate(0, 0, daW, daH);
				extraLayout();
			}
		};
		content.getStyleClass().add("base-dialog");

		draggingArea = new Region();
		draggingArea.setManaged(false);
		draggingArea.setViewOrder(-1);
		content.getChildren().add(draggingArea);
		StageUtils.makeDraggable(window, draggingArea);

		scene = new Scene(content);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(SessionManager.getCss("Dialogs.css"));

		build();
		window.setScene(scene);
	}

	//================================================================================
	// Methods
	//================================================================================
	public T showAndWait() {
		window.showAndWait();
		return ref.get();
	}

	public void hide() {
		window.hide();
	}

	protected void build() {
		// Header
		titleLabel = new Label(title);
		titleLabel.getStyleClass().add("title");
		titleLabel.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(titleLabel, Priority.ALWAYS);
		content.add(titleLabel, 0, 0);
	}

	protected void extraLayout() {
	}
}
