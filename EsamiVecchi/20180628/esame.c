#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeWrite, int fdIn);
void doChild2(int pipeRead, char *fout);
void handlerUSR1(int signum);
void handlerAlarm(int signum);
int pid1;
int volteSpazio;

int main(int argc, char *argv[]){
    
    int fdIn;
    int Tout;
    int pipeFd[2];

    if(argc != 4){
        fprintf(stderr,"Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0)){
        printf("Params Error, file not readable\n");
        return(0);
    }

    if(access(argv[3], 00) == 0){
        printf("Params Error, output file exist\n");
        return(0);
    }

    Tout = atoi(argv[2]);
    if(Tout <= 0){
        printf("Params Error, Tout non corretto\n");
        return(0);
    }

    //1 scrittura, 0 lettura
    pipe(pipeFd);
    pid1 = fork();
    if(pid1 == 0){
        signal(SIGUSR1, handlerUSR1);
        close(pipeFd[0]);
        doChild1(pipeFd[1], fdIn);
    }

    int pid2 = fork();
    if(pid2 == 0){
        close(pipeFd[1]);
        doChild2(pipeFd[0], argv[3]);
    }

    signal(SIGALRM, handlerAlarm);
    alarm(Tout);
    close(pipeFd[0]);
    close(pipeFd[1]);
    close(fdIn);

    while(1){
        printf("Sono P0 (%d)\n", getpid());
        sleep(1);
    }

    exit(0);
}

void doChild1(int pipeWrite, int fdIn){
    printf("Sono P1 (%d)\n", getpid());
    char letter;
    int readStatus;
    int stop = 0;
    volteSpazio = 0;
    while(1){
        int res = lseek(fdIn, -1, 2);
        stop = 0;
        while((readStatus = read(fdIn, &letter, 1)) > 0 && stop == 0){
            write(pipeWrite, &letter, 1);
            //printf("scrivo\n");
            if(letter == '\n'){
                volteSpazio++;
            }
            int res = lseek(fdIn, -2, 1);
            if(res < 0){
                stop = 1;
            }
        }
    }
    exit(0);
}

void doChild2(int pipeRead, char *fout){
    printf("Sono P2 (%d)\n", getpid());
    int fdOut;
    if((fdOut = open(fout, O_TRUNC | O_CREAT | O_WRONLY , 0777)) < 0){
        printf("Params Error, non riesco a creare il file\n");
        exit(1);
    }

    close(0);
    dup(pipeRead);
    close(pipeRead);
    close(1);
    dup(fdOut);
    close(fdOut);
    
    execl("/bin/cat", "/bin/cat" , (char *)0);
    perror("Not working");
    exit(0);
}

void handlerUSR1(int signum){
    printf("P1: in totale ho inviato %d volte il carattere newline a P2 \n", volteSpazio);
    exit(0);
}

void handlerAlarm(int signum){
    printf("Invio il segnale a P1 \n");
    kill(pid1, SIGUSR1);
    exit(0);
}