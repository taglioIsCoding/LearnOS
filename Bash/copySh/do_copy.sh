if [[ "$1" = "*" ]] ; then
    #Evito cartelle vuote
    exit 1
fi
#echo "$1" "$2" "$3" 

if ! [[ -e "$1" ]] ; then
    echo -e "Il file $1 non esiste" >&2
    # le forme >&2 oppure 1>&2 sono equivalenti e indicano alla shell di redirigere quanto stampato su standard output sullo standard error.
    #  > serve per redirigere
    #  1 indica lo stdout (e può essere sottinteso)
    #  & indica che quel che segue deve essere considerato come un file descriptor (non come il semplice nome di un file)
    #  2 è il file descriptor dello sterr
    exit 1
fi


if ! test -d "$1"; then
    #echo "$1 è un file!"
    numOfWords=`head -n 10 $1 | grep $2 | wc -l`
    echo "$numOfWords"
    if [[ $numOfWords -gt 0 ]]; then
        mkdir -p "$3/$numOfWords/"
        cp "$1" "$3/$numOfWords/"
    fi
    exit; 
else
    cd "$1"
    for file in * ;
    do
       "$0" "$file" "$2" "$3"
    done 
fi