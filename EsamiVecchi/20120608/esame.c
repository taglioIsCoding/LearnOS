#include<unistd.h>
#include<fcntl.h> 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>

void doChild1(int pipeRead, char c1, char c2);
void doChild2(int pipeWrite, int fdIn, int pid1);
void handlerUSR1(int signum);
int numSegnali = 0;

int main(int argc, char *argv[]){
    
    int fdIn;
    int fdOut;
    char c1;
    char c2;
    char selectedChar;
    int pipeFd[2];


    if(argc != 5){
        printf("Numero di parametri insufficienti\n");
        return(0);
    }

    if(((fdIn = open(argv[1], O_RDONLY)) < 0) || argv[1][0] != '/' ){
        printf("Params Error, file not readable\n");
        return(0);
    }

    if(((fdOut = open(argv[2], O_WRONLY|O_APPEND)) < 0) || argv[2][0] != '/' ){
        printf("Params Error, file not writable\n");
        return(0);
    }

    c1 = *argv[3];
    if(strlen(argv[3]) != 1){
         printf("Params Error, c1 non corretto\n");
         return(0);
    }
    c2 = *argv[4];
    if(strlen(argv[4]) != 1){
         printf("Params Error, c2 non corretto\n");
         return(0);
    }

    //1 scrittura, 0 lettura
    pipe(pipeFd);

    int pid1 = fork();
    if(pid1 == 0){
        signal(SIGUSR1, handlerUSR1);
        close(pipeFd[1]);
        doChild1(pipeFd[0], c1, c2);
    }

    int pid2 = fork();
    if(pid2 == 0){
        close(pipeFd[0]);
        doChild2(pipeFd[1], fdIn, pid1);
    }

    close(pipeFd[0]);
    close(pipeFd[1]);
    close(fdIn);
    exit(0);
}

void doChild1(int pipeRead, char c1, char c2){
    char letter;
    int readStatus;
    int ocC1 = 0;
    int ocC2 = 0;
    int lineeSogliaC1 = 0;
    int lineeSogliaC2 = 0;

    
    while((readStatus = read(pipeRead, &letter, 1)) > 0){
        if(letter == c1){
            ocC1++;
        }   
        if(letter == c2){
            ocC2++;
        }

        if(letter=='\n'){
            if(ocC1 > 2){
                lineeSogliaC1++;
            }
            if(ocC2 > 2){
                lineeSogliaC2++;
            }
        }
    }

    if(numSegnali%2 == 0){
        printf("L'input contiene %d righe con almeno 15 occorrenze del carattere %c\n", lineeSogliaC1, c1);
    }else{
        printf("L'input contiene %d righe con almeno 15 occorrenze del carattere %c\n", lineeSogliaC2, c2);
    }

    close(pipeRead);
    printf("Finisce 1 %d %d\n", lineeSogliaC1, lineeSogliaC2);
    exit(0);
}

void doChild2(int pipeWrite, int fdIn, int pid1){
    
    char line;
    int readStatus;
    while((readStatus = read(fdIn, &line, 1)) > 0){
       //printf("Riga: %s\n", line);
       if(line == '#'){
           kill(pid1, SIGUSR1);
           usleep(100);
           while((readStatus = read(fdIn, &line, 1)) > 0 && (line != '\n' || line == EOF)){
           }
       }else {
            //printf("%c", line);
            write(pipeWrite, &line, 1);
       }
    }

    close(pipeWrite);
    close(fdIn);
    printf("Finisce 2\n");
    exit(EXIT_SUCCESS);
}

void handlerUSR1(int signum){
    printf("Ho ricevuto un segnale \n");
    numSegnali++;
    return;
}