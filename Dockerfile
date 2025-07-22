# Использование OpenJDK в качестве базового образа
FROM eclipse-temurin:21-jdk-jammy

# Установка рабочей директории
WORKDIR /app

# Копирование файла Maven-проекта (pom.xml) и исходников
# Это позволяет Docker кэшировать слои и не пересобирать зависимости при каждом изменении кода
COPY pom.xml .
COPY src ./src

# Сборка Spring Boot приложения
# Используем mvnw (Maven Wrapper) для уверенности, что Maven доступен
RUN ./mvnw clean package -DskipTests

# Этап запуска - используется уменьшенный образ OpenJDK для более маленького финального образа
FROM eclipse-temurin:21-jre-jammy

# Установка рабочей директории
WORKDIR /app

# Копирование собранного JAR-файла из предыдущего этапа
# * - wildcard для поиска jar-файла, так как имя может меняться с версиями
COPY --from=0 /app/target/*.jar app.jar

# Открытие порта, на котором слушает Spring Boot
EXPOSE 8080

# Запуск Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]