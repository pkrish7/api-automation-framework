# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

# Run stage (use Maven image so mvn is available)
FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app
COPY --from=build /app .
ENV TEST_ENV=qa
CMD ["mvn", "test", "-DsuiteXmlFile=testng.xml", "-Denv=${TEST_ENV}"]
