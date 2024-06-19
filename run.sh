#! /bin/bash

javac -d out/ src/main/*.java src/ray/*.java src/math/*.java
cd out
java main.Main
