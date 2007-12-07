find . -name CVS -prune -o -name .svn -prune -o -type f -print -exec diff -u $1/{} $2/{} \;
