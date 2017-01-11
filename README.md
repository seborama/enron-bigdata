# enron-bigdata

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

Then (on OSX) type:

```bash
open target/site/index.html
```

Or just open the index.html file manually in your browser of choice.

### Generate Javadoc

```bash
mvn javadoc:javadoc
```

Then (on OSX) type:

```bash
open target/site/apidocs/index.html
```

Or just open the index.html file manually in your browser of choice.
