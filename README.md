# enron-bigdata

[![Build Status](https://travis-ci.org/seborama/enron-bigdata.svg?branch=master)](https://travis-ci.org/seborama/enron-bigdata)

Sainsbury's BigData test.

## Requirements

This project requires to download the Enron email dataset. A starting point may be:

https://www.cs.cmu.edu/~enron/

Extract the dataset and supply the path when running the application (see below).

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

`path/to/enron/v2/zip/files` references the path to the Enron email dataset as explained in the `Requirements`

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
