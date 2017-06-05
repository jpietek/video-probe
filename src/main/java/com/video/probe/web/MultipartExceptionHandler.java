package com.video.probe.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.probe.model.ProbeResult;

@ControllerAdvice
public class MultipartExceptionHandler extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (MaxUploadSizeExceededException e) {
            handle(request, response, e);
        } catch (ServletException e) {
            if(e.getRootCause() instanceof MultipartException) {
                handle(request, response, (MultipartException) e.getRootCause());
            } else {
                throw e;
            }
        }
    }

    private void handle(HttpServletRequest request,
            HttpServletResponse response, MultipartException e) throws ServletException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
		ProbeResult resp = new ProbeResult(false, "upload error, no file provided or "
				+ "probed file exceeds max size limit of 25mb");
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().write(mapper.writeValueAsString(resp));
    }

}