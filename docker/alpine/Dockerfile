FROM tomcat:9.0-jre8-alpine
MAINTAINER Mateusz Batok <mateusz.batok@orange.com>
RUN apk update \                                                                                                                                                                                                                        
  &&   apk add ca-certificates wget curl\                                                                                                                                                                                                      
  &&   update-ca-certificates   
RUN wget $(curl -s https://api.github.com/repos/earion/WifiQueue/releases/latest | grep browser_download_url | cut -d '"' -f 4)
RUN ls
RUN cp ./HostsQueue.war /usr/local/tomcat/webapps/
EXPOSE 8080


