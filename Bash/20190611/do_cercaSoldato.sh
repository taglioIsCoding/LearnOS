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
    cognome=`grep 123 $1 | cut -d';' -f3`;
    if [ "$cognome" != "" ]; then
        mesi=`grep 123 $1 | cut -d';' -f4`;
        dir=`pwd | rev | cut -d'/' -f1 | rev`;
        echo $mesi;
        echo $dir ;
        echo "Il soldato $cognome ($2) ha dedicato $mesi mesi alla missione $dir" >> /Users/micheletagliani/Developer/C/OS/EserciziBash/20190611/report.txt
    fi
    exit; 
else
    cd "$1"
    for file in * ;
    do
       "$0" "$file" "$2"
    done 
fi