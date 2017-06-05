package com.video.probe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Disposition {

	    private long _default; 
	    private long dub; 
	    private long original; 
	    private long comment; 
	    private long lyrics; 
	    private long karaoke; 
	    private long forced; 
	    private long hearing_impaired; 
	    private long visual_impaired; 
	    private long clean_effects; 
	    private long attached_pic;
	
}
