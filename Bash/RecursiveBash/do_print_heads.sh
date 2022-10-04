#echo $2
if [[ "$1" = "*" ]] ; then
    #Evito cartelle vuote
    exit 1
fi 

if ! [[ -e "$1" ]] ; then
    echo -e "Il file $1 non esiste" >&2
    # le forme >&2 oppure 1>&2 sono equivalenti e indicano alla shell di redirigere quanto stampato su standard output sullo standard error.
    #  > serve per redirigere
    #  1 indica lo stdout (e può essere sottinteso)
    #  & indica che quel che segue deve essere considerato come un file descriptor (non come il semplice nome di un file)
    #  2 è il file descriptor dello sterr
    exit 1
fi 

constBites=1024

if ! test -d "$1"; then
    #echo "`pwd`/$1"
    fileDir="`pwd`/$1"
    #Linux
    #numBytes="`stat --print="%s" $fileDir`"
    #MacOS
    numBytes="`stat -f %z $fileDir`"

    numBytes=`expr $numBytes / $constBites `;
    #echo $numBytes
    if [ "$numBytes" -gt "$2" ];then
        echo "`pwd`/$1"
        echo `head -c 10 $fileDir`
    fi
else
    cd "$1"
    for file in * ; do
       "$0" "$file" "$2"
    done 
fi
