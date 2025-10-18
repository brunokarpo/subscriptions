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