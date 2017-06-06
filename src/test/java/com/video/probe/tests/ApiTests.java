package com.video.probe.tests;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.video.probe.web.RateLimiter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
	@LocalServerPort
	int port;

	@Autowired
	RateLimiter rateLimiter;

	private final File BROKEN_MP4 
	= new File(getClass().getClassLoader().getResource("broken.mp4").getFile());

	private final File VALID_MP4 
	= new File(getClass().getClassLoader().getResource("master.mp4").getFile());

	private final File VALID_WEBM 
	= new File(getClass().getClassLoader().getResource("master.webm").getFile());

	
	private final File TXT_FILE 
	= new File(getClass().getClassLoader().getResource("txt").getFile());

	private final File INVALID_SIZE 
	= new File(getClass().getClassLoader().getResource("25mb_plus_1b").getFile());

	private final File VALID_SIZE 
	= new File(getClass().getClassLoader().getResource("25mb.mp4").getFile());
	
	private static final String VALID_PASS = "ODBjYTNkMDBiNzcyMGEyM2JkNWFlNmJi";
	private static final String INVALID_PASS = "ODBjYTNkMDBiNzcyMGEyM2JkNWFlNmJj";

	@After
	public void resetReqLimit() {
		rateLimiter.flush();
	}

	@Test
	public void uploadTooBigFile() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", INVALID_SIZE)

		.expect()
		.statusCode(403)
		.body("success", is(false))
		.body("message", containsString("exceeds max size"))
		.when()
		.post("/video/probe");
	}

	@Test
	public void uploadMp4Exactly25M() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", VALID_SIZE)

		.expect()
		.body("success", is(true))
		.body("message", is("ffprobe ok"))
		.when()
		.post("/video/probe");
	}

	@Test
	public void uploadNothing() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", "")

		.expect()
		.statusCode(403)
		.body("success", is(false));
	}

	@Test
	public void uploadInvalidMimeType() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", TXT_FILE)

		.expect()
		.statusCode(200)
		.body("success", is(false))
		.body("message", containsString("invalid mime type"))

		.when()
		.post("/video/probe");
	}

	@Test
	public void uploadBrokenVideo() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", BROKEN_MP4)

		.expect()
		.statusCode(200)
		.body("success", is(false))
		.body("message", containsString("invalid media file"))

		.when()
		.post("/video/probe");
	}

	@Test
	public void wrongUser() {
		given().port(port)
		.auth().basic("user2", VALID_PASS)
		.expect()
		.statusCode(401);
	}

	@Test
	public void wrongPass() {
		given().port(port)
		.auth().basic("user", INVALID_PASS)
		.expect()
		.statusCode(401);
	}

	@Test
	public void wrongMethod() {
		given().port(port)
		.auth().basic("user", VALID_PASS)
		.expect()
		.statusCode(405)
		.when()
		.get("/video/probe");
	}

	public String validVideo() {
		return given().port(port)
		.auth().basic("user", VALID_PASS)
		.multiPart("video", VALID_MP4)
		.when()
		.post("/video/probe")
		.thenReturn().getBody().jsonPath().get("message").toString();
	}
	
	@Test
	public void checkNotExistingField() {
		
		// webm stream:0 does not contain bitrate, so it should not be
		// included in output json, expect null not misleading long = 0
		String bitrate = given().port(port)
				.auth().basic("user", VALID_PASS)
				.multiPart("video", VALID_WEBM)
				.when()
				.post("/video/probe")
				.thenReturn().getBody().jsonPath().get("bit_rate");
		
		assertEquals(null, bitrate);
	}

	private boolean spamRequests(int n) {
		ExecutorService e = Executors.newFixedThreadPool(n);

		CompletableFuture<String>[] futures = new CompletableFuture[n];
		for(int i = 0; i < n; i++) {
			futures[i] = CompletableFuture.supplyAsync(() -> {
				return validVideo();
			}, e);
		}
		
		CompletableFuture.allOf(futures);
		
		for(CompletableFuture<String> f: futures) {
			if(f.join().contains("rate exceeded")) {
				return false;
			}
		}
		return true;
	}

	// Fire up 10 requests at once, one should be denied 
	// as it hits api rate limit
	@Test
	public void overRateLimit() {
		assertFalse(spamRequests(10));
	}

	// Fire up 9 requests at once, which is exactly below rate limit
	// every req should be processed with result = true
	@Test
	public void withinRateLimit() {
		assertTrue(spamRequests(9));
	}
}

