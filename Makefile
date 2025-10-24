build:
	@./gradlew build

fmt:
	@./gradlew spotlessApply

lint:
	@./gradlew pmdMain

test:
	@./gradlew test

install:
	@./gradlew publishToMavenLocal
