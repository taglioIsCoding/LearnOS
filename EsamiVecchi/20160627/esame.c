#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeWrite, char C);
void doChild2(int fdIn, int n);
void handlerUSR1(int signum);
int pid1;

int main(int argc, char *argv[]){
    
    int fdIn;
    int n;
    char C;
    int pipeFd[2];

    if(argc != 4){
        fprintf(stderr,"Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/' ){
        printf("Params Error, file not readable\n");
        return(0);
    }

    n = atoi(argv[2]);
    if(n <= 0){
        printf("Params Error, N non corretto\n");
        return(0);
    }

    C = *argv[3];
    if(strlen(argv[3]) != 1){
         printf("Params Error, C non corretto\n");
         return(0);
    }


    //1 scrittura, 0 lettura
    pipe(pipeFd);
    pid1 = fork();
    if(pid1==0){
        close(pipeFd[0]);
        signal(SIGUSR1, handlerUSR1);
        doChild1(pipeFd[1], C);
    }

    int pid2=fork();
    if(pid2==0){
        close(pipeFd[0]);
        close(pipeFd[1]);
        doChild2(fdIn, n);
    }

    close(pipeFd[1]);
    
    char letter;
    int readStatus;
    while((readStatus = read(pipeFd[0], &letter, 1)) > 0){
        fprintf(stdout, "%c", letter);
    }

    printf("Ho finito \n");
    exit(0);

}


void handlerUSR1(int signum){

}

void doChild1(int pipeWrite, char C){
    fprintf(stdout, "child 1 \n");
    pause();
    
    int fdTemp;
    if((fdTemp = open("/Users/micheletagliani/Developer/C/OS/EsamiVecchi/20160627/temp.txt", O_RDONLY )) < 0){
        fprintf(stderr, "file temp non aperto \n");
    }
    
    close(0);
    dup(fdTemp);
    close(fdTemp);
    close(1);
    dup(pipeWrite);
    close(pipeWrite);

    char charString[2];
    sprintf(charString, "%c" , C);

    execl("/usr/bin/grep", "/usr/bin/grep", charString , (char *)0);
    perror("exec non funziona\n");
    exit(1);
}

void doChild2(int fdIn, int n){
    fprintf(stdout, "child 2 \n");

    int fdTemp;
    if((fdTemp = open("/Users/micheletagliani/Developer/C/OS/EsamiVecchi/20160627/temp.txt", O_TRUNC | O_CREAT | O_WRONLY , 0777)) < 0){
        fprintf(stderr, "file temp non creato \n");
    }

    char letter;
    int readStatus;
    int volteSpazio = 0;
    while((readStatus = read(fdIn, &letter, 1)) > 0){
        if(letter == '\n'){
            volteSpazio++;
        } 

        if(volteSpazio % n == 0){
            write(fdTemp, &letter, 1);
        }
    }

    kill(pid1, SIGUSR1);
    exit(0);
}