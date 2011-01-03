keywords : **builder, build system, reactive build, chain reaction, watch**

# Goals/Todo

* Level 1
  * automatic rebuild of generated files when sources files are changed (saved from editor,...)
  * rebuild minimum files
  * allow to run builder wrote for JVM, ruby, javascript, python, shell, native,...
  * provide an integrable lib (usable from a standalone cli tools, from IDE like eclipse, from editor like jEdit, from full build system like maven, ...)
  * human comprehensible/reproductible sequence of builds
* Level 2
  * provide a plugins system to allow auto-install/download of builders/preset (and dependencies)
  * provide preset of builders, configurations (following various convention, maven, nanoc, couchapp, ...)
* Level 5
  * provide plugins for :
    * 'simple' compilers
      * coffeescript -> javascript
      * javascript -> javascript ( closure compiler, yui-compressor, jsLint)
      * scss -> css
      * haml -> html
    * framework :
      * scalate
      * compass
      * couchapp
* Level 4
  * being able to replace/work with web site builder like nanoc3
  * being able to replace/work with build system like maven, sbt
  * provide a TCP or HTTP API for integration into non JVM tools, other than by using stream (in, out, err)
  * provide other strategy for runs
    * strategy like, parallele run, non failed on first builder failed (if next builder aren't impacted by the failure)
    * will be done by replacing sequence of builder by a graph (done by human,r automaticatly from the sequence and analyze of a dry run, where builder are able to say "if you give this I'll give you that")
  
# Concepts

* define single line pipeline : builder chain
* define per builder the mask (on files), if masked delta is empty, then builder is not asked
* during run/traverse
  * updates in target scope are added in th full delta
  * ? if source trigger is modified during run/traverse then run is aborted, and restarted with updated delta (from aborted + new one)
* plugin system
  * for builders
  * for preconfiguration
* a way to customise preconfiguration
* a way to mixe preconfiguration
* startup/single run, send the full file liste as delta, like if clean before run
  * possibility to use a timestamp file, (or fingerprint  ?)
  
# Motivations

In lot of projects, I like to have "incremental/continous/watch" compilation.
From my search (correct me if I miss something), I found no composable builder, only solution specific for one build system/compiler or for one framework

* in scala land, `mvn scala:cc` or `sbt ~compile`
* in ruby/javascript land, every frameworks/stack provide its own

So if you want to combine technology that are not part of a stack, you have to create your own :-(.

It's why I decide to start my own builder that first target my current app (coffeescript + compass + haml/template + couchapp)

## Inspirations

* mvn scala:cc, sbt ~compile 
* nanoc3 and autocompile,
* coffescript --watch
* eclipse's Builder

## Links

* application/lib builder
  * Java/jvm oriented builders : [Ant](http://ant.apache.org), [Maven](http://maven.apache.org), [Buildr](http://buildr.apache.org/), [Gradle](http://gradle.org), [SBT](http://code.google.com/p/simple-build-tool/)
  * C/C++ oriented builders : [GNU Make](http://www.gnu.org/software/make/make.html), [Scons](http://www.scons.org/)
  * Ruby oriented builder : [Rake](http://rake.rubyforge.org/)
  * [Coadjute](http://hackage.haskell.org/packages/archive/Coadjute/0.1.0/doc/html/Coadjute.html) a generic builder in haskell
  * [Ocamlbuild](http://brion.inria.fr/gallium/index.php/Ocamlbuild) a builder in OCaml
* articles
  * [FromMakeToScons](http://www.scons.org/wiki/FromMakeToScons)

# TODO

