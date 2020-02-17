/**
 * 
 */
package org.graylog.interview.stuglik.files;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class FileUtils {
	
	public static boolean fileExists(String pathStr) {
		try {
			Path path = Paths.get(pathStr);
			return Files.exists(path);
		} catch (InvalidPathException | SecurityException e) {
			return false;
		}
	}
	
	private FileUtils() {
		// dont want this instantiate, simple util class
	}
}
