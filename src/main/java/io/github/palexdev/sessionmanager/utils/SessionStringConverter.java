package io.github.palexdev.sessionmanager.utils;

import io.github.palexdev.sessionmanager.model.Session;
import javafx.util.StringConverter;

public class SessionStringConverter extends StringConverter<Session> {
	private static final SessionStringConverter instance = new SessionStringConverter();

	public static SessionStringConverter instance() {
		return instance;
	}

	@Override
	public String toString(Session session) {
		return (session != null) ? session.getName() : "";
	}

	@Override
	public Session fromString(String s) {
		throw new UnsupportedOperationException();
	}
}
