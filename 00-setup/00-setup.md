systemctl stop docker.socket
systemctl stop docker

systemctl start docker.socket
systemctl start docker

systemctl status docker.socket
systemctl status docker

# start solace as container
docker run -d --restart unless-stopped -p 8080:8080 -p 55555:55555 -p 8008:8008 -p 1883:1883 -p 8000:8000 -p 5672:5672 -p 9000:9000 -p 2222:2222 --shm-size=2g --env username_admin_globalaccesslevel=admin --env username_admin_password=admin --name=solace solace/solace-pubsub-standard

# update container in restart mode instead killed
docker update --restart unless-stopped solace

# below command will ensure all currently running containers will be restarted unless stopped.
docker update --restart unless-stopped $(docker ps -q)

http://localhost:8080/

docker container ls -a
docker container prune
docker exec -it solace /usr/sw/loads/currentload/bin/cli -A
    show version
    show service
    show message-vpn *

docker update --restart unless-stopped solace