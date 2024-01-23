package io.github.palexdev.sessionmanager.listeners;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import io.github.palexdev.sessionmanager.SessionManager;
import org.jetbrains.annotations.NotNull;

public class LafListener implements LafManagerListener {
	@Override
	public void lookAndFeelChanged(@NotNull LafManager source) {
		SessionManager.updateTheme();
	}
}
