package com.video.probe.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Probe { 
	 
    private List<Stream> streams = new ArrayList<Stream>(); 
    private Format format; 
 
}
