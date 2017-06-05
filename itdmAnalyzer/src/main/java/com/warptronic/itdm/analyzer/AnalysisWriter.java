package com.warptronic.itdm.analyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class AnalysisWriter {
	
	private static final String LOCATION = "./output/";
	
	private Path filePath;
	private List<String> lineToWrite;
	
	public AnalysisWriter(String fileName) {
		
		this.filePath = Paths.get(LOCATION + fileName);
		this.lineToWrite = new ArrayList<>();
	}
	
	public void writeHeader(String text) {
		writeFormatted("*** %s ***", text);
	}
	
	public void writeFormatted(String format, Object... params) {
		lineToWrite.add(String.format(format, params));
	}
	
	public void writeln(String text) {
		lineToWrite.add(text);
	}
	
	public void newLine() {
		lineToWrite.add("");
	}
	
	public void flushToFile() throws IOException {
		
		if (!Paths.get(LOCATION).toFile().exists()) {
			Files.createDirectories(Paths.get(LOCATION));
		}
		
		Files.write(filePath, lineToWrite, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING, WRITE);
	}

}
