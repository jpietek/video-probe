package com.video.probe.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		
		String mp4Path = 
				getClass().getClassLoader().getResource("master.mp4").getFile();
		
		ProbeResult res = probeLogic.probeVideo(mp4Path);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 2);
		
		assertEquals("h264", p.getStreams().get(0).getCodec_name());
		assertEquals(2400440, p.getStreams().get(0).getBit_rate());
		assertEquals(new Long(50), p.getStreams().get(0).getNb_frames());
		
		assertEquals("aac", p.getStreams().get(1).getCodec_name());
		assertEquals(98448, p.getStreams().get(1).getBit_rate());
		
		assertEquals(100, p.getFormat().getProbe_score());
	}
	
	@Test
	public void probeTs() {
		String mpegtsPath = 
				getClass().getClassLoader().getResource("master.ts").getFile();
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
		String soundOnlyPath = getClass().getClassLoader()
				.getResource("sound_only.mp3").getFile();
		
		ProbeResult res = probeLogic.probeVideo(soundOnlyPath);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 1);
		
		assertEquals("mp3", p.getStreams().get(0).getCodec_name());
	}
	
	@Test
	public void probeVideoOnly() {
		String videoOnlyPath 
			= getClass().getClassLoader().getResource("video_only.mp4").getFile();
		
		ProbeResult res = probeLogic.probeVideo(videoOnlyPath);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		assertTrue(p.getStreams().size() == 1);
		
		assertEquals("h264", p.getStreams().get(0).getCodec_name());
	}
	
	@Test
	public void probeProRes() {
		String proResPath = 
				getClass().getClassLoader().getResource("master.mov").getFile();
		ProbeResult res = probeLogic.probeVideo(proResPath);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();

		assertEquals("prores", p.getStreams().get(0).getCodec_name());
		assertEquals("yuva444p10le", p.getStreams().get(0).getPix_fmt());
	}
	
	@Test
	public void probeMkv() {
		String mkvPath = 
				getClass().getClassLoader().getResource("master.mkv").getFile();
		ProbeResult res = probeLogic.probeVideo(mkvPath);
		
		assertTrue(res.isSuccess());
		Probe p = (Probe) res.getResult();
		
		assertTrue(p.getFormat().getFormat_name().contains("matroska"));
	}
}
