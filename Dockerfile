FROM openjdk:21

WORKDIR /app

COPY src/utils/ src/utils/
COPY src/Main.java src/Main.java

RUN mkdir -p build && \
    javac -d build src/Main.java src/utils/*.java

WORKDIR /app

ENTRYPOINT ["java", "-cp", "build", "Main"]