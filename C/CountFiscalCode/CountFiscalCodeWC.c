#include<signal.h>
#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>

int fdIn;
int pipefd[2];
int wordCount = 0;

void handlerChild(int signum){
    int status = 0;
    close(fdIn);
    close(pipefd[0]);
    int pid = wait(&status);
    printf("Finisce anche il padre, parole lette: %d\n", wordCount);
    exit(EXIT_SUCCESS);
}

void childReadWords(int fdPipe, char *fileName){
    close(1);
    dup(fdPipe);
    close(fdPipe);
    
    //For MacOS
    execl("/usr/bin/wc", "wc", "-w", fileName, (char*)0);
    //For Linux
    //execl("/bin/wc", "wc", "-w", fileName, (char*)0);
    perror("Execl fallita");
    exit(-1);;
}

int main(int argc, char *argv[]){

    if(argc != 2){
        printf("Params Error\n");
        return(0);
    }

    if((fdIn = open(argv[1], O_RDONLY)) < 0){
        printf("Params Error\n");
        return(0);
    }

    signal(SIGCHLD, handlerChild);

    pipe(pipefd);
    int pid = fork();
    if(pid == 0){
        close(pipefd[0]);
        childReadWords(pipefd[1], argv[1]);
    }
    
    close(pipefd[1]);

    char NcodiceFiscale;
    int readStatus;
    int found = 0;
    while((readStatus = read(pipefd[0], &NcodiceFiscale, 1)) > 0){
        // this becouse wc -w return nWord filemane
        if(NcodiceFiscale != ' ' && found == 0){
            wordCount = atoi(&NcodiceFiscale);
            found = 1;
        }
    }
    
}