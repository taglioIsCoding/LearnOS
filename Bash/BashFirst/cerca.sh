#!/bin/bash
if [ $# -lt 3 ]; then echo Sintax! ; exit; fi
S=$1
shift
M=$1
#TODO check if is int
shift
total=0
for dir;
    do
        if ! [ -d $dir ] || ! [ $dir != "/*" ]; then echo 'Dir not a dir in the dir list' ; exit; fi
        for file in $dir/*
        do
            numOccurency=`grep -s -o $S $file | wc -l`
            if [ $numOccurency == $M ]; 
            then 
                echo $file
                total=`expr $total + 1`
            fi
        done
    done
echo $total