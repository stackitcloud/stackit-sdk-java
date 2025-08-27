build:
	@./gradlew build

fmt:
	@./gradlew spotlessApply

lint:
	@echo "linting not ready yet"

test:
	@./gradlew test

install:
	@./gradlew publishToMavenLocal
