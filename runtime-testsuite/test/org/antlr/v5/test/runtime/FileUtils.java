/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;

public class FileUtils {

	public static void writeFile(String fileName, String content, String encoding) {
		try {
			File f = new File(fileName);
			FileOutputStream fos = new FileOutputStream(f);
			try(OutputStreamWriter osw = encoding != null ? new OutputStreamWriter(fos, encoding) : new OutputStreamWriter(fos)) {
				osw.write(content);
			}
		} catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	public static void replaceInFile(Path sourcePath, String target, String replacement) throws IOException {
		replaceInFile(sourcePath, sourcePath, target, replacement);
	}

	public static void replaceInFile(Path sourcePath, Path destPath, String target, String replacement) throws IOException {
		String content = new String(Files.readAllBytes(sourcePath), StandardCharsets.UTF_8);
		String newContent = content.replace(target, replacement);
		try (PrintWriter out = new PrintWriter(destPath.toString())) {
			out.println(newContent);
		}
	}

	public static void mkdir(String dir) {
		File f = new File(dir);
		//noinspection ResultOfMethodCallIgnored
		f.mkdirs();
	}

	public static void deleteDirectory(File f) throws IOException {
		if (f.isDirectory() && !isLink(f.toPath())) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File c : files)
					deleteDirectory(c);
			}
		}
		if (!f.delete())
			throw new IOException("Failed to delete file: " + f);
	}

	public static boolean isLink(Path path) {
		try {
			BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			return attrs.isSymbolicLink() || (attrs instanceof DosFileAttributes && attrs.isOther());
		} catch (IOException ignored) {
			return false;
		}
	}
}
