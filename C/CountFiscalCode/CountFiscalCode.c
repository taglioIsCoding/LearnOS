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

void childReadWords(int fdPipe){
    char codiceFiscale[17];
    int status;

    // Leggo parola completa
    while ((status = read(fdIn, codiceFiscale, 16) > 0)){
        codiceFiscale[16] = '\0';
        printf("codice: %s\n", codiceFiscale);
        lseek(fdIn, sizeof(char), 1);
        int writeRes = write(fdPipe, codiceFiscale, 15);
        if(writeRes < 0){
            printf("pipe error\n");
        }
    }

    // Leggo carattere per carattere
    // int i = 0;
    // char letter;
    // while ((status = read(fdIn, &letter, 1) > 0)){
    //     if(letter == ' ' || letter == '\n'){
    //         codiceFiscale[i] = '\0';
    //         printf("codice: %s\n", codiceFiscale);
    //         int writeRes = write(fdPipe, codiceFiscale, 15);
    //         if(writeRes < 0){
    //             printf("pipe error\n");
    //         }
    //         printf("res scrittura %d\n", writeRes);
    //         i = 0;
    //         codiceFiscale[0] = '\0';
    //     }else{
    //         codiceFiscale[i] = letter; 
    //         i++;
    //     }
    // }
    // codiceFiscale[i] = '\0';
    // printf("codice: %s\n", codiceFiscale);
    // int writeRes = write(fdPipe, codiceFiscale, 15);
    // if(writeRes < 0){
    //     printf("pipe error\n");
    // }
    // printf("res scrittura %d\n", writeRes);
    // i = 0;
    // codiceFiscale[0] = '\0';

    close(fdIn);
    exit(EXIT_SUCCESS);
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
        childReadWords(pipefd[1]);
    }
    
    close(pipefd[1]);

    char codiceFiscale[17];
    int readStatus;
    while((readStatus = read(pipefd[0], codiceFiscale, 16)) > 0){
        wordCount++;
    }
    
}