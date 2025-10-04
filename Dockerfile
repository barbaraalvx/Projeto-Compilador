FROM openjdk:21-bullseye

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends python3 python3-pip && \
    pip install cram && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

RUN find src -name "*.java" > sources.txt && \
    javac -d build @sources.txt && \
    rm sources.txt

ENTRYPOINT ["java", "-cp", "build", "Main"]