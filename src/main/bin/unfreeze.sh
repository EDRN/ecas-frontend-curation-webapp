#!/bin/sh


echo "This script will unfreeze ecas from two tarballs"
echo "from /usr/local/ecas.archive/live-freeze"

( cd /usr/local/ecas/filemgr/bin ; sudo ./filemgr stop )
( cd ; rm -rf \
        /usr/local/ecas/aux/met_def/ecas_curator/ \
        /usr/local/ecas/filemgr/policy \
        /usr/local/ecas/filemgr/catalog )

( cd / ; tar xfz /usr/local/ecas.archive/live-freeze/ecas.tar.gz )
( cd / ; tar xfz /usr/local/ecas.archive/live-freeze/test_site.tar.gz )

( cd /usr/local/ecas/filemgr/bin ; sudo ./filemgr start )
