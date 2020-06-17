FROM ubuntu:16.04
RUN apt-get update \
        && apt-get install -y apt-transport-https apt-utils \
		&& apt-get update \
        && apt-get upgrade -y \
        && apt-get install -y software-properties-common \
		&& add-apt-repository ppa:openjdk-r/ppa \
        && apt-get update \
        && apt-get install -y openjdk-11-jdk \
        && apt-get install -y gcc g++ \
        && useradd -u 5000 code \
        && mkdir -p /judger \
        && mkdir -p /purple \
        && mkdir -p /judger/judge \
        && mkdir -p /judger/data \
        && chown -R code:code /judger/judge \
        && chown -R code:code /judger/data
ADD purple-judger.jar /purple/
ADD application.yml /purple/
ADD libjudger.so /purple/
WORKDIR /purple
ENTRYPOINT ["java", "-jar", "purple-judger.jar"]
