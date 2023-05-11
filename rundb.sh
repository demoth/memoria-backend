# cleanup
docker stop memoria-mongo ; docker rm memoria-mongo ; docker volume rm mongo-data

# run
docker run -d -p 27017:27017 --name memoria-mongo -e MONGO_INITDB_ROOT_USERNAME=asdf -e MONGO_INITDB_ROOT_PASSWORD=qwer -v mongo-data:/data/db mongo:latest
