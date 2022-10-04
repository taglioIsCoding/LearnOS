#include<stdio.h>
#include<unistd.h>
#include<stdlib.h>
#include<string.h>

int childCopyOrRemove(char *fileName,char *source ,char *destination);

int main(int argc, char *argv[]){
    
    if(argc < 3){
        printf("Numero di parametri insufficienti\n");
        return(0);
    }

    for(int i = 3; i < argc; i++){
        int pid = fork();
        if(pid == 0){
            childCopyOrRemove(argv[i], argv[1], argv[2]);
        }
    }

    for(int i = 3; i < argc; i++){
        if(getpid() != 0){
            int status;
            int endedPid = wait(&status);
            if(status != 0){
                printf("Pid: %d del processo fallito", endedPid);
                exit(1);
            }
        }
    }

    chdir(argv[2]);
    execl("/bin/ls", "-l", (char *)0);
    exit(0);
}

int childCopyOrRemove(char *fileName, char *source ,char *destination){
    printf("Pid: %d file: %s %s\n", getpid(), fileName, destination);
    chdir(source);
    if(getpid() % 2 == 0){
        execl("/bin/cp", "cp", fileName, destination,(char *)0);
        printf("Error in cp\n");
        exit(1);
    }else{
        execl("/bin/rm", "rm", fileName, (char *)0);
        printf("Error in remove\n");
        exit(2);
    }
    exit(0);
}

/*
    gcc CopyRemove.c -o ese22
    ./ese22 /Users/micheletagliani/Developer/C/OS/CopyRemove/From /Users/micheletagliani/Developer/C/OS/CopyRemove/To text1 text2 text3 text4
*/