package io.github.palexdev.sessionmanager.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class SessionInfo {
	//================================================================================
	// Properties
	//================================================================================
	@SerializedName("name")
	private final String name;

	@SerializedName("path")
	private final String path;

	@SerializedName("desc")
	private final String description;

	//================================================================================
	// Constructors
	//================================================================================
	public SessionInfo(@NotNull String name, @NotNull String path, String description) {
		this.name = name;
		this.path = path;
		this.description = description;
	}

	public static SessionInfo of(@NotNull String name, @NotNull String path, String description) {
		return new SessionInfo(name, path, description);
	}

	//================================================================================
	// Methods
	//================================================================================
	public Session toSession() {
		return Session.of(name, description);
	}

	//================================================================================
	// Getters
	//================================================================================
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getDescription() {
		return description;
	}
}
