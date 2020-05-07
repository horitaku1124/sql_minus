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
~/graalvm-ce-java11-20.0.0/Contents/Home/bin/native-image --no-fallback -jar build/libs/*.jar
```

### Native Run

```bash
./sql_minus-1.0-SNAPSHOT 8001
```


### Practice

```
telnet localhost 8001
> create database db_1
> connect db_1
> create table sample_table (id int, name varchar(20), status smallint )
> insert into sample_table(id, name, status) values (101, 'Taro', 1)
> select * from sample_table
> exit
Bye
```