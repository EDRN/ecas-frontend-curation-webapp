#!/bin/sh


echo "This script will freeze ecas into a tarball"
echo "the data from /data/ingest/test_site"
echo "the tar ball can be retrieved from "
echo "  /usr/local/ecas.archive/live-freeze"

( cd /usr/local/ecas/filemgr/bin ; sudo ./filemgr stop )
tar cfz /usr/local/ecas.archive/live-freeze/ecas.tar.gz \
	/usr/local/ecas/aux/met_def/ecas_curator/ \
	/usr/local/ecas/filemgr/policy \
	/usr/local/ecas/filemgr/catalog 

# uncomment to freeze additional data
#tar cfz /usr/local/ecas.archive/live-freeze/test_site.tar.gz \
# 	/data/ingest/test_site*

( cd /usr/local/ecas/filemgr/bin ; sudo ./filemgr start )
