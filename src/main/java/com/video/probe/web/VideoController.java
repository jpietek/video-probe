package com.video.probe.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.video.probe.logic.ProbeLogic;
import com.video.probe.model.ProbeResult;

@RestController
@RequestMapping(path = "/video")
public class VideoController {

	@Autowired
	ProbeLogic probeLogic;

	@RequestMapping(path = "/probe", method = RequestMethod.POST)
	public ProbeResult probe(@RequestPart("video") MultipartFile file) {

		String tmpPath = "/tmp/" + UUID.randomUUID().toString();
		File tmpFile = new File(tmpPath);

		try {
			FileUtils.writeByteArrayToFile(tmpFile, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return new ProbeResult(false, "can't write tmp file");
		}

		try {
			String mimeType = Files.probeContentType(tmpFile.toPath());
			String type = mimeType.split("/")[0];

			// mkvs can get miss-detected as 'application'
			if (!type.equalsIgnoreCase("video") 
					&& !type.equalsIgnoreCase("audio")
					&& !type.equalsIgnoreCase("application")) {
				return new ProbeResult(false, 
						"invalid mime type, expected video, got: " + type);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ProbeResult(false, "io err while probing for mime type");
		}

		return probeLogic.probeVideo(tmpPath);
	}
}
