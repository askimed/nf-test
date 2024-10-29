# Plugins

:octicons-tag-24: 0.7.0

Most assertions are usecase specific. Therefore, separating this functionality and helper classes from the nf-test codebase has several advantages:

1. nf-test releases are independent from plugin releases
2. it is easier for third-parties to develop and maintain plugins
3. it is possible to use private repositories to integrate private/protected code in plugins without sharing them

For this purpose, we integrated the following plugin system that provides (a) the possibility to extend existing classes with custom methods (e.g. `path(filename).fasta`) and (2) to extends nf-test with new methods.


## Using Plugins

Available plugins are listed [here](https://plugins.nf-test.com).

A plugin can be activated via the `nf-test.config` by adding the `plugin` section and by using `load` method to specify the plugin and its version:

```
config {

  plugins {

    load "nft-fasta@1.0.0"

  }

}
```

It is also possible to add one ore more additional repositories. (Example: repository with development/snapshot versions, in-house repository, ...)

```
config {

  plugins {

    repository "https://github.com/askimed/nf-test-plugins/blob/main/plugins-snapshots.json"
    repository "https://github.com/seppinho/nf-test-plugin2/blob/main/plugins.json"

    load "nft-fasta@1.1.0-snapshot"
    load "nft-plugin2@1.1.0"

    // you can also load jar files directly without any repository
    // loadFromFile "path/to/my/nft-plugin.jar"
  }

}
```

All plugins are downloaded and cached in `.nf-test\plugins`. This installation mechanism is yet not safe for parallel execution when multiple nf-test instances are resolving the same plugin. However, you can use `nf-test update-plugins` to download all plugins before you run your tests in parallel.

To clear the cache and to force redownloading plugins and repositories you can execute the `nf-test clean` command.

One or multiple plugins can be activated also via the `--plugins` parameter:

```
nf-test test my-test.nf.test --plugins nft-fasta@1.0.0:plugin2@1.0.0
```

or

```
nf-test test my-test.nf.test --plugins path/to/my/nft-plugin.jar
```
