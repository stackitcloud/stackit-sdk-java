build:
	@./gradlew build

fmt:
	@./gradlew spotlessApply

test:
	@./gradlew test

install:
	@./gradlew publishToMavenLocal
