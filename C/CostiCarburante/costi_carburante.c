#include<stdio.h>
#include <unistd.h>
#include<stdlib.h>
#define MAXKM 100

typedef struct CamionInfo{
        int index;
        int pid;
}CamionInfo;

int childDoAvg(int *kmForMonth, float costForKm);
int getCamionIndex(CamionInfo * camions, int pid, int size);


int main(int argc, char *argv[]){ 
    
    int avgCost = 0;
    
    //check the number of params
    if(argc != 3){
        printf("Numero di parametri insufficienti\n");
        return(0);
    }
    
    int numCamion = atoi(argv[1]);
    float costForKm = atof(argv[2]);
    int kmForMonth[numCamion][12];
    struct CamionInfo camionArray[numCamion];
     
    
    for(int i = 0; i < numCamion; i++){
        for(int j = 0; j < 12; j++){
            kmForMonth[i][j] = rand() % MAXKM;
        }
    }
    
    //print the random matrix
    //for(int i = 0; i < numCamion; i++){
    //    for(int j = 0; j < 12; j++){
    //         printf(" %d ",  kmForMonth[i][j]);
    //    }
    //    printf("\n");
    //}
    
    //v1 not so concurrent
    // for(int i = 0; i < numCamion; i++){
    //     int pid = fork();
    //     if(pid == 0){ 
    //         childDoAvg(kmForMonth[i], costForKm);
    //     }else{
    //         wait(&avgCost);
    //         printf("Pid: %d, index: %d, avg: %d \n\n", pid, i, avgCost>>8);
    //     }
    // }

    //v2 
    for(int i = 0; i < numCamion; i++){
        int pid = fork();
        struct CamionInfo camion;
        if(pid == 0){
            childDoAvg(kmForMonth[i], costForKm);
        }else{
            camion.pid = pid;
            camion.index = i;
            camionArray[i] = camion;
        }
    }

    int size = sizeof(camionArray) / sizeof(CamionInfo);

    for(int i = 0; i < numCamion; i++){
        if(getpid() != 0){
            int endedPid = wait(&avgCost);
            printf("Pid: %d del camion: %d con media: %d \n", endedPid, getCamionIndex(camionArray, endedPid, size) ,avgCost>>8);
        }
    }

}

int childDoAvg(int *kmForMonth, float costForKm){
    int sum = 0;
    for(int j = 0; j < 12; j++){
        sum = sum + kmForMonth[j];
    }
    
    int intAvg = (int)(sum*costForKm)/12;
     
    // for debugging
    //  printf("\n");
    //  printf("Float avg %f \n", avg);
    //  printf("int avg %d \n", intAvg);
    
    exit(intAvg); 
}

int getCamionIndex(CamionInfo *camions, int pid, int size){
    for(int i = 0; i < size; i++){
        if(camions[i].pid == pid){
            return camions[i].index;
        }
    }

    return -1;
}

/* test string 
 * 
 * gcc costi_carburante.c -o costi_carburante.c
 * 
 * Math rand
 */
