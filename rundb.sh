# cleanup
docker stop memoria-mongo ; docker rm memoria-mongo ; docker volume rm mongo-data

# run
docker run -d -p 27017:27017 --name memoria-mongo -v mongo-data:/data/db mongo:latest
