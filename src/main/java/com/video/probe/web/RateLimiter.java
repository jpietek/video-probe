package com.video.probe.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.probe.model.ProbeResult;

@Component
public class RateLimiter extends HandlerInterceptorAdapter {

	private final static int MAX_REQ_PER_SECOND = 10;
	
	private static final Logger logger 
		= LoggerFactory.getLogger(RateLimiter.class);
	
	private CircularFifoQueue<Long> requests = 
			new CircularFifoQueue<Long>(MAX_REQ_PER_SECOND);

	public void flush() {
		requests.clear();
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
					throws Exception {

		requests.add(System.currentTimeMillis());
		
		if(requests.isAtFullCapacity() 
				&& requests.peek() > System.currentTimeMillis() - 1000) {
			logger.error("rate exceeded");
			
			ObjectMapper mapper = new ObjectMapper();
			ProbeResult resp = new ProbeResult(
					false, "rate exceeded, max 10 requests per second allowed");
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().write(mapper.writeValueAsString(resp));
			return false;
		}

		return true;
	}

}