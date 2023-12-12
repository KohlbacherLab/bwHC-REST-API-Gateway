#!/bin/bash


TARGET_DIR=""


if [ "$#" -eq "0" ]; then
  echo "ERROR: Enter a target directory for bwHC backend installation"
  exit 1
else 
  TARGET_DIR="$1"
fi

if [ ! -d "$TARGET_DIR" ]; then
  mkdir -p "$TARGET_DIR"
fi


BWHC_APP_PREFIX="PREFIX_PLACEHOLDER"
BWHC_APP_DIR="BWHCAPPPLACEHOLDER" 


BWHC_ZIP="$BWHC_APP_DIR.zip"

FILES=(
  "config"
  "production.conf"
#  "bwhc-backend-service"
  "bwhcConnectorConfig.xml"
  "logback.xml"
)

cp $BWHC_ZIP "$TARGET_DIR/"

for FILE in "${FILES[@]}"; do
  if [ ! -f "$TARGET_DIR/$FILE" ]; then
    echo "Copying $FILE to $TARGET_DIR..."
    cp $FILE "$TARGET_DIR/"
  fi
done

cp bwhc-backend-service "$TARGET_DIR/"


cd "$TARGET_DIR"

if [ -d "$BWHC_APP_DIR" ]; then
  echo "Replacing previous bwHC backend app installation"
  rm -r $BWHC_APP_DIR
fi


unzip -q $BWHC_ZIP


echo "Done!"

