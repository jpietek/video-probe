## Synopsis

Simple REST service written in Spring Boot that extracts metadata from media files. Wrapper around ffprobe 3.3.1.

## Example usage

curl -F 'video=@<path_to_file>' \<user>:\<password>@\<host>:8080/video/probe

## Output format

Each valid request should respond with json:

{ <br /> 
&nbsp;&nbsp;&nbsp;	message: \<"ffprobe ok" or failure message>, <br />
&nbsp;&nbsp;&nbsp;	result: \<ffprobe json>, <br />
&nbsp;&nbsp;&nbsp;	success: \<boolean> <br /> 
}

If the metadata api call fails, success flag is false and result is null.

ffprobe json : { <br /> 
&nbsp;&nbsp;&nbsp; streams : [ stream1, stream2 etc. ], (by convention 0th index is usually video, 1st - audio) <br />
&nbsp;&nbsp;&nbsp; format : { } (per container metadata, like number of streams, bitrate or size in bytes) <br />
}

stream : { <br />
&nbsp;&nbsp;&nbsp; "index" : \<long>, <br />
&nbsp;&nbsp;&nbsp; "codec" : \<codec shortname>, <br />
&nbsp;&nbsp;&nbsp; "codec_type" : \<audio/video>, <br />
&nbsp;&nbsp;&nbsp; "bit_rate" : \<long>, (not always available per stream) <br />
	(...) <br /> 
}
	
Stream metadata apart from additional technical info may contain disposition and tags. For details refer to ffprobe documentation https://ffmpeg.org/ffprobe.html


## Build

mvn package -> jar <br />
mvn package docker:build -> docker image <br />
<br />
run with: java -jar probe-video.jar <br />

## Features

- basic http auth
- 25MB POST limit
- simple rate limit of 10req/s not to overkill nano aws instance
- mime type pre-check, to filter out obviously invalid files and save up some ffprobing power


## Tests

- ApiTest focused on proper http responses, input validation
- ProbeTests, ffprobing tests using some popular container/codecs combinations (sample files included in the test/resources)

