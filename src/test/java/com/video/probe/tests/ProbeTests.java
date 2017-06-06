package com.video.probe.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.video.probe.logic.ProbeLogic;
import com.video.probe.model.Probe;
import com.video.probe.model.ProbeResult;
import com.video.probe.web.RateLimiter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProbeTests {

	@Autowired
	RateLimiter rateLimiter;

	@Autowired
	ProbeLogic probeLogic;

	@After
	public void resetReqLimit() {
		rateLimiter.flush();
	}

	@Test
	public void probeMp4() {

		String mp4Path = getClass().getClassLoader().getResource("master.mp4").getFile();

		ProbeResult res = probeLogic.probeVideo(mp4Path);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 2);

		assertEquals("h264", p.getStreams().get(0).getCodec_name());
		assertEquals(Long.valueOf(2400440), p.getStreams().get(0).getBit_rate());
		assertEquals(Long.valueOf(50), p.getStreams().get(0).getNb_frames());

		assertEquals("aac", p.getStreams().get(1).getCodec_name());
		assertEquals(Long.valueOf(98448), p.getStreams().get(1).getBit_rate());

		assertEquals(Long.valueOf(100), p.getFormat().getProbe_score());
	}

	@Test
	public void probeTs() {
		String mpegtsPath = getClass().getClassLoader().getResource("master.ts").getFile();
		ProbeResult res = probeLogic.probeVideo(mpegtsPath);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 2);

		assertEquals("mpeg2video", p.getStreams().get(0).getCodec_name());

		// ts does not contain frame count
		assertEquals(null, p.getStreams().get(0).getNb_frames());
	}

	@Test
	public void probeMp3() {
		String soundOnlyPath = getClass().getClassLoader().getResource("sound_only.mp3").getFile();

		ProbeResult res = probeLogic.probeVideo(soundOnlyPath);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 1);

		assertEquals("mp3", p.getStreams().get(0).getCodec_name());
		assertEquals(Long.valueOf(128000), p.getStreams().get(0).getBit_rate());
	}

	@Test
	public void probeVideoOnly() {
		String videoOnlyPath = getClass().getClassLoader().getResource("video_only.mp4").getFile();

		ProbeResult res = probeLogic.probeVideo(videoOnlyPath);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 1);

		assertEquals("h264", p.getStreams().get(0).getCodec_name());
	}

	@Test
	public void probeProRes() {
		String proResPath = getClass().getClassLoader().getResource("master.mov").getFile();
		ProbeResult res = probeLogic.probeVideo(proResPath);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();

		assertEquals("prores", p.getStreams().get(0).getCodec_name());
		assertEquals(Long.valueOf(60079680), p.getStreams().get(0).getBit_rate());

		// check fancy prores pixel format with alpha channel
		assertEquals("yuva444p10le", p.getStreams().get(0).getPix_fmt());
	}

	@Test
	public void probeWebm() {
		String webmPath = 
				getClass().getClassLoader().getResource("master.webm").getFile();
		ProbeResult res = probeLogic.probeVideo(webmPath);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();

		assertEquals("Google VP9", p.getStreams().get(0).getCodec_long_name());
		
		// no per-stream bitrate in vp9
		assertEquals(null, p.getStreams().get(0).getBit_rate());
		
		// format, container bitrate
		assertEquals("220496", p.getFormat().getBit_rate());
		assertEquals("Opus (Opus Interactive Audio Codec)", p.getStreams().get(1).getCodec_long_name());
	}

	@Test
	public void probeMkv() {
		String mkvPath = getClass().getClassLoader().getResource("master.mkv").getFile();
		ProbeResult res = probeLogic.probeVideo(mkvPath);

		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();

		assertTrue(p.getFormat().getFormat_name().contains("matroska"));
	}
}
