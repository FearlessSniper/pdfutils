# pdfutils

## Summary

*pdfutils* is a set of CLI tools to manipulate PDF documents.

The features include:
- encrypting the PDF
- converting the PDF to Word document (with images)
- retrieving info from the PDF
- remove the encryption from PDF
- merge several PDF into one
- rotate pages of the PDF
- render the PDF into images
- reorder the pages of PDF

## Build
### Prerequisites
The project is developed with JDK 11, so JDK 11 is required.

Dependencies include:
- Apache pdfbox
- Apache poi
- Apache commons-io
- picocli

### Building
To build the project, maven is required.
```Shell
mvn package
```
As only native-image with awt is supported on Linux, the package command builds a native executable (with the jar) on Linux. For all of the platforms, the `package` command will build a self-contained jar including all the dependencies.

### Run
After downloading the JRE and adding to path, etc., the pdfutils app can be run by executing the pdfutils jar:
```Shell
cd target
java -jar pdfutils-1.0-jar-with-dependencies.jar -V # Shows version
```
