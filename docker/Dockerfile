FROM tomcat:9
MAINTAINER Mateusz Batok <mateusz.batok@orange.com>
RUN wget $(curl -s https://api.github.com/repos/earion/WifiQueue/releases/latest | grep browser_download_url | cut -d '"' -f 4)
RUN ls
RUN cp ./HostsQueue.war /usr/local/tomcat/webapps/
EXPOSE 8080


