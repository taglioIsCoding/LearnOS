#!/bin/bash

if [ $# != 3 ]; then echo Sintax! ; exit; fi
if ! [[ -d "$1" && "$1" = /* ]] ; then
    echo -e "Il file $1 non è una directory o la sintassi è errata" >&2
    exit 1
fi

if ! [[ -d "$3" && "$3" = /* ]] ; then
    echo -e "Il file $3 non è una directory o la sintassi è errata" >&2
    exit 1
fi

if [[ "$0" = /* ]] ; then
    #Iniziando con /, si tratta di un path assoluto
    #(eg /home/andrea/recurse_dir.sh)

    #Estrazione di parti di path: man dirname oppure man basename
    dir_name=`dirname "$0"`
    recursive_command="$dir_name/do_copy.sh"
    #echo qui
elif [[ "$0" = */* ]] ; then
    # C'è uno slash nel comando, ma non inizia con /. Path relativo
    dir_name=`dirname "$0"`
    recursive_command="`pwd`/$dir_name/do_copy.sh"
    #echo qui2
else 
    # Non si tratta ne di un path relativo, ne di uno assoluto.
    # E' un path "secco": il comando sarà dunque cercato
    # nelle cartelle indicate dalla variabile d'ambiente $PATH.
    recursive_command=do_copy.sh
    #echo qui3
fi

cd $1

#Invoco il comando ricorsivo
for dir in *
do
    "$recursive_command" "$dir" "$2" "$3"
done