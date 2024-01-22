package io.github.palexdev.sessionmanager.ui;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import io.github.palexdev.mfxcore.utils.fx.TextMeasurementCache;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import io.github.palexdev.sessionmanager.SessionManager;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.ui.components.ListCell;
import io.github.palexdev.sessionmanager.ui.components.ListView;
import io.github.palexdev.sessionmanager.ui.components.TableView.SessionTable;
import io.github.palexdev.sessionmanager.ui.components.TextArea;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.Objects;

public class LoadDialog extends BaseDialog<Session> {
	//================================================================================
	// Properties
	//================================================================================
	private final SessionManager sManager;
	private final ObservableList<Session> sessions;

	private SessionTable sTable;
	private GridPane dView;
	private ListView<String> tList;
	private TextArea dArea;
	private MFXIconButton backBtn;
	private Animation dAnimation;
	private BooleanProperty showingDetails;

	private final ObjectProperty<StringConverter<String>> pathConverter = new SimpleObjectProperty<>(
			FunctionalStringConverter.to(s -> {
				int start = s.lastIndexOf("/") + 1;
				return s.substring(start);
			})
	);
	private boolean isLess = true;

	//================================================================================
	// Constructors
	//================================================================================
	public LoadDialog(SessionManager sManager, Collection<Session> sessions) {
		super("Saved Sessions");
		this.sManager = sManager;
		this.sessions = FXCollections.observableArrayList(sessions);
		content.getStyleClass().add("load");
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void build() {
		super.build();

		showingDetails = new SimpleBooleanProperty(false) {
			@Override
			protected void invalidated() {
				boolean v = get();
				MFXFontIcon icon = new MFXFontIcon(v ? FontAwesomeSolid.CHEVRON_DOWN : FontAwesomeSolid.CHEVRON_UP);
				backBtn.setIcon(icon);
			}
		};

		// Main view
		sTable = new SessionTable();
		sTable.setCellHeight(40);
		VirtualScrollPane vsp = sTable.wrap();
		GridPane.setColumnSpan(vsp, GridPane.REMAINING);
		GridPane.setVgrow(vsp, Priority.ALWAYS);
		content.addRow(1, vsp);

		MFXButton resBtn = new MFXButton("Restore").text();
		resBtn.disableProperty().bind(sTable.getSelectionModel().emptyProperty());
		resBtn.setOnAction(e -> restore());

		MFXButton delBtn = new MFXButton("Delete").text();
		delBtn.disableProperty().bind(sTable.getSelectionModel().emptyProperty());
		delBtn.setOnAction(e -> delete());

		MFXButton closeBtn = new MFXButton("Close").text();
		closeBtn.setOnAction(e -> hide());

		HBox actionsBox = new HBox(resBtn, delBtn, closeBtn);
		actionsBox.spacingProperty().bind(content.hgapProperty());
		actionsBox.setAlignment(Pos.CENTER_RIGHT);
		actionsBox.setPadding(ACTIONS_MARGIN);
		GridPane.setColumnSpan(actionsBox, GridPane.REMAINING);
		content.addRow(2, actionsBox);

		// Details view
		tList = new ListView<>();
		tList.setCellFactory(s -> new ListCell<>(tList, s) {
			final TextMeasurementCache tmc = new TextMeasurementCache(itemProperty(), label.fontProperty());

			{
				label.setForceDisableTextEllipsis(true);
				label.setMaxWidth(Double.MAX_VALUE);
				converterProperty().bind(LoadDialog.this.pathConverter);
			}

			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				label.resize(tmc.getSnappedWidth(), label.getHeight());
			}
		});
		tList.setFitToBreadth(true);
		VirtualScrollPane tVsp = tList.wrap();
		dArea = new TextArea();
		dArea.setEditable(false);
		MFXCheckbox check = new MFXCheckbox("Show full path");
		check.setOnAction(e -> {
			if (isLess) {
				pathConverter.set(FunctionalStringConverter.to(Objects::toString));
				isLess = false;
			} else {
				pathConverter.set(FunctionalStringConverter.to(s -> {
					int start = s.lastIndexOf("/") + 1;
					return s.substring(start);
				}));
				isLess = true;
			}
		});
		GridPane.setColumnSpan(check, GridPane.REMAINING);
		GridPane.setHalignment(check, HPos.CENTER);
		dView = new GridPane();
		for (int i = 0; i < 2; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(50);
			cc.setHgrow(Priority.ALWAYS);
			dView.getColumnConstraints().add(cc);

			RowConstraints rc = new RowConstraints();
			if (i == 0) rc.setVgrow(Priority.ALWAYS);
			dView.getRowConstraints().add(rc);
		}
		dView.addRow(0, tVsp, dArea);
		dView.addRow(1, check);
		dView.setOpacity(0.0);
		dView.setVisible(false);
		dView.setManaged(false);
		dView.getStyleClass().add("details");
		content.getChildren().add(dView);
		dView.setTranslateY(dView.getHeight() + 40);

		backBtn = new MFXIconButton(new MFXFontIcon(FontAwesomeSolid.CHEVRON_UP));
		backBtn.setOnAction(e -> details());
		backBtn.setViewOrder(-2);
		backBtn.getStyleClass().add("back");
		backBtn.disableProperty().bind(sTable.getSelectionModel().emptyProperty());
		MFXTooltip detTooltip = new MFXTooltip(backBtn);
		detTooltip.setInDelay(M3Motion.SHORT4);
		detTooltip.setOutDelay(M3Motion.MEDIUM2);
		MFXPlainContent detContent = new MFXPlainContent();
		detContent.textProperty().bind(showingDetails.map(v -> v ? "Hide details" : "Show details"));
		detTooltip.setContent(detContent);
		detTooltip.install();
		GridPane.setHalignment(backBtn, HPos.RIGHT);
		content.add(backBtn, 2, 0);
	}

