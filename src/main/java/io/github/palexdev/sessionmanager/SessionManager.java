package io.github.palexdev.sessionmanager;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ModalityUiUtil;
import com.intellij.util.ui.UIUtil;
import com.sun.javafx.application.PlatformImpl;
import io.github.palexdev.sessionmanager.model.Session;
import io.github.palexdev.sessionmanager.model.SessionInfo;
import io.github.palexdev.sessionmanager.ui.DialogUtils;
import io.github.palexdev.sessionmanager.utils.PathUtils;
import io.github.palexdev.sessionmanager.utils.StorageUtils;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public final class SessionManager implements Disposable {
	//================================================================================
	// Properties
	//================================================================================
	private final Logger logger = Logger.getLogger(SessionManager.class.getName());

	private final Project project;
	private final Map<String, Session> sessions;

	//================================================================================
	// Constructors
	//================================================================================
	public SessionManager(Project project) {
		this.project = project;
		Platform.setImplicitExit(false);
		PlatformImpl.startup(() -> {
		});

		List<Session> prevSaved = StorageUtils.locateAndLoadSessions(project);
		sessions = prevSaved.stream()
				.collect(Collectors.toMap(
						Session::getName,
						s -> s
				));
	}

	//================================================================================
	// Methods
	//================================================================================
	public void saveSession() {
		Platform.runLater(() -> {
			SessionInfo sessionInfo = DialogUtils.saveDialog(project);
			if (sessionInfo == null) {
				logger.log(Level.WARNING, "Session was not saved as return value of save dialog was null");
				return;
			}
			Session session = sessionInfo.toSession();

			FileDocumentManager fdm = FileDocumentManager.getInstance();
			Editor[] editors = EditorFactory.getInstance().getAllEditors();
			List<String> openFiles = Arrays.stream(editors)
					.map(e -> fdm.getFile(e.getDocument()))
					.filter(Objects::nonNull)
					.map(VirtualFile::getCanonicalPath)
					.filter(Objects::nonNull)
					.map(s -> PathUtils.normalizeFileName(project, s))
					.filter(s -> !s.isBlank())
					.collect(Collectors.toList());

			if (sessions.containsKey(session.getName()) && !DialogUtils.booleanDialog(
					"Saving session...", "A session with the given name already exists, overwrite?")) {
				logger.log(Level.INFO, "Session was not overwritten.");
				return;
			}

			session.setFocusedFile(getFocusedFile());
			session.addFiles(openFiles);
			sessions.put(session.getName(), session);
			StorageUtils.saveSessions(sessions.values(), sessionInfo.getPath());
		});
	}

	public void loadSession() {
		Platform.runLater(() -> {
			if (sessions.isEmpty()) {
				DialogUtils.showGeneric("Load Session...", "No saved sessions");
				return;
			}

			Session session = DialogUtils.chooseSessionToLoad(sessions.values());
			if (session == null) {
				logger.log(Level.WARNING, "Session was not loaded as return value of load dialog was null");
				return;
			}

			ModalityUiUtil.invokeLaterIfNeeded(ModalityState.NON_MODAL, () -> {
				closeSession();
				openSession(session);
			});
		});
	}

	public void loadFile() {
		Platform.runLater(() -> {
			Path file = DialogUtils.loadSessionsFile();
			if (file == null) {
				logger.log(Level.WARNING, "No file selected");
				return;
			}

			try {
				StorageUtils.loadSessions(file).forEach(s -> sessions.put(s.getName(), s));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Could not load sessions from file: " + file);
				e.printStackTrace();
			}
		});
	}

	private void closeSession() {
		FileEditorManager fem = FileEditorManager.getInstance(project);
		FileDocumentManager fdm = FileDocumentManager.getInstance();
		Editor[] editors = EditorFactory.getInstance().getAllEditors();
		for (Editor editor : editors) {
			VirtualFile vf = fdm.getFile(editor.getDocument());
			if (vf == null) continue;
			fem.closeFile(vf);
		}
	}

	private void openSession(Session session) {
		Set<String> files = session.getFiles();
		VirtualFile vffFile = null;
		for (String file : files) {
			VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(PathUtils.getProjectPath(project) + "/" + file);
			if (vf == null) continue;
			if (session.isFocused(file)) vffFile = vf;
			openFile(vf, true);
		}
		if (vffFile != null) openFile(vffFile, true);
	}

	private String getFocusedFile() {
		FileDocumentManager fdm = FileDocumentManager.getInstance();
		return UIUtil.invokeAndWaitIfNeeded(() -> Optional.ofNullable(FileEditorManager.getInstance(project).getSelectedTextEditor())
				.map(e -> fdm.getFile(e.getDocument()))
				.map(VirtualFile::getCanonicalPath)
				.map(s -> PathUtils.normalizeFileName(project, s))
				.filter(s -> !s.isBlank())
				.orElse(""));
	}

	private void openFile(VirtualFile file, boolean focus) {
		new OpenFileDescriptor(project, file).navigate(focus);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void dispose() {
		GlobalState.instance().unregister(project);
		sessions.clear();
	}
}
