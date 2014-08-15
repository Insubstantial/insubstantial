To Whom It May Concern:

Insubstantial is currently not releasing any more builds and is [actively looking for a new maintainer](http://speling.shemnon.com/blog/2013/06/08/insubstantial-needs-a-new-maintainer/).  It could be you!


---

This is a fork of several of Kirill Grouchnikov's Java Swing libraries.

BuildHive status:
[![Build Status](https://buildhive.cloudbees.com/job/Insubstantial/job/insubstantial/badge/icon)](https://buildhive.cloudbees.com/job/Insubstantial/job/insubstantial/)

##To perform a full release

###Prerequisites
You need to have your GPG keys set up and your sonatype password handy.  To set up your GPG key set this in
your `~/.gradle/gradle.properties (you will need to replace ABCDEF12 and password with your key ID and password).

    signing.keyId=ABCDEF12
    signing.password=password
    signing.secretKeyRingFile=~/.gnupg/secring.gpg

If you are alergic to storing passwords on the file system, the passwords can be passed into the command line
of your gradle invocation via the `-Dsigning.password=password` flag.  If you have no such allergies you can also store your sonatype
credentails

    sonatypeUsername=username
    sonatypePassword=password

###The Release Build

1. Update the version in the root build.gradle file to remove the SNAPSHOT.  There should be two locations.
2. Commit the change to git
3. perform the build:
   gradle clean githubDist uploadPublished
4. Perform smoke testing so you are sure you are happy with the bits.
5. Upload jars to the github site
6. Go to oss.sonatype.org and close and release the repo
7. Bump the version numbers in the root build.gradle, being sure to add SNAPSHOT
8. Commit the change to git
9. Push changes to github
10. After the jars appear in the central repo, post release notes
