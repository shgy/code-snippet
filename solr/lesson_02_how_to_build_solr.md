According to LUCENE-4288, Solr will only package properly if it's checked out from SVN by default. However, if you change package-src-tgz to package-local-src-tgz, it will properly package. Find the following lines in solr/build.xml:

<!-- make a distribution -->
<target name="package" depends="package-src-tgz,..."/>
And change package-src-tgz to package-local-src-tgz.

<!-- make a distribution -->
<target name="package" depends="package-local-src-tgz,..."/>
Then just rerun ant package inside solr/, and the packaged archives (solr-<version>-SNAPSHOT.tgz and solr-<version>-SNAPSHOT.zip) will be available under solr/package/
