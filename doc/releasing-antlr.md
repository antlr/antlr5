# Cutting an ANTLR Release

## Github

### Get dev merged into master

Do this or make a PR:

```bash
cd ~/antlr/code/antlr5
git checkout master
git merge dev
```

### Turn on DCO Enforcement

For ANTLR5 we will be using the Linux DCO. Contributors must use `-s` on each commit to the branch associated with a pull request.

See [GitHub App DCO](https://github.com/apps/dco).

Make sure this feature is turned on for the `antlr5` repo upon release.

### Delete existing release tag

Wack any existing tag as mvn will create one and it fails if already there.

```
$ git tag -d 1.0.0
$ git push origin :refs/tags/1.0.0
$ git push upstream :refs/tags/1.0.0
```

## Bump version in code and other files

There are a number of files that require inversion number be updated.


Here is a simple script to display any line from the critical files with, say, `1.1.0` in it.  Here's an example run of the script:

```bash
~/antlr/code/antlr5 $ python scripts/update_antlr_version.py 1.0 1.1.0
Updating ANTLR version from 1.0 to 1.1.0
Set ANTLR repo root (default ~/antlr/code/antlr5): 
Perform antlr5 `mvn clean` and wipe build dirs Y/N? (default no): 
Ok, not cleaning antlr5 dir
1.0 appears on 2 lines so _not_ updating /tmp/antlr5/runtime/JavaScript/package-lock.json
1.0 not in /tmp/antlr5/doc/releasing-antlr.md
```

Make sure this file doesn't have `-SNAPSHOT` when releasing!

```
runtime/Java/src/org/antlr/v5/runtime/RuntimeMetaData.java
```

It's also worth doing a quick check to see if you find any other references to a version:

```bash
mvn clean
find . -type f -exec grep -l '1\.2.0' {} \; | grep -v -E '\.o|\.a|\.jar|\.dylib|node_modules/|\.class|tests/|CHANGELOG|\.zip|\.gz|.iml|.svg'
```

Commit to repository.

## Build XPath parsers

This section addresses a [circular dependency regarding XPath](https://github.com/antlr/antlr4/issues/3600). In the java target I avoided a circular dependency (gen 4.13.1 parser for XPath using 4.13.1 which needs it to build) by hand building the parser: runtime/Java/src/org/antlr/v4/runtime/tree/xpath/XPath.java.  Probably we won't have to rerun this for the patch releases, just major ones that alter the ATN serialization.

```bash
cd ~/antlr/code/antlr5/runtime/Cpp/runtime/src/tree/xpath
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr5/4.13.1-SNAPSHOT/antlr5-4.13.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v5.Tool -Dlanguage=Cpp XPathLexer.g4

cd ~/antlr/code/antlr5/runtime/CSharp/src/Tree/Xpath
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr5/4.13.1-SNAPSHOT/antlr5-4.13.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v5.Tool -Dlanguage=CSharp XPathLexer.g4

cd ~/antlr/code/antlr5/runtime/Python3/tests/expr
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr5/4.13.1-SNAPSHOT/antlr5-4.13.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v5.Tool -Dlanguage=Python3 Expr.g4
cd ~/antlr/code/antlr5/runtime/Python3/src/antlr5/xpath
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr5/4.13.1-SNAPSHOT/antlr5-4.13.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v5.Tool -Dlanguage=Python3 XPathLexer.g4
```

## Maven Repository Settings

First, make sure you have maven set up to communicate with staging servers etc...  Create file `~/.m2/settings.xml` with appropriate username/password for staging server and gpg.keyname/passphrase for signing. Make sure it has strict visibility privileges to just you. On unix, it looks like:

```bash
beast:~/.m2 $ ls -l settings.xml 
-rw-------  1 parrt  staff  914 Jul 15 14:42 settings.xml
```

Here is the file template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  User-specific configuration for maven. Includes things that should not
  be distributed with the pom.xml file, such as developer identity, along with
  local settings, like proxy information.
-->
<settings>
   <servers>
        <server>
          <id>sonatype-nexus-staging</id>
          <username>sonatype-username</username>
          <password>XXX</password>
        </server>
        <server>
          <id>sonatype-nexus-snapshots</id>
          <username>sonatype-username</username>
          <password>XXX</password>
        </server>
   </servers>
    <profiles>
            <profile>
              <activation>
                    <activeByDefault>false</activeByDefault>
              </activation>
              <properties>
                    <gpg.keyname>UUU</gpg.keyname>
                    <gpg.passphrase>XXX</gpg.passphrase>
              </properties>
            </profile>
    </profiles>
</settings>
```

## Maven deploy snapshot

The goal is to get a snapshot, such as `1.0.0-SNAPSHOT`, to the staging server: [antlr5 tool](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr5/4.13.1-SNAPSHOT/) and [antlr5 java runtime](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr5-runtime/4.13.1-SNAPSHOT/).

Do this:

```bash
$ mvn install -DskipTests  # seems required to get the jar files visible to maven
$ mvn deploy -DskipTests
...
Uploading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr5-tool-testsuite/maven-metadata.xml
Uploaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr5-tool-testsuite/maven-metadata.xml (388 B at 0.9 KB/sec)
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] ANTLR 5 ............................................ SUCCESS [  4.073 s]
[INFO] ANTLR 5 Runtime .................................... SUCCESS [ 13.828 s]
[INFO] ANTLR 5 Tool ....................................... SUCCESS [ 14.032 s]
[INFO] ANTLR 5 Maven plugin ............................... SUCCESS [  6.547 s]
[INFO] ANTLR 5 Runtime Test Annotations ................... SUCCESS [  2.519 s]
[INFO] ANTLR 5 Runtime Test Processors .................... SUCCESS [  2.385 s]
[INFO] ANTLR 5 Runtime Tests (4th generation) ............. SUCCESS [ 15.276 s]
[INFO] ANTLR 5 Tool Tests ................................. SUCCESS [  2.233 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:01 min
[INFO] Finished at: 2016-12-11T09:37:54-08:00
[INFO] Final Memory: 44M/470M
[INFO] ------------------------------------------------------------------------
```

## Maven release

The maven deploy lifecycle phased deploys the artifacts and the poms for the ANTLR project to the [sonatype remote staging server](https://oss.sonatype.org/content/repositories/snapshots/).

```bash
mvn deploy -DskipTests
```

Make sure `gpg` is installed (`brew install gpg` on mac). Also must [create a key and publish it](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) then update `.m2/settings` to use that public key.

Then:

```bash
mvn release:prepare -Darguments="-DskipTests"
```

Hmm...per https://github.com/keybase/keybase-issues/issues/1712 we need this to make gpg work:

```bash
export GPG_TTY=$(tty)
```

You should see 0x37 in generated .class files after 0xCAFEBABE; see [Java SE 11 = 55 (0x37 hex)](https://en.wikipedia.org/wiki/Java_class_file):

```bash
~/antlr/code/antlr5 $ od -h tool/target/classes/org/antlr/v5/Tool.class |head -1
0000000      feca    beba    0000    3700    ed04    0207    0a9d    0100
                                     ^^
```

Also verify run time is 1.8:

```bash
od -h runtime/Java/target/classes/org/antlr/v5/runtime/Token.class | head -1
0000000      feca    beba    0000    3400    2500    0007    0722    2300
```

It will start out by asking you the version number:

```
...
What is the release version for "ANTLR 5"? (org.antlr:antlr5-master) 1.1.1: : 1.1.1
What is the release version for "ANTLR 5 Runtime"? (org.antlr:antlr5-runtime) 1.1.1: : 
What is the release version for "ANTLR 5 Tool"? (org.antlr:antlr5) 1.1.1: : 
What is the release version for "ANTLR 5 Maven plugin"? (org.antlr:antlr5-maven-plugin) 1.1.1: : 
What is the release version for "ANTLR 5 Runtime Test Generator"? (org.antlr:antlr5-runtime-testsuite) 1.1.1: : 
What is the release version for "ANTLR 5 Tool Tests"? (org.antlr:antlr5-tool-testsuite) 1.1.1: : 
What is SCM release tag or label for "ANTLR 5"? (org.antlr:antlr5-master) antlr5-master-1.1.1: : 1.1.1
What is the new development version for "ANTLR 5"? (org.antlr:antlr5-master) 1.1.2-SNAPSHOT:
...
```

Maven will go through your pom.xml files to update versions from 1.1.1-SNAPSHOT to 1.1.1 for release and then to 1.1.2-SNAPSHOT after release, which is done with:

```bash
mvn release:perform -Darguments="-DskipTests"
```

Maven will use git to push pom.xml changes.

Now, go here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/#welcome](https://oss.sonatype.org/#welcome)

and on the left click "Staging Repositories". You click the staging repo and close it, then you refresh, click it and release it. It's done when you see it here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr5-runtime/1.1.1/antlr5-runtime-1.1.1.jar](https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr5-runtime/4.13.1/antlr5-runtime-4.13.1.jar)

All releases should be here: [https://repo1.maven.org/maven2/org/antlr/antlr5-runtime](https://repo1.maven.org/maven2/org/antlr/antlr5-runtime).

## Deploying Targets

### JavaScript

**Push to npm**

(I think this has to be run before the unit test can run locally as it installs the global lib)

```bash
cd ~/antlr/code/antlr5/runtime/JavaScript
rm -rf node_modules # seems we might need this later but try it here
npm update
npm install
npm run build 
npm login     # asks for username/password/2FA (npmjs.com)
npm publish   # don't put antlr5 on there or it will try to push the old version for some reason
```

Move (and zip) target to website:

```bash
cd src
zip -r ~/antlr/sites/website-antlr5/download/antlr-javascript-runtime-4.13.1.zip .
```

## Update website

### javadoc for runtime and tool

Jars are in:

```
~/.m2/repository/org/antlr/antlr5-runtime/1.1.1/antlr5-runtime-1.1.1
```

### Update version and copy jars / api

Copy javadoc and java jars to website using this script:

```bash
cd ~/antlr/code/antlr5
python scripts/deploy_to_website.py 1.1.0 1.1.1
```

Output:

```bash
Updating ANTLR version from 1.1.0 to 1.1.1
Set ANTLR website root (default /Users/parrt/antlr/sites/website-antlr5): 
Version string updated. Please commit/push:
Javadoc copied:
	api/Java updated from antlr5-runtime-1.1.1-javadoc.jar
	api/JavaTool updated from antlr5-1.1.1-javadoc.jar
Jars copied:
	antlr-1.1.1-complete.jar
	antlr-runtime-1.1.1.jar

Please look for and add new api files!!
Then MANUALLY commit/push:

git commit -a -m 'Update website, javadoc, jars to 1.1.1'
git push origin gh-pages
```

<!--
```bash
cp ~/.m2/repository/org/antlr/antlr5-runtime/1.1.1/antlr5-runtime-1.1.1.jar ~/antlr/sites/website-antlr5/download/antlr-runtime-1.1.1.jar
cp ~/.m2/repository/org/antlr/antlr5/1.1.1/antlr5-1.1.1-complete.jar ~/antlr/sites/website-antlr5/download/antlr-1.1.1-complete.jar
cd ~/antlr/sites/website-antlr5/download
git add antlr-1.1.1-complete.jar
git add antlr-runtime-1.1.1.jar 
```
-->

Once it's done, you must do the following manually:

```
cd ~/antlr/sites/website-antlr5
git commit -a -m 'Update website, javadoc, jars to 1.1.1'
git push origin gh-pages
```

<!--
Then copy to website:

```bash
cd ~/antlr/sites/website-antlr5/api
git checkout gh-pages
git pull origin gh-pages
cd Java
jar xvf ~/.m2/repository/org/antlr/antlr5-runtime/1.1.1/antlr5-runtime-1.1.1-javadoc.jar
cd ../JavaTool
jar xvf ~/.m2/repository/org/antlr/antlr5/1.1.1/antlr5-1.1.1-javadoc.jar
git commit -a -m 'freshen api doc'
git push origin gh-pages
```
-->

## Get fresh dev branch

```bash
git checkout master
git pull upstream master
git checkout dev
git pull upstream dev
git merge master
git push origin dev
git push upstream dev
```

## Other updates 

* Rebuild antlr Intellij plug-in with new antlr jar.
* Cut release notes in github
* Update lab.antlr.org
