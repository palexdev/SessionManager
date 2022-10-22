package io.github.palexdev.sessionmanager.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import io.github.palexdev.sessionmanager.GlobalState;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadFileAction extends AnAction {
	private final Logger logger = Logger.getLogger(LoadFileAction.class.getName());

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getProject();
		if (project == null) {
			logger.log(Level.SEVERE, "Could not load session as project is null");
			return;
		}
		GlobalState.instance().getManager(project).loadFile();
	}
}
