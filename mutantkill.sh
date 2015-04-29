#!/bin/bash

BASEFILE="surefire-api/src/main/java/org/apache/maven/surefire/util/DefaultRunOrderCalculator.java"
BACKUP=$BASEFILE.old
for i in 1 2 3 4 5; do
    MUTANT=$BASEFILE.Mutant$i
    mv $BASEFILE $BACKUP
    mv $MUTANT $BASEFILE
    mvn -Dit.test=RunOrderIT -DfailIfNoTests=false -Dtest=NONE verify
    mv $BASEFILE $MUTANT
    mv $BACKUP $BASEFILE
done
