#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<fcntl.h> 
#define MAX 100

typedef struct{
    int ruolo;
    int id;
    int ore;
    int minuti;
}marcatura;

int main(int argc, char *argv[]){
    
    int status = 0;
    char buff;

    if(argc != 4){
        printf("Params Error\n");
        return(0);
    }
    
    int fdFinIn= open(argv[2], O_WRONLY| O_CREAT| O_TRUNC, 00640);
    int fdFinOut= open(argv[3], O_WRONLY| O_CREAT| O_TRUNC, 00640);
    int fdInput = open(argv[1], O_RDONLY);
    
    
    //printf("%s\n", argv[1]);
    //printf("%d\n", fdInput);
    
    int i = 0;
    int k = 0;
    char data[MAX];
    marcatura macaturaToWrite;
    while(status = read(fdInput, &buff, 1) > 0){
        if(buff != ',' && buff != '\n'){
            data[i] = buff;
            i++;
        }else{
            data[i] = '\0'; 
            switch (k % 5){
                case 0:
                    macaturaToWrite.id = atoi(data);
                    break;
                case 1:
                    macaturaToWrite.ruolo = atoi(data);
                    break;
                case 2:
                    macaturaToWrite.ore = atoi(data);
                    break;
                case 3:
                    macaturaToWrite.minuti = atoi(data);    
                    break;
                case 4:
                    if(data[0] == 'I'){
                        printf("%d, %d, %d, %d \n", macaturaToWrite.id,macaturaToWrite.ruolo, macaturaToWrite.ore, macaturaToWrite.minuti);
                        write(fdFinIn, &macaturaToWrite, sizeof(marcatura));
                    }else{
                        printf("%d, %d, %d, %d \n", macaturaToWrite.id,macaturaToWrite.ruolo, macaturaToWrite.ore, macaturaToWrite.minuti);
                        write(fdFinOut, &macaturaToWrite, sizeof(marcatura));
                    }
                    break;
            }
            data[0] = '\0';
            i = 0;
            k++;
        }
    }
    
    if(macaturaToWrite.id != NULL){
        if(data[0] == 'I'){
            printf("%d, %d, %d, %d \n", macaturaToWrite.id,macaturaToWrite.ruolo, macaturaToWrite.ore, macaturaToWrite.minuti);
            write(fdFinIn, &macaturaToWrite, sizeof(marcatura));
        }else{
            printf("%d, %d, %d, %d \n", macaturaToWrite.id,macaturaToWrite.ruolo, macaturaToWrite.ore, macaturaToWrite.minuti);
            write(fdFinOut, &macaturaToWrite, sizeof(marcatura));
        }
    }
                       
    exit(0);
}

/* test string 
 * 
 * gcc InOut.c -o InOut
 * 
 * 
 */
