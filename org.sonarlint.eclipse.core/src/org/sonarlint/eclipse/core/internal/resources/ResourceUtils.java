/*
 * SonarLint for Eclipse
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.eclipse.core.internal.resources;

import java.io.File;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.sonarlint.eclipse.core.internal.SonarLintCorePlugin;

public final class ResourceUtils {

  private static final String PATH_SEPARATOR = "/";

  private ResourceUtils() {
  }

  @CheckForNull
  public static String getSonarResourcePartialKey(IResource resource, String serverVersion) {
    String path = resource.getProjectRelativePath().toString();
    if (StringUtils.isNotBlank(path)) {
      return normalize(path);
    }
    return null;
  }

  @CheckForNull
  private static String normalize(@Nullable String path) {
    if (StringUtils.isBlank(path)) {
      return null;
    }
    String normalizedPath = path;
    normalizedPath = normalizedPath.replace('\\', '/');
    normalizedPath = StringUtils.trim(normalizedPath);
    if (PATH_SEPARATOR.equals(normalizedPath)) {
      return PATH_SEPARATOR;
    }
    normalizedPath = StringUtils.removeStart(normalizedPath, PATH_SEPARATOR);
    normalizedPath = StringUtils.removeEnd(normalizedPath, PATH_SEPARATOR);
    return normalizedPath;
  }

  @CheckForNull
  public static IResource findResource(IProject project, String componentKey) {
    String relativePath = StringUtils.substringAfterLast(componentKey, ":");
    IResource resource = project.findMember(relativePath);
    return resource != null ? resource : project;
  }

  @CheckForNull
  public static IPath getAbsolutePath(IPath path) {
    // IPath should be resolved this way in order to handle linked resources (SONARIDE-271)
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource res = root.findMember(path);
    if (res != null) {
      if (res.getLocation() != null) {
        return res.getLocation();
      } else {
        SonarLintCorePlugin.getDefault().error("Unable to resolve absolute path for " + res.getLocationURI());
        return null;
      }
    } else {
      File external = path.toFile();
      if (external.exists()) {
        return path;
      }
      return null;
    }
  }

}