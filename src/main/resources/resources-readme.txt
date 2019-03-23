files that belong in resources:
* icons
* configuration files

files that don't belong in resources:
* source code
* temporary files

files in resources are always available in the classpath.
access the files using a classloader:

  try (InputStream is = getClass().getResourceAsStream("/resources-readme.txt")) {
    // use resource
  }
