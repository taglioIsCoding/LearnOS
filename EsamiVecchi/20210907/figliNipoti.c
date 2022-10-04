#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int fdIn, int pipeWrite, char c);
void doChild2(int pipeRead, int pipeWrite);
void doChild3(int pipeRead, char *fout);
void handlerSIGCHILD0(int signum);
void handlerSIGCHILD1(int signum);

int figli_terminati;

int main(int argc, char *argv[]){
    
    int fdIn;
    int fdOut;
    char c;
    int pipeFd12[2];
    int pipeFd23[2];

    if(argc != 4){
        fprintf(stderr,"Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/'){
        fprintf(stderr,"Params Error, file not readable\n");
        return(0);
    }

    if(access(argv[2], 00) == 0 || argv[2][0] != '/'){
        fprintf(stderr,"Params Error, output file exist\n");
        return(0);
    }

    c = *argv[3];
    if(strlen(argv[3]) != 1){
         printf("Params Error, C non corretto\n");
         return(0);
    }

    signal(SIGCHLD, handlerSIGCHILD0);

    //1 scrittura, 0 lettura
    pipe(pipeFd23);
    int pid1 = fork();
    
    if(pid1==0){
        pipe(pipeFd12);
        signal(SIGCHLD, handlerSIGCHILD1);
        figli_terminati = 0;
        int pid2 = fork();
        if(pid2==0){
            signal(SIGCHLD, SIG_DFL);
            close(pipeFd12[1]);
            close(pipeFd23[0]);
            doChild2(pipeFd12[0], pipeFd23[1]);
        }else{
            close(pipeFd12[0]);
            doChild1(fdIn, pipeFd12[1], c);
        }
    }

    close(pipeFd12[1]);
    close(pipeFd12[0]);

    int pid3 = fork();
    if(pid3==0){
        signal(SIGCHLD, SIG_DFL);
        doChild3(pipeFd23[0], argv[2]);
    }

    close(pipeFd23[1]);
    close(pipeFd23[0]);

    while(figli_terminati != 5){
        pause();
    }

}

void doChild1(int fdIn, int pipeWrite, char c){
     fprintf(stdout, "child 1 %d \n", getpid());

    char letter;
    int readStatus;
    while((readStatus = read(fdIn, &letter, 1)) > 0){
        if(letter != c){
            write(pipeWrite, &letter, 1);
        }
    }

    close(pipeWrite);
    while(1){
        sleep(1);
       pause();
    }

    exit(0);
}

void doChild2(int pipeRead, int pipeWrite){
    fprintf(stdout, "child 2 %d\n", getpid());

    close(0);
    dup(pipeRead);
    close(pipeRead);
    close(1);
    dup(pipeWrite);
    close(pipeWrite);

    execl("/usr/bin/wc", "/usr/bin/wc", "-m", (char *)0);
    perror("exec non funziona\n");
    exit(1);
}
void doChild3(int pipeRead, char *fout){
    fprintf(stdout, "child 3 %d\n", getpid());

    int fdOut;
    if((fdOut = open(fout, O_TRUNC | O_CREAT | O_WRONLY , 0777)) < 0){
        fprintf(stderr, "file output non creato \n");
    }

    char letter;
    int readStatus;
    int flag = 1;
    while((readStatus = read(pipeRead, &letter, 1)) > 0){
        write(fdOut, &letter, 1);
        if(letter == '\n'){
            close(pipeRead);
        }
    }
    
    exit(0);
}

void handlerSIGCHILD0(int signum){
    figli_terminati++;
    int status;
    int childPid = wait(&status);
    printf("P0 (%d): ricevuto sigchld  da %d con staus %d - figli terminati = %d.\n",getpid(),childPid,status,figli_terminati);
    if (figli_terminati==2)
    {   printf("P0 (%d): termino.\n",getpid());    
        exit(0);
    }
}

void handlerSIGCHILD1(int signum){
    figli_terminati++;
    int status;
    int childPid = wait(&status);
    printf("P1 (%d): ricevuto sigchld  da %d con staus %d - figli terminati = %d.\n",getpid(),childPid,status,figli_terminati);
    if (figli_terminati==1)
    {   printf("P1 (%d): termino.\n",getpid());    
        exit(0);
    }
}