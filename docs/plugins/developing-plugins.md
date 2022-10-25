# Plugin Development

:octicons-tag-24: 0.7.0

The following plugin can be used as a boilerplate: [https://github.com/askimed/nft-fasta](https://github.com/askimed/nft-fasta)

## Developing Plugins

A plugin has the possibility:

1. Adding a new method to an existing class (e.g. the property `fasta` to class `Path`). It uses Groovy's ExtensionModule concept. Important: the method has to be static. One class can provide multiple methods.

```java
// com.askimed.nf.test.fasta.PathExtension
public class PathExtension {
  //can be used as: path(filename).fasta
    public static Object getFasta(Path self) {
    return FastaUtil.readAsMap(self);
  }

}
```

2. Providing new methods

```java
// com.askimed.nf.test.fasta.Methods
public class Methods {

  //can be used as: helloFasta()
  public static void helloFasta() {
    System.out.println("Hello FASTA");
  }

}
```

### Manifest file

You need to create a file **`META-INF/nf-test-plugin`** (in your resources). This file contains metadata about the plugin and both classes can now be registered by using the `extensionClasses` and `extensionMethods` properties.

```
moduleName=nft-my-plugin
moduleVersion=1.0.0
moduleAuthors=Lukas Forer
extensionClasses=com.askimed.nf.test.fasta.PathExtension
extensionMethods=com.askimed.nf.test.fasta.Methods
```

### Building a jar file

The plugin itself is a jar file that contains all classes and the `META-INF/nf-test-plugin` file. If you have dependencies then you have to create a uber-jar that includes all libraries, because nf-test doesn't support the classpath set in `META-INF\MANIFEST`.


### Publishing Plugins

Available plugins are managed in this default repository: [https://github.com/askimed/nf-test-plugins/blob/main/plugins.json](https://github.com/askimed/nf-test-plugins/blob/main/plugins.json)

Add your plugin or a new release to the `plugin.json` file and create a pull request to publish your plugin in the default repository. Or host you own repository:

```
[{
  "id": "nft-fasta",
  "releases": [{
    "version": "1.0.0",
    "url": "https://github.com/askimed/nft-fasta/releases/download/v1.0.0/nft-fasta-1.0.0.jar",
  },{
    "version": "2.0.0",
    "url": "https://github.com/askimed/nft-fasta/releases/download/v2.0.0/nft-fasta-2.0.0.jar",
  }]
},{
  "id": "nft-my-plugin",
  "releases": [{
    "version": "1.0.0",
    "url": "https://github.com/lukfor/nft-my-plugin2/releases/download/v1.0.0/nft-my-plugin-1.0.0.jar",
  }]
}]
```
