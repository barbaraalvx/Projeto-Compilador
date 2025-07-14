FROM openjdk:21-bullseye

RUN apt-get update && \
    apt-get install -y python3 python3-pip findutils && \
    pip3 install cram

WORKDIR /app

COPY . .

RUN mkdir -p build && \
    javac -d build src/Main.java src/utils/*.java src/syntacticanalysis/*.java

ENTRYPOINT ["java", "-cp", "build", "Main"]
