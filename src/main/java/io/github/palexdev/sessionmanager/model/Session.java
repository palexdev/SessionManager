package io.github.palexdev.sessionmanager.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Session {
	//================================================================================
	// Properties
	//================================================================================
	@SerializedName("name")
	private String name;

	@SerializedName("timestamp")
	private Long timestamp;

	@SerializedName("desc")
	private String description;

	@SerializedName("path")
	private String path;

	@SerializedName("fFile")
	private String focusedFile;

	@SerializedName("files")
	private final Set<String> files = new LinkedHashSet<>();

	//================================================================================
	// Constructors
	//================================================================================
	public Session(@NotNull String name, Long timestamp, String description, String path) {
		this.name = name;
		this.timestamp = timestamp;
		this.description = description;
		this.path = path;
	}

	public static Session of(@NotNull String name, Long timestamp, String description, String path) {
		return new Session(name, timestamp, description, path);
	}

	//================================================================================
	// Methods
	//================================================================================
	public boolean addFile(String file) {
		return files.add(file);
	}

	public boolean addFiles(String... files) {
		return this.files.addAll(List.of(files));
	}

	public boolean addFiles(Collection<String> files) {
		return this.files.addAll(files);
	}

	public boolean isFocused(String file) {
		return Objects.equals(file, focusedFile);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Session session = (Session) o;
		return getName().equals(session.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String getName() {
		return name;
	}

	public void setName(@NotNull String name) {
		this.name = name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFocusedFile() {
		return focusedFile;
	}

	public void setFocusedFile(String focusedFile) {
		this.focusedFile = focusedFile;
	}

	public Set<String> getFiles() {
		return files;
	}
}
