# Package management in Java

## The what management?

It's rather common to use code written by other people.
Why reinvent the wheel and write a database driver / web framework / email client if one already exists.
There are tools to include code from other projects and we'll learn them very soon.
But first, let's see how code is distributed in Java world.

A Java project usually consists of three parts:

* a unique name of the project
* the compiled code (.class files)
* project resources (config files, icons, static content)

When packaging a project, the code is compiled and the resulting class files and resources are packed into a regular zip archive.
The zip archive's file name extension must be .jar (Java ARchive) instead of the usual .zip.
The class files preserve their usual directory structure defined by the classes' package names.

*sample-maven-app-1.0-SNAPSHOT.jar:*

```
META-INF/
META-INF/MANIFEST.MF
mypackage/
mypackage/MyApp.class
resources-readme.txt
```

The sample archive contains the class `mypackage.MyApp` and two resource files: `MANIFEST.MF` and `resources-readme.txt`.

A project can be packaged manually without using any special tools:
have the IDE compile the classes, open an archive tool (7zip, winrar, ...) and add the class files to a new zip file.
Then add any other files to the zip if needed.
Finally, rename the file extension to jar.
The jar file can then be used by any other java project.

## Using dependencies

To use classes from a jar file with IntelliJ, open the project structure dialog, find the libraries tab and include the jar file as a dependency.
IntelliJ will automatically index the jar and allow using the contained classes.
Similarly, the jar of a database driver / web framework / email client can be downloaded from the internet and included as a dependency.

Now starts the trouble.
If a project contains many modules and these are all packaged manually, then someone must do the dull and error prone job of zipping everything.
When a useful dependency has dependencies of its own (each of which can have more dependencies) then finding all the jars becomes a pain.
When a jar has several versions and each version can depend on different versions of its dependencies, then matching the versions becomes a nightmare.
All this is solved by build tools.
We will focus on a tool called *apache maven*.

## What is Maven

Maven is a rather old and popular tool that can do almost everything.
Among other things it can:

* compile java sources
* package a project into a jar file
* resolve and download dependencies

Maven is a convention-over-configuration tool.
To get along with maven, the following requirements must be met:

* all sources are placed in the `src/main/java` directory
* all resources are placed in the `src/main/resources` directory
* all test sources are placed in the `src/test/java` directory
* all test resources are placed in the `src/test/resources` directory
* each project will contain a **pom.xml** file in the project root directory (more on that later)

To install maven, download the binary zip from https://maven.apache.org/download.cgi and follow the install instructions at https://maven.apache.org/install.html.
Linux users: just use your package manager.

When everything is set up correctly, then the following one-liners can be used on the command line (in the project root directory):

* `mvn compile` (compile all the code)
* `mvn test` (compile and run the tests)
* `mvn package` (package the entire project)
* `mvn clean` (delete the compiled code and packages)
* `mvn clean package` (delete all, compile, run tests, package)

The pom.xml contains the project name and version along with the list of dependencies the project is using.
All the dependencies declared in the pom.xml are automatically downloaded and become usable in the project.
IntelliJ has excellent integration with maven - maven commands can be invoked directly from IntelliJ and the dependencies are automatically imported.

## The pom.xml

A pom.xml is the main configuration file for each maven project.
It is written in xml, which is reasonably intuitive.
Take a look at the pom.xml in this repository right now.
Writing a pom.xml from scratch is not a job for a sane person.
Usually it is put together by copy-pasting stuff from stack overflow or an existing project.

Each project has "maven coordinates", which is a combination of a groupId (company name, usually the reversed domain name), an artifactId (project name) and a version (-SNAPSHOT suffix is used to mark non-release versions).
Maven coordinates are also used to reference dependencies.

Many public packages are available from Maven Central.
To add a dependency, find its coordinates in maven central and add the info to pom.xml.
Use https://mvnrepository.com/ or https://search.maven.org/ to search for packages by name.

```
<dependencies>
  <dependency>
    <groupId>???</groupId>
    <artifactId>???</artifactId>
    <version>???</version>
  </dependency>
</dependencies>
```

## Task 1

A poor chap on stackoverflow is having trouble packing some files: https://stackoverflow.com/q/23265857

Clone this repository and open it in IntelliJ.
Try to add his compression method and compile it.
You are missing the *org.apache.commons.io.IOUtils* class (don't try to use *sun.misc.IOUtils* instead of it).
Add the missing dependency for commons-io by changing the pom.xml.
Use groupId `commons-io`, artifactId `commons-io` and look up the latest 2.x version from [MavenRepository](https://mvnrepository.com/) or [Maven Central](https://search.maven.org/).
Package the sample project into a jar file: open the maven toolbar (view -> tool windows -> maven projects).
Find lifecycle -> package and run it.
Open the created jar (in the target directory) and see what's inside.

## Task 2

Add a dependency for [gson](https://github.com/google/gson).
Create a simple class that contains some fields.
Create some instances of the class.
Read the [gson user guide](https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples) and try to convert the objects to string and back again.

## Task 3

Convert your team's project repository to use maven:
* add the pom.xml
* move the code from src/ to src/main/java/
* add a line "target" to .gitignore (maven puts all compiled code into the "target" directory)
