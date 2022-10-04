#!/bin/bash

childWork(){
    top > /Users/micheletagliani/Developer/C/OS/EserciziBash/topSearch/processi.out
}

if [ $# -lt 3 ]; then echo Sintax! ; exit; fi
if ! [[ "$2" =~ ^[0-9]+$ && "$2" -gt 0 ]]; then echo Need an int! ; exit; fi

string=$1
number=$2

shift
shift

for dir;
    do
        if ! [ -d $dir ] || ! [ $dir != "/*" ]; then echo 'Dir not a dir in the dir list' ; exit; fi
    done

childWork &



