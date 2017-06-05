package com.video.probe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Stream { 
	 
    private long index; 
    private String codec_name; 
    private String codec_long_name; 
    private String profile; 
    private String codec_type; 
    private String codec_time_base; 
    private String codec_tag_string; 
    private String codec_tag; 
    private long width; 
    private long height; 
    private long has_b_frames; 
    private String sample_aspect_ratio; 
    private String display_aspect_ratio; 
    private String pix_fmt; 
    private long level; 
    private String r_frame_rate; 
    private String avg_frame_rate; 
    private String time_base; 
    private long start_pts; 
    private String start_time; 
    private int bit_rate; 
    private Disposition disposition; 
    private Tags tags;
    private String sample_fmt; 
    private String sample_rate; 
    private long channels; 
    private String channel_layout; 
    private long bits_per_sample; 
    private long duration_ts; 
    private String duration;
    private Long nb_frames;
	
}