	@Override
	protected void extraLayout() {
		Insets insets = content.getInsets();
		double y = draggingArea.getHeight() + content.getVgap();
		double w = content.getWidth() - insets.getRight() - insets.getLeft();
		double h = content.getHeight() - y - (insets.getBottom() / 2.0);
		dView.resizeRelocate(insets.getLeft(), y, w, h);
	}

	@Override
	public Session showAndWait() {
		sTable.setItems(sessions);
		return super.showAndWait();
	}

	@Override
	public void hide() {
		/*
		 * FIXME well, there is still no fix for popups causing exceptions because the window is already closed.
		 *  This is a mere workaround, but it seems to work, so.....
		 */
		backBtn.getMFXTooltip().setAnimated(false);
		backBtn.getMFXTooltip().hide();
		super.hide();
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void restore() {
		Session session = sTable.getSelectionModel().getSelectedItem();
		ref.set(session);
		hide();
	}

	protected void delete() {
		Session session = sTable.getSelectionModel().getSelectedItem();
		sTable.getItems().remove(session);
		sManager.delete(session);
	}

	protected void details() {
		if (Animations.isPlaying(dAnimation)) return;
		Duration inDuration = M3Motion.MEDIUM2;
		Duration outDuration = M3Motion.MEDIUM4;
		Interpolator curve = M3Motion.EMPHASIZED;
		if (showingDetails.get()) {
			dAnimation = TimelineBuilder.build()
					.add(KeyFrames.of(outDuration, dView.translateYProperty(), dView.getHeight() + 40, curve))
					.add(KeyFrames.of(outDuration, dView.opacityProperty(), 0.0, curve))
					.setOnFinished(e -> dView.setVisible(false))
					.getAnimation();
			dAnimation.play();
			showingDetails.set(false);
			tList.setFitToBreadth(true);
			return;
		}

		Session session = sTable.getSelectionModel().getSelectedItem();
		tList.setItems(FXCollections.observableArrayList(session.getFiles()));
		dArea.setText(session.getDescription());
		dAnimation = TimelineBuilder.build()
				.add(KeyFrames.of(0, e -> dView.setVisible(true)))
				.add(KeyFrames.of(inDuration, dView.translateYProperty(), 0.0, curve))
				.add(KeyFrames.of(inDuration, dView.opacityProperty(), 1.0, curve))
				.getAnimation();
		showingDetails.set(true);
		tList.setFitToBreadth(false);
		dAnimation.play();
	}

}
