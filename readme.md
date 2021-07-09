Create a volume for storing data of the container and a network where the containers can communicate

docker volume create soavolume

docker network create soanet

(

To start ActiveMQ as Docker container (https://hub.docker.com/r/rmohr/activemq/), if needed:

docker pull rmohr/activemq

docker run -p 61616:61616 -p 8161:8161 --network=soanet --name=actmq rmohr/activemq

Needed queues: "request", "response".

)

Import the WS project in Docker:

docker build --build-arg JAR_FILE=build/libs/web_services-0.0.1-SNAPSHOT.jar -t lib_proj .

docker run -p 8080:8080 --mount source=soavolume,target=/soadata/ --network=soanet --name=ws_cont lib_proj