# Build application
.PHONY: build
build:
	./mvnw clean package

.PHONY: build-no-test
build-no-test:
	./mvnw clean package -DskipTests=true

.PHONY: run
run:
	docker compose up -d
	./mvnw -f infra/app spring-boot:run


# Docker
.PHONY: build-image
build-image: build-no-test
	docker image build -t nom.brunokarpo/subscriptions:latest -t nom.brunokarpo/subscriptions:local .

.PHONY: run-image
run-image:
	docker compose up -d
	docker container run --rm --name subscription-app --network host --publish 8080:8080 --publish 8081:8081 nom.brunokarpo/subscriptions:local