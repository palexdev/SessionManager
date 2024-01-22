package io.github.palexdev.sessionmanager.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCloseListener;
import io.github.palexdev.sessionmanager.GlobalState;
import org.jetbrains.annotations.NotNull;

public class ProjectListener implements ProjectCloseListener {
	@Override
	public void projectClosingBeforeSave(@NotNull Project project) {
		GlobalState.instance().getManager(project).saveOnClose();
	}
}
