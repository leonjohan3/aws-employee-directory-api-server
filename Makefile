.PHONY: build run dropbear

IMAGE_NAME = employee-directory-api-server
DROPBEAR_IMAGE_NAME = dropbear

build:
	./gradlew build
	docker build --progress plain -t $(IMAGE_NAME) .

run: build
	docker run --rm -p 8080:8080 -d --name $(IMAGE_NAME) $(IMAGE_NAME)

dropbear:
	docker build --progress plain -f Dockerfile-dropbear -t $(DROPBEAR_IMAGE_NAME) .
	#docker run --rm -ti -p 2222:22 -d --name $(DROPBEAR_IMAGE_NAME) $(DROPBEAR_IMAGE_NAME)

