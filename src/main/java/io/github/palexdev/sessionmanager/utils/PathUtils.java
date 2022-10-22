package io.github.palexdev.sessionmanager.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PathUtils {
	//================================================================================
	// Static Members
	//================================================================================
	private static String projectPath = "";

	//================================================================================
	// Constructors
	//================================================================================
	private PathUtils() {
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static String getProjectPath(Project project) {
		if (!projectPath.equals("")) return projectPath;
		projectPath = Optional.ofNullable(project)
				.flatMap(p -> Optional.ofNullable(ProjectUtil.guessProjectDir(p)))
				.map(VirtualFile::getCanonicalPath)
				.orElse("");
		return projectPath;
	}

	public static String getDefaultPath(Project project) {
		String projectPath = getProjectPath(project);
		if (projectPath.isEmpty()) return "";
		return projectPath + "/.idea";
	}

	public static String getFallbackPath(Project project) {
		String path = "";
		if (project != null) {
			path = getProjectPath(project);
		}

		if ("".equals(path)) {
			path = System.getProperty("user.home");
		}

		return path;
	}

	public static Path getStoragePath(Project project) {
		// Default
		String defaultPath = PathUtils.getDefaultPath(project);
		Path toPath = Path.of(defaultPath);
		if (PathUtils.isValidDir(toPath)) return toPath;

		// Fallback
		return Path.of(PathUtils.getFallbackPath(project));
	}

	public static boolean isValidDir(Path path) {
		try {
			return Files.exists(path) && Files.isDirectory(path);
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isValidFile(Path path) {
		try {
			return Files.exists(path) && !Files.isDirectory(path);
		} catch (Exception ex) {
			return false;
		}
	}

	public static String normalizeFileName(Project project, String fName) {
		String basePath = getProjectPath(project);
		return basePath.isEmpty() ? "" : fName.replace(basePath, "");
	}
}
