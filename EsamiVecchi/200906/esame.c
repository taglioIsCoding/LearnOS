#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeRead, int pipeWrite, char charToCheck);
void doChild2(int pipeRead, char charToCheck, int secToExit);
void handlerAlarm(int signum);
void handlerChild(int signum);
int figli_terminati = 0;


int main(int argc, char *argv[]){
    
    int fdIn;
    int N;
    char c1;
    char c2;
    int pipeFd1[2], pipeFd2[2];

    signal(SIGCHLD, handlerChild);

    if(argc != 5){
        printf("Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/' ){
        printf("Params Error, file not readable\n");
        return(0);
    }

    N = atoi(argv[4]);
    if(N <= 0){
        printf("Params Error, N non corretto\n");
        return(0);
    }

    c1 = *argv[2];
    if(strlen(argv[2]) != 1){
         printf("Params Error, c1 non corretto\n");
         return(0);
    }
    c2 = *argv[3];
    if(strlen(argv[3]) != 1){
         printf("Params Error, c2 non corretto\n");
         return(0);
    }


    //1 scrittura, 0 lettura
    pipe(pipeFd1);
    pipe(pipeFd2);
    int pid1=fork();
    if(pid1==0){
        close(pipeFd1[1]);
        close(pipeFd2[0]);
        doChild1(pipeFd1[0], pipeFd2[1], c1);
    }

    close(pipeFd1[0]);

    int pid2=fork();
    if(pid2==0){
        close(pipeFd2[1]);
        close(pipeFd1[1]);
        doChild2(pipeFd2[0], c2, N);
    }

    close(pipeFd2[0]);
    close(pipeFd2[1]);

    char letter;
    int readStatus;
    while((readStatus = read(fdIn, &letter, 1)) > 0){
        write(pipeFd1[1], &letter, 1);
    }

    close(fdIn);
    close(pipeFd1[1]);

    while(figli_terminati != 2){
        pause();
    }
}

void doChild1(int pipeRead, int pipeWrite, char charToCheck){
    char letter;
    int readStatus;
    while((readStatus = read(pipeRead, &letter, 1)) > 0){
        if(letter != charToCheck){
            write(pipeWrite, &letter, 1);
        }
    }

    close(pipeRead);
    close(pipeWrite);
    printf("Finisce 1\n");
    exit(0);
}

void doChild2(int pipeRead, char charToCheck, int secToExit){
    signal(SIGALRM, handlerAlarm);
    alarm(secToExit);
    char letter;
    int readStatus;
    while((readStatus = read(pipeRead, &letter, 1)) > 0){
        if(letter != charToCheck){
           printf("%c", letter);
        }
    }

    printf("\nFinisce 2\n");
    close(pipeRead);
    exit(EXIT_SUCCESS);
}

void handlerAlarm(int signum){
    printf("\nFinisce 22\n");
    exit(EXIT_SUCCESS);
}

void handlerChild(int signum){
    figli_terminati++;
    int status;
    int childPid = wait(&status);
    printf("P0 (%d): ricevuto sigchld  da %d con staus %d - figli terminati = %d.\n",getpid(),childPid,status,figli_terminati);
    if (figli_terminati==2)
    {   printf("P0 (%d): termino.\n",getpid());    
        exit(0);
    }
}