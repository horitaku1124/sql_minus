SQL Minus
----------------


[![CircleCI](https://circleci.com/gh/horitaku1124/sql_minus.svg?style=svg)](https://circleci.com/gh/horitaku1124/sql_minus)

### Requirement

- Java11 or above

### Build

```bash
./gradlew clean build
```

### Run

```bash
java -jar build/libs/*.jar 8001
```

### Native Build

download [GraalVM20](https://www.graalvm.org/downloads/)

```bash
~/graalvm-ce-java11-20.0.0/Contents/Home/bin/gu install native-image
~/graalvm-ce-java11-20.0.0/Contents/Home/bin/native-image -jar build/libs/*.jar
```

### Native Run

```bash
./sql_minus-1.0-SNAPSHOT 8001
```
