#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeRead, char chatToCheck);
void doChild2(int pipeWrite, int fdIn, int pid1);
void handlerUSR1(int signum);
void handlerUSR2(int signum);
void hardlerALARM(int signum);

int pid0;
int pid1;
char fin[80];
char fout[80];

int main(int argc, char *argv[]){

    int fdIn;
    int fdOut;
    int T;
    char c;
    int pipeFd[2];
    pid0 = getpid();

    if(argc != 5){
        fprintf(stderr,"Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/'){
        fprintf(stdout,"Params Error, file not readable\n");
        return(0);
    }
    strcpy(fin, argv[1]);

    if(access(argv[2], 00) == 0 || argv[2][0] != '/'){
        fprintf(stdout,"Params Error, output file exist or not absolute\n");
        return(0);
    }
    strcpy(fout, argv[2]);

    c = argv[3][0];
    if(c != 'P' && c != 'D'){
        fprintf(stdout, "Params Error, char not correct\n");
    }

    T = atoi(argv[4]);
    if(T <= 0){
        fprintf(stdout,"Params Error, T not correct\n");
        return(0);
    }


    //1 scrittura, 0 lettura
    pipe(pipeFd);
    signal(SIGUSR1, handlerUSR1);
    signal(SIGUSR2, handlerUSR2);
    pid1 = fork();
    if(pid1 == 0){
        close(pipeFd[1]);
        doChild1(pipeFd[0], c);
    }

    int pid2 = fork();
    if(pid2 == 0){
        close(pipeFd[0]);
        doChild2(pipeFd[1], fdIn, pid1);
    }

    signal(SIGALRM, hardlerALARM);
    close(pipeFd[0]);
    close(pipeFd[1]);

    alarm(T);
    int i = 0;
    while(i < 2){
        pause();
        i++;
    }

}

void doChild1(int pipeRead, char chatToCheck){
    fprintf(stdout, "Child1\n");
    int number;
    int readStatus;
    readStatus = read(pipeRead, &number, sizeof(int));
    fprintf(stdout, "Linee %d\n", number);
    int X = number % 2;
    if(chatToCheck == 'P' && X == 0){
        kill(pid0, SIGUSR1);
    }else if(chatToCheck == 'D' && X == 1){
        kill(pid0, SIGUSR2);
    }
    close(pipeRead);
    exit(0);
}

void doChild2(int pipeWrite, int fdIn, int pid1){
    fprintf(stdout, "Child2\n");
    int readStatus;
    char letter;
    int lines = 1;
    while((readStatus = read(fdIn, &letter, 1)) > 0){
       if(letter == '\n'){
           lines++;
       }
    }
    
    write(pipeWrite, &lines, sizeof(int));
    close(pipeWrite);
    exit(0);
}

void handlerUSR1(int signum){
    fprintf(stdout, "PARI\n");
    int fdOut;
    if((fdOut = open(fout, O_CREAT | O_WRONLY , 0777)) < 0){
        printf("Params Error, non riesco a creare il file\n");
        exit(1);
    }

    close(1);
    dup(fdOut);
    fprintf(stdout, "%s contiene un numero pari di righe\n", fin);
    exit(0);
}
void handlerUSR2(int signum){
    fprintf(stdout, "DISPARI\n");
    int fdOut;
    if((fdOut = open(fout, O_CREAT | O_WRONLY , 0777)) < 0){
        printf("Params Error, non riesco a creare il file\n");
        exit(1);
    }

    close(1);
    dup(fdOut);
    fprintf(stdout, "%s contiene un numero dispari di righe\n", fin);
    exit(0);
}

void hardlerALARM(int signum){
    fprintf(stdout, "Uccido p1\n");
    kill(pid1, SIGKILL);
    exit(0);
}