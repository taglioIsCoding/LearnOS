#include<stdio.h>
#include <signal.h>
#include<unistd.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>
#define MAX 100

void child1();
void child2(int pidChild1);
void handlerSigusr2(int signum);
void handlerSigusr2(int signum);
void handlerAlarm(int signum);
void handlerChild(int signum);

int N=0;
char command[];
int endedChild = 0;
int main(int argc, char *argv[]){

    if(argc < 3){
        printf("Not enough parameters\n");
        return(0);
    }

    N = atoi(argv[2]);
    strcpy(command, argv[1]);
    signal(SIGCHLD, handlerChild);
    int pidChild1 = 0;

    int pid1 = fork();
    if(pid1 == 0){
        signal(SIGCHLD, SIG_DFL);
        child1();
        exit(0);
    }
    int pid2 = fork();
    if(pid2 == 0){
        signal(SIGCHLD, SIG_DFL);
        child2(pid1);
        exit(0);
    }

    for(int j=0;;j++){
        printf("padre: %f\n", exp(j));
        sleep(1);
    }

}

void handlerSigusr1(int signum){
    printf("FINE!, pid: %d\n", getpid());
    exit(EXIT_SUCCESS);
}

void handlerAlarm(int signum){
    char commandPath[MAX] = "/bin/";
    strcat(commandPath, command);
    int pid = fork();
    if (pid ==0){
        execl(commandPath, command, (char *)0);
    }
    exit(EXIT_SUCCESS);
}

void handlerSigusr2(int signum){
    alarm(N);
    signal(SIGALRM, handlerAlarm);
    while(1){
        printf("Signal 2 pid1: %d\n", getpid());
        sleep(1);
    }
}

void child2(int pidChild1){
    if(getpid() % 2 == 0){
        kill(pidChild1, SIGUSR2);
        exit(EXIT_SUCCESS);
    }
    kill(pidChild1, SIGUSR1);
    exit(EXIT_SUCCESS);
}

void child1(int N){
    signal(SIGUSR1, handlerSigusr1);
    signal(SIGUSR2, handlerSigusr2);
    pause();
}

void handlerChild(int signum){
    int status = 0;
    int pid = wait(&status);
    endedChild++;
    if(endedChild == 2){
    printf("Finisce anche il padre\n");
    exit(EXIT_SUCCESS);
    }
}

/*
test command
./es3 ls 5
*/