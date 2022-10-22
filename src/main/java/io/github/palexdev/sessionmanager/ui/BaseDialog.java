package io.github.palexdev.sessionmanager.ui;

import javafx.scene.control.Dialog;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseDialog<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final AtomicReference<T> ref = new AtomicReference<>();
	protected final Dialog<Void> dialog = new Dialog<>();

	//================================================================================
	// Constructors
	//================================================================================
	protected BaseDialog() {
	}

	//================================================================================
	// Abstract Methods
	//================================================================================
	protected abstract void init();

	//================================================================================
	// Methods
	//================================================================================
	public T showAndWait() {
		dialog.showAndWait();
		return ref.get();
	}

	public void hide() {
		DialogUtils.forceClose(dialog);
	}
}
