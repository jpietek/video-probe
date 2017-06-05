FROM ubuntu:16.04

RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN apt-get update
RUN apt-get install -y software-properties-common python-software-properties
RUN apt-add-repository ppa:webupd8team/java
RUN apt-add-repository ppa:jonathonf/ffmpeg-3
RUN apt-get update
RUN apt-get install -y ffmpeg
RUN apt-get install -y oracle-java8-installer
RUN apt-get clean all
RUN apt-get autoremove

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

ADD probe-video-0.1.0.jar app.jar
RUN sh -c 'touch /app.jar'
