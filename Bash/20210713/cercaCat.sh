#!/bin/bash

if [ $# -lt 4 ]; then echo Sintax! ; exit; fi

if ! [[ -f "$1" && "$1" = /*.txt && -r "$1" ]] ; then
    echo -e "Il file $1 non è un file o il file non è nel formato corretto" >&2
    exit 1
fi

if ! [[ -e "$2" && -f "$2" && -w "$2" ]] ; then
    echo -e "Il file $2 non è un file o non hai permessi" >&2
    exit 1
fi

if ! [[ ! -e "$3" || -e "$3" && -w "$3" ]] ; then
    echo -e "Il file $3 non hai permessi" >&2
    exit 1
fi

ORIGINE=$1;
LOG=$2;
ERR=$3;

shift; 
shift;
shift;

for file in $*;
do
    if ! [[ -f "$file" && "$file" = /* && -r "$file" ]] ; then
        echo -e "Il file $file non è un file o il file non è nel formato corretto" >&2
        exit 1
    fi
done

> $ERR

for file in $@;
do
   for line in `cat $file`;
    do
        if ! [[ -d $line ]] ; then
            echo -e "La cartella $line non appartiene a PWD" >> $ERR;
        elif ! [[ -w $line && -r $line ]] ; then
            echo -e "La cartella $line non ha i permessi necessari" >> $ERR;
        else
        
        cp $ORIGINE $line;
        ls $line >> $LOG

        fi
        #echo $line
    done
done