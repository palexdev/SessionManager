package io.github.palexdev.sessionmanager.utils;

import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.utils.gson.GsonInstance;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageUtils {
	private static final Logger logger = Logger.getLogger(StorageUtils.class.getName());
	public static final String stgFileName = "session-manager.json";

	//================================================================================
	// Constructors
	//================================================================================
	private StorageUtils() {
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static void saveSessions(Collection<Session> sessions, String path) {
		Path toPath = null;
		try {
			toPath = Path.of(path + "/" + stgFileName);
			String toJson = GsonInstance.toJson(sessions);
			Files.writeString(toPath, toJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Failed to write to file: " + toPath);
			ex.printStackTrace();
		}
	}

	public static List<Session> locateAndLoadSessions(Project project) {
		List<Session> defSessions = loadSessions(PathUtils.getDefaultPath(project));
		if (!defSessions.isEmpty()) return defSessions;
		return loadSessions(PathUtils.getFallbackPath(project));
	}


	public static List<Session> loadSessions(String path) {
		try {
			Path sessionFile = Path.of(path).resolve(stgFileName);
			return loadSessions(sessionFile);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Could not find or load sessions from path: " + path);
		}
		return List.of();
	}

	public static List<Session> loadSessions(Path path) throws IOException {
		if (PathUtils.isValidFile(path)) {
			String content = Files.readString(path);
			Type type = new TypeToken<List<Session>>() {
			}.getType();
			return GsonInstance.fromJson(content, type);
		} else {
			throw new IOException();
		}
	}
}
