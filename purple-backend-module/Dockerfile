FROM ubuntu:16.04
RUN apt-get update \
	&& apt-get install -y apt-transport-https apt-utils \
	&& apt-get update \
	&& apt-get upgrade -y \
	&& apt-get install -y software-properties-common \
	&& add-apt-repository ppa:openjdk-r/ppa \
	&& apt-get update \
	&& apt-get install -y openjdk-11-jdk
ADD purple-backend.jar /purple/
ADD application.yml /purple/
WORKDIR /purple
ENTRYPOINT ["java", "-jar", "purple-backend.jar"]
