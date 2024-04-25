#!/bin/zsh

printf 'Nouvelle version ? '
read newVersion

./mvnw versions:set -DnewVersion=$newVersion-SNAPSHOT
echo "✅ Upgrade pom.xml"

cat package.json | jq '.version = $newVer' --arg newVer $newVersion > new-package.json
rm package.json
mv new-package.json package.json
echo "✅ Upgrade package.json"