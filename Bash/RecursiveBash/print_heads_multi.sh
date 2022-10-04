#!/bin/bash
if ! [ $# -gt 2 ]; then echo Sintax! ; exit; fi
if ! [[ "$1" =~ ^[0-9]+$ ]]; then echo Need an int! ; exit; fi 


if ! [[ -d "$2" ]] ; then
    echo -e "Il file $2 non è una directory" >&2
    exit 1
fi 

if [[ "$0" = /* ]] ; then
    #Iniziando con /, si tratta di un path assoluto
    #(eg /home/andrea/recurse_dir.sh)

    #Estrazione di parti di path: man dirname oppure man basename
    dir_name=`dirname "$0"`
    recursive_command="$dir_name/do_print_heads_multi.sh"
    #echo qui
elif [[ "$0" = */* ]] ; then
    # C'è uno slash nel comando, ma non inizia con /. Path relativo
    dir_name=`dirname "$0"`
    recursive_command="`pwd`/$dir_name/do_print_heads_multi.sh"
    #echo qui2
else 
    # Non si tratta ne di un path relativo, ne di uno assoluto.
    # E' un path "secco": il comando sarà dunque cercato
    # nelle cartelle indicate dalla variabile d'ambiente $PATH.
    recursive_command=do_print_heads_multi.sh
    #echo qui3
fi

size=$1
shift
#Invoco il comando ricorsivo
for dir
do
    "$recursive_command" "$dir" "$size" 
done
