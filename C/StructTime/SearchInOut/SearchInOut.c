#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<signal.h>
#include<unistd.h>
#include<fcntl.h> 

typedef struct{
    int ruolo;
    int id;
    int ore;
    int minuti;
}marcatura;
int R = -1;
int endedChild = 0;
int pid1, pid2;


void handlerChild(int signum){
    endedChild++;
    int status = 0;
    int pid = wait(&status);
    if(status == 0){
        if(pid == pid1){
            int data; 
            int fdTemp1 = open("temp1", O_RDONLY);
            for(int i = 0; i < 3; i++){
                read(fdTemp1, &data, sizeof(int));
                printf("%d ", data);
            }
            printf("\n");
        }else{
            int data;
            int fdTemp2 = open("temp2", O_RDONLY);
            for(int i = 0; i < 3; i++){
                read(fdTemp2, &data, sizeof(int));
                printf("%d ", data);
            }
            printf("\n");
        }
    }else{
        if(pid == pid1){
            printf("il processo %d non ha trovato ingressi da parte di dipendenti di ruolo %d\n", pid1, R);
        }else{
            printf("il processo %d non ha trovato ingressi da parte di dipendenti di ruolo\n", pid2);
        }
    }
    if(endedChild == 2){
        printf("Finisce anche il padre\n");
        exit(EXIT_SUCCESS);
    }
}

int main(int argc, char *argv[]){
    
    int status = 0;
    char buff;

    if(argc != 4){
        printf("Params Error\n");
        return(0);
    }

    R = atoi(argv[3]);
    if(R < 0 || R > 3){
        printf("Params Error\n");
        return(0);
    }

    signal(SIGCHLD, handlerChild);

    pid1 = fork();
    if(pid1 == 0){

        signal(SIGCHLD, SIG_DFL);

        int fdFinIn= open(argv[1], O_RDONLY);
        marcatura marcaturaRead; 
        while ((status = read(fdFinIn, &marcaturaRead, sizeof(marcatura))) > 0){
            if(marcaturaRead.ruolo == R){
                int fdTemp1 = open("temp1", O_WRONLY| O_CREAT| O_TRUNC, 00640);
                write(fdTemp1, &marcaturaRead.id, sizeof(int));
                write(fdTemp1, &marcaturaRead.ore, sizeof(int));
                write(fdTemp1, &marcaturaRead.minuti, sizeof(int));
                close(fdTemp1);
                exit(EXIT_SUCCESS);
            }
        }
        //printf("%d, %d, %d, %d \n", marcaturaRead.id,marcaturaRead.ruolo, marcaturaRead.ore, marcaturaRead.minuti);
        exit(1);
    }

    pid2 = fork();
    if(pid2 == 0){

        signal(SIGCHLD, SIG_DFL);

        int fdFinOut= open(argv[2], O_RDONLY);
        lseek(fdFinOut, -sizeof(marcatura), SEEK_END);
        marcatura marcaturaRead;
        if((status = read(fdFinOut, &marcaturaRead, sizeof(marcatura))) > 0){
            int fdTemp2 = open("temp2", O_WRONLY| O_CREAT| O_TRUNC, 00640);
            write(fdTemp2, &marcaturaRead.id, sizeof(int));
            write(fdTemp2, &marcaturaRead.ore, sizeof(int));
            write(fdTemp2, &marcaturaRead.minuti, sizeof(int));
            close(fdTemp2);
            exit(EXIT_SUCCESS);
        }
        exit(1);
    }

    while(1){
        pause();
    }
}