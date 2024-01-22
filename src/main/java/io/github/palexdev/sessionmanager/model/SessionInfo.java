package io.github.palexdev.sessionmanager.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class SessionInfo {
	//================================================================================
	// Properties
	//================================================================================
	@SerializedName("name")
	private final String name;

	@SerializedName("timestamp")
	private final Long timestamp;

	@SerializedName("path")
	private final String path;

	@SerializedName("desc")
	private final String description;

	//================================================================================
	// Constructors
	//================================================================================
	public SessionInfo(@NotNull String name, @NotNull String path, String description) {
		this.name = name;
		this.timestamp = Instant.now().toEpochMilli();
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
		return Session.of(name, getTimestamp(), description, path);
	}

	//================================================================================
	// Getters
	//================================================================================
	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return (timestamp != null) ? timestamp : Instant.EPOCH.toEpochMilli();
	}

	public String getPath() {
		return path;
	}

	public String getDescription() {
		return description;
	}
}
