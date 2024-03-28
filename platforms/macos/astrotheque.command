#!/bin/zsh

cd "$(dirname "$0")"

export DB_FILE=core/dbs/astrotheque.db
export HTML_DIST=./core/dist
./core/Contents/Home/bin/java -jar astrotheque.jar