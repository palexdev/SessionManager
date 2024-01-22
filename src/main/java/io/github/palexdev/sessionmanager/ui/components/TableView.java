package io.github.palexdev.sessionmanager.ui.components;

import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import io.github.palexdev.sessionmanager.SessionManager;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.ui.misc.ListChangeProcessor;
import io.github.palexdev.sessionmanager.ui.misc.SelectionModel;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.enums.ColumnsLayoutMode;
import io.github.palexdev.virtualizedfx.table.TableHelper;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableRow;
import io.github.palexdev.virtualizedfx.table.defaults.SimpleTableCell;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class TableView<T> extends VirtualTable<T> implements MFXStyleable {
	//================================================================================
	// Properties
	//================================================================================
	private final SelectionModel<T> sModel = new SelectionModel<>();
	private final ListChangeListener<? super T> itemsChanged = this::itemsChanged;

	//================================================================================
	// Constructors
	//================================================================================
	public TableView() {
		this(FXCollections.observableArrayList());
	}

	public TableView(ObservableList<T> items) {
		setItems(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		setOnMousePressed(e -> requestFocus());
		getStyleClass().setAll(defaultStyleClasses());
		getStylesheets().add(SessionManager.getCss("TableView.css"));
		sModel.setAllowMultipleSelection(false);
		sModel.itemsProperty().bind(itemsProperty());

		itemsProperty().addListener((ob, o, n) -> {
			if (o != null) o.removeListener(itemsChanged);
			if (n != null) n.addListener(itemsChanged);
		});
		getItems().addListener(itemsChanged);

		/*
		 * FIXME there is a bug that causes the estimated width to be 0, thus the rows do not show
		 * This is an easy workaround but it should be properly fixed in VirtualizedFX.
		 */
		setTableHelperSupplier(() -> (getColumnsLayoutMode() == ColumnsLayoutMode.FIXED) ?
				new TableHelper.FixedTableHelper(this) {
					@Override
					public Size computeEstimatedSize() {
						double cellHeight = table.getCellHeight();
						double length = table.getItems().size() * cellHeight;
						double breadth = table.getColumns().size() * getColumnSize().getWidth();
						Size size = Size.of(breadth, length);
						estimatedSize.set(size);
						return size;
					}
				} :
				new TableHelper.VariableTableHelper(this));
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
		return List.of("table-view");
	}

	@Override
	public VirtualScrollPane wrap() {
		VirtualScrollPane vsp = super.wrap();
		vsp.getStylesheets().setAll(SessionManager.getCss("TableView.css"));
		return vsp;
	}

	//================================================================================
	// Getters
	//================================================================================
	public SelectionModel<T> getSelectionModel() {
		return sModel;
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	public static class SessionTable extends TableView<Session> {
		{
			setRowFactory((i, r) -> new SessionRow(this, i, r));

			DefaultTableColumn<Session, SessionCell<String>> nameColumn = new DefaultTableColumn<>(this, "Name");
			nameColumn.setCellFactory(s -> new SessionCell<>(s, Session::getName));
			DefaultTableColumn<Session, SessionCell<Long>> dateColumn = new DefaultTableColumn<>(this, "Date");
			dateColumn.setCellFactory(s -> new SessionCell<>(s, Session::getTimestamp, FunctionalStringConverter.to(l -> {
				if (l == null) l = Instant.EPOCH.toEpochMilli();
				LocalDateTime dt = Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDateTime();
				return SessionManager.DTF.format(dt);
			})));
			DefaultTableColumn<Session, SessionCell<Integer>> filesColumn = new DefaultTableColumn<>(this, "Num files");
			filesColumn.setCellFactory(s -> new SessionCell<>(s, s1 -> s1.getFiles().size()));
			getColumns().addAll(nameColumn, dateColumn, filesColumn);
			setColumnsLayoutMode(ColumnsLayoutMode.VARIABLE);
			autosizeColumns();
		}

		public SessionTable() {
		}

		public SessionTable(ObservableList<Session> items) {
			super(items);
		}
	}

	public static class SessionRow extends DefaultTableRow<Session> {
		private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper() {
			@Override
			protected void invalidated() {
				PseudoClasses.SELECTED.setOn(SessionRow.this, get());
			}
		};
		private final MaterialSurface surface = new MaterialSurface(this) {
			@Override
			public boolean isOwnerFocused() {
				return super.isOwnerFocused() || isSelected();
			}
		};

		public SessionRow(VirtualTable<Session> table, int index, IntegerRange columns) {
			super(table, index, columns);

			SelectionModel<Session> sModel = getTable().getSelectionModel();
			sModel.getSelection().addListener((MapChangeListener<? super Integer, ? super Session>) c -> {
				Integer key = c.getKey();
				if (!Objects.equals(key, getIndex())) return;
				setSelected(c.wasAdded());
			});
			updateSelection();
			setOnMouseClicked(e -> {
				int idx = getIndex();
				if (isSelected()) {
					sModel.deselectIndex(idx);
					return;
				}
				sModel.updateSelection(idx);
			});
		}

		protected void updateSelection() {
			SelectionModel<Session> sModel = getTable().getSelectionModel();
			setSelected(sModel.getSelectedIndexes().contains(getIndex()));
		}

		@Override
		protected void cellsChanged() {
			List<Node> nodes = getCellsAsNodes();
			nodes.add(surface);
			getChildren().setAll(nodes);
		}

		@Override
		protected void layoutChildren() {
			super.layoutChildren();
			surface.resizeRelocate(0, 0, getWidth(), getHeight());
		}

		@Override
		public SessionTable getTable() {
			return (SessionTable) super.getTable();
		}

		public boolean isSelected() {
			return selected.get();
		}

		public ReadOnlyBooleanProperty selectedProperty() {
			return selected.getReadOnlyProperty();
		}

		protected void setSelected(boolean selected) {
			this.selected.set(selected);
		}
	}

	public static class SessionCell<T> extends SimpleTableCell<Session, T> {
		public SessionCell(Session item, Function<Session, T> extractor) {
			this(item, extractor, FunctionalStringConverter.to(Objects::toString));
		}

		public SessionCell(Session item, Function<Session, T> extractor, StringConverter<T> converter) {
			super(item, extractor, converter);
			initialize();
		}

		private void initialize() {
			getStyleClass().setAll("table-cell");
		}
	}
}
