build:
	@./gradlew build

fmt:
	@./gradlew spotlessApply

lint:
	@./gradlew pmdMain
	@./gradlew lintProjectVersion

test:
	@./gradlew test

install:
	@./gradlew publishToMavenLocal
