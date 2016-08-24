# DepVis (Java 9 Jigsaw Dependency Visualizer)

### Info
Written by [Martin Lehmann](https://github.com/MartinLehmann1971), [Kristine Schaal](https://github.com/kristines) and Rüdiger Grammes.

Version 0.1

### What is this about?
DepVis visualizes dependencies of Java 9 Jigsaw modules as defined in [Project Jigsaw](http://openjdk.java.net/projects/jigsaw/) by [JSR 376](https://www.jcp.org/en/jsr/detail?id=376) and [JEP 261](http://openjdk.java.net/jeps/261). DepVis produces a [GraphViz](www.graphviz.org) output file (DOT file)
which can be rendered with GraphViz in a separate step.

DepVis takes into account:
1. Requires/Read relationships (currently visualized as a blue arrow)
   * requires mandated to `java.base` (dashed blue arrow)
   * requires public (blue arrow)
2. Exports-To relationships (red)
3. Requires public transitivity (green arrow)
   * If `moda--requires-public-->modb` (blue) and `modc--requires-->moda` (blue), then also `modc--requires-->modb` (green). Note that this is currently limited to 1-transitivity.

Further hints:
1. DepVis can be configured, see section below.
2. A legend is written in the top left corner. As this unfortunately flips the whole graph, this is done as a separate graph.
3. A helper printer tool is also included which prints the relationship to STDOUT. 

### Setup
1. Clone this repo.
2. Install a Java 9 JDK with Jigsaw support. DepVis has been tested with b127 as of July 2016.
3. Install GraphViz 2.38.
4. Edit file `env.sh` to configure `JAVA_HOME` and `GRAPHVIZ_HOME` (see TODO markers)
5. Call `clean.sh`, `compile.sh` and `run-vis.sh` (or `all.sh` for all in one step).
6. Output is `moduledependencies.dot` and `moduledependencies.png` (and a second file pair `...with-legend`).

### Configuration
DepVis can be configured in a configuration properties file (see depvis.properties).
Configuration settings are:
1. `depvis.includeFilter`
   * comma-separated Strings
   * module names whitelist, _all_ used to be checked with `String.startsWith`
   * if not set, all modules from system and/or module-path will be used
   * example: `java.,jdk.`
2. `depvis.excludeFilter`   
   * comma-separated Strings
   * module names blacklist, _all_ used to be checked with `String.startsWith`
   * if not set, all modules from system and/or module-path will be used
   * example: `jdk.internal`
3. `depvis.useSystemModules`
   * boolean 
   * do we want to visualize modules from system (i.e. `java.*`, `jdk.*` etc.)?
4. `depvis.useModulePath`
    * boolean value
    * want to visualize modules from a module path
    * if so, `depvis.modulePath` needs to be set
5. `depvis.modulePath`
    * path Strings, separated by system's file separator) 
    * set a local module path
    * example: `/jigsaw/example/mlib`
6. `depvis.showRequires`
    * boolean 
    * want to visualize requires/reads relationships?
7. `depvis.showRequiresMandated`
   * boolean 
   * want to visualize requires/reads mandated relationships?
8. `depvis.showRequiresPublic`
   * boolean 
   * want to visualize requires/reads public relationships (1-transitive)?
9. `depvis.showExportsTo`
   * boolean 
   * want to visualize exports-to relationships?
10. `depvis.outputFileName`
    * String
    * filename for the DOT output file
    * example: `/tmp/moduledependencies.dot`
11. `depvis.showLegend`
    * boolean 
    * want to visualize a legend plus title and timestamp?
    * if so, the graph will be flipped to LR
12. `depvis.diagramTitle`
    * String
    * configure a title for the diagram
### TODOs
No software is ready, ever ;-) So here are some ideas left (any other feedback very welcome!):
1. Include n-transitivity for requires-public
2. Allow filtering of individual relationships (black/white listing)
3. Include uses/provides relationships
4. Include package names for modules
5. Include hash information of a module
6. Currently, DepVis shows modules from the Observable modules (module path and system modules). Allow to show modules from a Configuration.
7. Allow to configure colors, line styles etc. from outside (currently one needs to change Java class depvis.GraphVizHelper and recompile).

### Acknowledgments
Thx to the GraphViz team (http://www.graphviz.org) for this magic tool!

Thx also to [Kohsuke Kawaguchi](https://github.com/kohsuke) for his graphviz-api at https://github.com/kohsuke/graphviz-api!
We have forked his API to https://github.com/MartinLehmann1971/graphviz-api and made a few minor changes (mainly to avoid duplicates of GraphViz Nodes based on their ID).
