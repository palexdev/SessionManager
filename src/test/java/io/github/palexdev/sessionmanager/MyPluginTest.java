package io.github.palexdev.sessionmanager;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import io.github.palexdev.sessionmanager.utils.PathUtils;

public class MyPluginTest extends BasePlatformTestCase {

	public void testPaths() {
		Project project = getProject();
		String projectPath = PathUtils.getProjectPath(project);
		String defaultPath = PathUtils.getDefaultPath(project);
		System.out.println("Project Path is:" + projectPath);
		System.out.println("Default Path is: " + defaultPath);
	}
}
