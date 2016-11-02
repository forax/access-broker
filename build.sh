#!/bin/bash
export JAVA_HOME=/usr/jdk/jdk-9-jigsaw
export java=$JAVA_HOME/bin/java
export javac=$JAVA_HOME/bin/javac

rm -fr output
mkdir output
$javac -d output/classes \
       -Xmodule:java.base \
       $(find src/java.base/ -name "*.java")

$javac -d output/modules/javax.persistence \
       $(find src/javax.persistence/ -name "*.java")

$javac -d output/modules/org.hibernate.jpa \
       --patch-module java.base=output/classes \
       --module-path output/modules \
       $(find src/org.hibernate.jpa/ -name "*.java")

$javac -d output/modules/my.jpa.app \
       --patch-module java.base=output/classes \
       --module-path output/modules \
       $(find src/my.jpa.app/ -name "*.java")

$java --module-path output/modules \
      --patch-module java.base=output/classes \
      -m my.jpa.app/my.jpa.app.Bean


       



