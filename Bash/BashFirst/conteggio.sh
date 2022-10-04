#!/bin/bash
if ! [ $# -eq 3 ]; then echo Sintax! ; exit; fi
for dir in `cat $3`;
do
    fileInfo=`ls -l $dir | grep $1 | awk '{ print $3,$5 }'`
    if [[ $fileInfo != '' ]];
    then
        #echo "Il file $1 dell’utente $2 nella directory $dir contiene `cut $fileInfo -d' ' -f2` caratteri";
        dim=$(echo $fileInfo | awk '{ print $2 }');
        echo "Il file $1 dell’utente $2 nella directory $dir contiene $dim caratteri";

    fi
done

# ls -l /Users/micheletagliani/Developer/C/OS/BashFirst/3 | grep pippo.txt | awk -F3 | grep micheletagliani
# find /Users/micheletagliani/Developer/C/OS/BashFirst/3 -user micheletagliani -name pippo.txt -ls