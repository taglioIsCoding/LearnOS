#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeWrite, int fdIn);
void doChild2(int pipeRead, int lineToRead, int limit,  int ppid);
void handlerUSR1(int signum);
void handlerUSR2(int signum);
int N;
int A;

int main(int argc, char *argv[]){
    
    int fdIn;
    int pipeFd[2];

    if(argc != 4){
        fprintf(stderr,"Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/' ){
        fprintf(stderr,"Params Error, file not readable\n");
        return(0);
    }

   N = atoi(argv[2]);
    if(N <= 0){
       fprintf(stderr,"Params Error, N non corretto\n");
        return(0);
    }

    A = atoi(argv[3]);
    if(A <= 0){
        fprintf(stderr,"Params Error, A non corretto\n");
        return(0);
    }

    signal(SIGUSR1,handlerUSR1);
    signal(SIGUSR2,handlerUSR2);

    //1 scrittura, 0 lettura
    pipe(pipeFd);
    int pid1 = fork();
    if(pid1==0){
        close(pipeFd[0]);
        doChild1(pipeFd[1], fdIn);
    }

    close(pipeFd[1]);

    int fatherPid = getpid();
    int pid2=fork();
    if(pid2==0){
        doChild2(pipeFd[0], N, A, fatherPid);
    }
    
    close(pipeFd[0]);
    int signalsRecived = 0;
    while(signalsRecived < 2){
        pause();
    }

}

void doChild1(int pipeWrite, int fdIn){
    fprintf(stdout, "Child 1 \n");
    close(0);
    dup(fdIn);
    close(fdIn);
    close(1);
    dup(pipeWrite);
    close(pipeWrite);

    execl("/usr/bin/rev", "/usr/bin/rev", (char *)0);
    perror("Rev error");
    exit(1);
}
void doChild2(int pipeRead, int lineToRead, int limit,  int ppid){
    fprintf(stdout, "Child 2 \n");

    char letter;
    int readStatus;
    int linea = 0;
    int numOfChar = 0;
    while((readStatus = read(pipeRead, &letter, 1)) > 0){
        if(letter == '\n'){
            linea++;
        } 

        if(linea == lineToRead){
            //write(1, &letter, 1);
            numOfChar++;
        }
    }

    (numOfChar >= limit) ? kill(ppid, SIGUSR1) : kill(ppid, SIGUSR2);
    exit(0);
}

void handlerUSR1(int signum){
    fprintf(stdout, "La riga %d contiene %d o più caratteri \n", N, A);
    exit(0);
}
void handlerUSR2(int signum){
    fprintf(stdout, "La riga %d contiene meno di %d caratteri\n", N, A);
    exit(0);
}