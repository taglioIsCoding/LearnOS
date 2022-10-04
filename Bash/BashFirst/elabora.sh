#!/bin/bash
read path
if [ $# -ne 1 ] || ! [ -f $path  ]; then echo Sintax! ; exit; fi
if [ $path != "/*" ]; then echo Not absolute! ; exit; fi
if ! [ -r $path ]; then echo NotReadable! ; exit; fi
grep $1 $path | sort -r > $HOME/results_$USER.out
exit;