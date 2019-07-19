FROM ubuntu:14.04
MAINTAINER Mick Giles <mick@victiv.com>

# update the image
RUN apt-get update -y
RUN apt-get -y upgrade
RUN apt-get -y install software-properties-common

# install java
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update -y
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get -y install oracle-java8-installer
RUN update-alternatives --display java

# Add staged Play app
ADD bin/ /opt/play/bin/
ADD conf/ /opt/play/conf/
ADD lib/ /opt/play/lib/
ADD share/ /opt/play/share/

# open the ports
EXPOSE 9000

# define the run command
CMD ["/opt/play/bin/ruckus", "-Dhttp.port=9000"]
