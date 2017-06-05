package com.video.probe.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.probe.model.Probe;
import com.video.probe.model.ProbeResult;

@Service
public class ProbeLogic {

	private static final Logger logger 
		= LoggerFactory.getLogger(ProbeLogic.class);
	
	public ProbeResult probeVideo(String videoPath) {
		final String[] cmds = { "ffprobe", 
				"-v", "quiet", "-print_format", "json", 
				"-show_format", "-show_streams", videoPath };

		try {
			ProcessBuilder pb = new ProcessBuilder(cmds);
			Process proc = pb.start();
			
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String json = builder.toString();

			ObjectMapper mapper = new ObjectMapper();
			Probe p = mapper.readValue(json, Probe.class);
			
			proc.waitFor();
			
			return (proc.exitValue() == 0) ? 
					new ProbeResult(true, "ffprobe ok", p) :
						new ProbeResult(false, "ffprobe failed, invalid media file?");

		} catch (Exception e) {
			e.printStackTrace();
			return new ProbeResult(false, "ffprobe failed");
		} finally {
			try {
				FileUtils.forceDelete(new File(videoPath));
			} catch (IOException e) {
				logger.error("io exception while deleting file: " + videoPath);
			}
		}
	}
}
