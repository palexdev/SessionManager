package io.github.palexdev.sessionmanager;

import com.intellij.openapi.project.Project;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;

public class GlobalState {
	//================================================================================
	// Static Members
	//================================================================================
	private static final GlobalState instance = new GlobalState();

	public static GlobalState instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final Map<Project, SessionManager> managers = new HashMap<>();

	//================================================================================
	// Constructors
	//================================================================================
	private GlobalState() {
		Platform.startup(SessionManager::updateTheme);
	}

	//================================================================================
	// Methods
	//================================================================================
	public void registerManager(Project project, SessionManager sm) {
		managers.put(project, sm);
	}

	public SessionManager getManager(Project project) {
		return managers.computeIfAbsent(project, p -> p.getService(SessionManager.class));
	}

	public void unregister(Project project) {
		managers.remove(project);
	}
}
