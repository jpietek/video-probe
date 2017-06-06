## Synopsis

Simple REST service written in Spring Boot that extracts metadata from media files. Wrapper around ffprobe 3.3.1.

## Example usage

curl -F 'video=@<path_to_file>' \<user>:\<password>@\<host>:8080/video/probe

## Output format

Each valid request should respond with json:

{ message: \<"ffprobe ok" or failure message>, <br />
result: \<ffprobe json>, <br />
success: \<boolean> }

If the metadata api call fails, success flag is false and result is null.

ffprobe json : { 
	array_of_streams : [ stream1, stream2 etc. ], (by convention 0th index is video, 1st is audio) <br />
	format : { } <- per container metadata, like number of streams, bitrate or size in bytes
}

stream : {
	"index" : \<long>, <br />
	"codec" : \<codec shortname>, <br />
	"codec_type" : \<audio/video>, <br />
	"bit_rate" : \<long> (not always available per stream) <br />
	(...) <br /> 
}
	
Stream metadata may also contain disposition info and tags. For details refer to ffprobe documentation https://ffmpeg.org/ffprobe.html


## Build

mvn package -> jar
mvn package docker:build -> docker image

## Features

- basic http auth
- 25MB POST limit
- simple rate limit of 10req/s not to overkill nano aws instance
- mime type pre-check, to filter out obviously invalid files and save up some ffprobing power


## Tests

- ApiTest focused on proper http responses, input validation
- ProbeTests, ffprobing tests using some popular container/codecs combinations (sample files included in the test/resources)

