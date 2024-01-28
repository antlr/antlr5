FROM eclipse-temurin:11 AS builder

WORKDIR /opt/antlr4

ARG ANTLR_VERSION="0.0.1-snapshot"
ARG MAVEN_OPTS="-Xmx1G"


RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install maven git -y \
    && git clone https://github.com/antlr/antlr5.git \
    && cd antlr5 \
    && git checkout $ANTLR_VERSION \
    && mvn clean --projects tool --also-make \
    && mvn -DskipTests install --projects tool --also-make \
    && mv ./tool/target/antlr5-*-complete.jar antlr5-tool.jar

FROM eclipse-temurin:11-jre

ARG user=appuser
ARG group=appuser
ARG uid=1000
ARG gid=1000

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --no-create-home \
    --uid "${uid}" \
    "${user}"

COPY --from=builder /opt/antlr5/antlr5/antlr5-tool.jar /usr/local/lib/
WORKDIR /work
ENTRYPOINT ["java", "-Xmx500M", "-cp", "/usr/local/lib/antlr5-tool.jar", "org.antlr.v5.Tool"]


