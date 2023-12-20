.PHONY: build run

IMAGE_NAME = employee-directory-api-server

build:
	./gradlew build
	docker build --progress plain -t $(IMAGE_NAME) .

run: build
	docker run --rm -p 8080:8080 -d --name $(IMAGE_NAME) $(IMAGE_NAME)
