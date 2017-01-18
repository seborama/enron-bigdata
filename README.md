# enron-bigdata

[![Build Status](https://travis-ci.org/seborama/enron-bigdata.svg?branch=master)](https://travis-ci.org/seborama/enron-bigdata)

Sainsbury's BigData test.

## Maven

### Running the tests

```bash
mvn test
```

### Create the application JAR file

```bash
mvn package
ls -la target/seborama.enron-bigdata-1.0-SNAPSHOT.jar
```

### Running the application

You need to create the jar file first (see `mvn package` above)

```bash
java -jar seborama.enron-bigdata-1.0-SNAPSHOT.jar path/to/enron/v2/zip/files
```

### Building the maven reporting site

```bash
mvn site
```

The output is available from:

```bash
open target/site/index.html
```

### Generate Javadoc

```bash
mvn javadoc:javadoc
```

The output is available from:

```bash
open target/site/apidocs/index.html
```
