package io.github.palexdev.sessionmanager.ui.components;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import io.github.palexdev.sessionmanager.SessionManager;
import io.github.palexdev.sessionmanager.ui.misc.ListChangeProcessor;
import io.github.palexdev.sessionmanager.ui.misc.SelectionModel;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.flow.VirtualFlow;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;

public class ListView<T> extends VirtualFlow<T, ListCell<T>> implements MFXStyleable {
	//================================================================================
	// Properties
	//================================================================================
	private final SelectionModel<T> sModel = new SelectionModel<>();
	private final ListChangeListener<? super T> itemsChanged = this::itemsChanged;

	//================================================================================
	// Constructors
	//================================================================================
	public ListView() {
		this(FXCollections.observableArrayList());
	}

	public ListView(ObservableList<T> items) {
		setItems(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		setOnMousePressed(e -> requestFocus());
		getStyleClass().setAll(defaultStyleClasses());
		getStylesheets().add(SessionManager.getCss("ListView.css"));
		sModel.setAllowMultipleSelection(false);
		sModel.itemsProperty().bind(itemsProperty());

		itemsProperty().addListener((ob, o, n) -> {
			if (o != null) o.removeListener(itemsChanged);
			if (n != null) n.addListener(itemsChanged);
		});
		getItems().addListener(itemsChanged);
	}

	protected void itemsChanged(ListChangeListener.Change<? extends T> change) {
		if (sModel.getSelection().isEmpty()) return;
		if (change.getList().isEmpty()) {
			sModel.clearSelection();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.instance().processChange(change).get(0);
		ListChangeProcessor updater = new ListChangeProcessor(sModel.getSelectedIndexes());
		switch (c.getType()) {
			case REPLACE -> sModel.replaceSelection(c.getIndexes().toArray(Integer[]::new));
			case ADD -> {
				Set<Integer> added = c.getIndexes();
				updater.computeAddition(added.size(), c.getFrom());
				sModel.replaceSelection(updater.getIndexes().toArray(Integer[]::new));
			}
			case REMOVE -> {
				Set<Integer> removed = c.getIndexes();
				updater.computeRemoval(removed, c.getFrom());
				sModel.replaceSelection(updater.getIndexes().toArray(Integer[]::new));
			}
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<String> defaultStyleClasses() {
		return List.of("list-view");
	}

	@Override
	public VirtualScrollPane wrap() {
		VirtualScrollPane vsp = super.wrap();
		vsp.getStylesheets().setAll(SessionManager.getCss("ListView.css"));
		return vsp;
	}

	//================================================================================
	// Getters
	//================================================================================
	public SelectionModel<T> getSelectionModel() {
		return sModel;
	}
}
