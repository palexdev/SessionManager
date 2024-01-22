package io.github.palexdev.sessionmanager.ui.components;

import io.github.palexdev.sessionmanager.SessionManager;

public class TextArea extends javafx.scene.control.TextArea {

	//================================================================================
	// Constructors
	//================================================================================
	public TextArea() {
		this("");
	}

	public TextArea(String text) {
		super(text);
		initialize();
	}

	public TextArea(String text, String prompt) {
		super(text);
		setPromptText(prompt);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStylesheets().add(SessionManager.getCss("TextArea.css"));
	}
}
