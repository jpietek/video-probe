package com.video.probe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Format { 
	 
    private long nb_streams; 
    private long nb_programs;
    private long probe_score;
    private String format_name; 
    private String format_long_name; 
    private String start_time; 
    private String duration; 
    private String size; 
    private String bit_rate;
    
}
