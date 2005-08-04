Requirements
==========================================
You MUST have the following:

 - A Java.net Glassfish membership (for EL API/Impl)
   https://glassfish.dev.java.net/

 - Nightly Snapshot of JSF RI (Version 1.2 recommended)
   https://javaserverfaces.dev.java.net

 - OR, Current release of MyFaces
   http://myfaces.apache.org


Legal
==========================================
Facelets is allowed to release versions of the JavaServer Faces RI and API
in binary format, under the CDDL License Agreement.

For licensing questions and available source code, go to:
https://javaserverfaces.dev.java.net


Building Facelets
==========================================

1.  Copy 2 jars into /lib of this project:

  - servlet-api.jar (a jar containing servlet api's)

  - jsf-api.jar (version 1.2, see requirements)


2.  Now that you have the lib's available, modify the build.properties

  - Set 'java.net.cvs.user' to your Java.net user name

3.  Run Ant from project root

  - Type 'ant'

  - This will fetch and build the EL dependencies from Glassfish


FAQ
==========================================

1.  The Ant build 'depend-XXX' is failing with CVS, "anonymous: no such user"

    Facelets requires the EL-API and the EL-RI to be built from Glassfish.
    Glassfish requires you to agree with their licensing while you are logged
    in under your user name on Java.net.  Once you have agreed to the Glassfish
    license, then you must modify the build.properties 'java.net.cvs.user' to
    the user name you 'joined' Glassfish with.

    Once you've modified the build.properties, Ant will use that name to checkout
    the required sources from Glassfish to build the supporting EL libraries and
    place them in /lib.


2.  "I believe I've found a bug!"

    First off, thanks!  Please use Facelet's Issue tracker on Java.net to submit
    the bug you've found.  Even better yet, the source is freely available and you
    can submit a patch if you have the time.

    https://facelets.dev.java.net/servlets/ProjectIssues


3.  I'm getting ClassNotFound Exceptions for javax.faces.* when I run Ant

    Make sure that you have the JSF 1.2 API in your lib folder when compiling.
    If you do not have JSF 1.2 available, either download the binary version of
    Facelets, or download the binaries of the JSF RI (see requirements).

    Once compiled, you can use any JSF 1.1 or 1.2 implementation and API with
    your project.

