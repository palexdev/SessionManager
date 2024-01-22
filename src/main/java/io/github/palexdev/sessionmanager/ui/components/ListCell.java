package io.github.palexdev.sessionmanager.ui.components;

import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import io.github.palexdev.sessionmanager.ui.misc.SelectionModel;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.util.Objects;

public class ListCell<T> extends StackPane implements Cell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ListView<T> list;
	private final ReadOnlyObjectWrapper<T> item = new ReadOnlyObjectWrapper<>() {
		@Override
		protected void invalidated() {
			render();
		}
	};
	private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();
	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper() {
		@Override
		protected void invalidated() {
			PseudoClasses.SELECTED.setOn(ListCell.this, get());
		}
	};
	private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(FunctionalStringConverter.to(Objects::toString)) {
		@Override
		public void set(StringConverter<T> newValue) {
			StringConverter<T> val = newValue == null ? FunctionalStringConverter.to(Objects::toString) : newValue;
			super.set(val);
		}

		@Override
		protected void invalidated() {
			render();
		}
	};

	protected final Label label = new Label();
	private final MaterialSurface surface = new MaterialSurface(this) {
		@Override
		public boolean isOwnerFocused() {
			return super.isOwnerFocused() || isSelected();
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public ListCell(ListView<T> list, T item) {
		this.list = list;
		updateItem(item);
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		getChildren().addAll(label, surface);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().setAll("list-cell");

		SelectionModel<T> sModel = list.getSelectionModel();
		sModel.getSelection().addListener((MapChangeListener<? super Integer, ? super T>) c -> {
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
		SelectionModel<T> sModel = list.getSelectionModel();
		setSelected(sModel.getSelectedIndexes().contains(getIndex()));
	}

	protected void render() {
		T it = getItem();
		label.setText(getConverter().toString(it));
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void updateItem(T item) {
		setItem(item);
		updateSelection();
	}

	@Override
	public void updateIndex(int index) {
		setIndex(index);
		updateSelection();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		surface.resizeRelocate(0, 0, getWidth(), getHeight());
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public T getItem() {
		return item.get();
	}

	public ReadOnlyObjectProperty<T> itemProperty() {
		return item.getReadOnlyProperty();
	}

	protected void setItem(T item) {
		this.item.set(item);
	}

	public int getIndex() {
		return index.get();
	}

	public ReadOnlyIntegerProperty indexProperty() {
		return index.getReadOnlyProperty();
	}

	protected void setIndex(int index) {
		this.index.set(index);
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

	public StringConverter<T> getConverter() {
		return converter.get();
	}

	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	public void setConverter(StringConverter<T> converter) {
		this.converter.set(converter);
	}
}
