#! /bin/bash

javac -d out/ src/main/*.java
cd out
java main.Main
