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

X=0
Y=0

if test -d "$1"; then
    cd "$1"
    
    # for file in * ;
    # do
    #     if [[ -f "$file" ]]; then
    #         X=`expr $X + `ls| grep "$file" | cut -d' ' -f4 | grep $USER | wc -l``
    #         Y=`expr $Y + `ls -l | grep "$file" | grep -e *$3 | wc -l``
    #     fi
    #     echo $X
    #     echo $Y
    # done

    X=`ls -l | grep '^-' | cut -d' ' -f4 | grep $USER | wc -l`
    Y=`ls -l | grep '^-' | ls *$3 2>/dev/null| wc -l`
    if [[ `expr $X + $Y` -gt $2 ]]; then
        echo "Cartella:$1 X=$X Y=$Y" >> $4/report.txt
    fi
    
    
    for file in * ;
    do
       "$0" "$file" "$2" "$3" "$4"
    done
    
fi
