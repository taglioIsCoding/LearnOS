#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>
#include<unistd.h>
#include<fcntl.h> 

int fdIn;
int pid1;
int pid2;
int endedChild = 0;
int changes = 0;

void switchCouple(int signum){
    int a,b;
    lseek(fdIn, -2 * sizeof(int), 1);
    read(fdIn, &a, sizeof(int)); 
    read(fdIn, &b, sizeof(int));
    lseek(fdIn, -2 * sizeof(int), 1);
    write(fdIn, &b, sizeof(int));
    write(fdIn, &a, sizeof(int));
    lseek(fdIn, -2 * sizeof(int), 1);
    read(fdIn, &a, sizeof(int)); 
    read(fdIn, &b, sizeof(int));
    printf("scambiati: %d %d\n", a, b);
    kill(getppid(), SIGUSR1);
    sleep(1);
    kill(pid2, SIGUSR2);
}

void terminate(int signum){
    close(fdIn);
    exit(EXIT_SUCCESS);
}
void countChanges(int signum){
    changes++;
}
void nothing(int signum){}

void handlerChild(int signum){
    int status = 0;
    int pid = wait(&status);
    endedChild++;
    if(endedChild == 2){
        printf("Finisce anche il padre, scambi fatti: %d\n", changes);
        exit(EXIT_SUCCESS);
    }
}

int main(int argc, char *argv[]){

    if(argc != 3){
        printf("Params Error\n");
        return(0);
    }

    if((fdIn = open(argv[1], O_RDWR)) < 0){
        printf("Params Error\n");
        return(0);
    }

    signal(SIGCHLD, handlerChild);
    signal(SIGUSR1, countChanges);

    pid1 = fork();
    if(pid1 == 0){
        signal(SIGUSR1, switchCouple);
        signal(SIGUSR2, SIG_IGN);
        signal(SIGALRM, terminate);
        for(;;){
            pause();
        }
    }

    pid2 = fork();
    if(pid2 == 0){
        signal(SIGUSR2, nothing);
        int a,b, statusa, statusb;
        while ((statusa = read(fdIn, &a, sizeof(int))) > 0 && (statusb = read(fdIn, &b, sizeof(int))) > 0 ){
            printf("%d %d\n", a, b);
            if (a > b){
                //scambia
                printf("Scambia\n");
                kill(pid1, SIGUSR1);
                pause();
            }else{
                //non fa nulla
                printf("Niente\n");
                kill(pid1, SIGUSR2);
            }
        }
        kill(pid1, SIGALRM);
        exit(EXIT_SUCCESS);
    }

    for(;;){
        sleep(1);
    }
}