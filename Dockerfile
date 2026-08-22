FROM us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_1:latest

# --- Fashion-Mate project dependencies ---
# Backend: Java 21 + Spring Boot, built with Maven. The repo's ./mvnw wrapper
# has no .mvn/wrapper/maven-wrapper.properties committed, so it can't bootstrap
# itself -- install a real JDK 21 + Maven directly instead.
# Frontend: React/Vite/npm reuses the Node/npm already in the base image;
# `npm install` runs into /workspace at agent runtime and persists there via
# the workspace bind mount, so nothing extra is needed in the image for it.

RUN curl -fsSL https://packages.adoptium.net/artifactory/api/gpg/key/public \
      | tee /etc/apt/trusted.gpg.d/adoptium.asc >/dev/null \
    && echo "deb https://packages.adoptium.net/artifactory/deb bookworm main" \
      | tee /etc/apt/sources.list.d/adoptium.list \
    && apt-get update && apt-get install -y --no-install-recommends \
       temurin-21-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV MAVEN_VERSION=3.9.9
RUN curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
      -o /tmp/maven.tar.gz \
    && tar xzf /tmp/maven.tar.gz -C /opt \
    && ln -s "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn \
    && rm /tmp/maven.tar.gz
ENV CLAUDE_CONFIG_DIR=/claude-auth
