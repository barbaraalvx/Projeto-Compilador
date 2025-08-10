FROM openjdk:21-bullseye

RUN apt-get update && \
    apt-get install -y python3 python3-pip findutils && \
    pip3 install cram

WORKDIR /app

COPY . .

RUN find src -name "*.java" > sources.txt && \
    javac -d build @sources.txt && \
    rm sources.txt

ENTRYPOINT ["java", "-cp", "build", "Main"]