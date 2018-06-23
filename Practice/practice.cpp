#include <stdlib.h> 
#include <stdio.h>
#include <unistd.h> 
#include <sys/wait.h> 
#include <iostream> 
using namespace std; 

int main() {
    enum {READ, WRITE};
    pid_t pid; 
    int pipeFD[2]; 

    if (pipe(pipeFD) < 0) {
        perror("Error in creating pipe"); 
        exit(EXIT_FAILURE); 
    }

    pid = fork(); 
    if (pid < 0) {
        perror("Error during first fork()"); 
        exit(EXIT_FAILURE); 
    }

    if (pid == 0) { // Parent process
        pid_t childPid = fork(); 
        if (childPid < 0) {
            perror("Error during second fork"); 
            exit(EXIT_FAILURE); 
        }

        if (childPid == 0) { // child process
            cerr << "child" << endl; 
            //dup2(pipeFD[WRITE], 1); 
            //execl("/bin/ps", "ps", "-A", NULL); 
           
            
        } else { // parent process
            cerr << "parent" << endl; 

            wait(NULL); 
         
            cerr << "child done" << endl; 
            dup2(pipeFD[WRITE], 1); 
            execl("/bin/ps", "ps", "-A", NULL); 
            //dup2(pipeFD[READ], 0); 
            //dup2(pipeFD[WRITE], 1); 
            //execl("/bin/grep", "grep", "tt", "-", NULL); 
           
        }
    } else { // grand parent process 
        cerr << "grand parent" << endl; 
        int savestdout = dup(1); 
        wait(NULL); 
        cerr << "parennt done" << endl; 
        dup2(pipeFD[READ], 0); 
        dup2(savestdout, 1); 
        //char buf[50000]; 
        //int n = read(pipeFD[READ], buf, 50000); 
        //buf[n] = '\0';  
        //cout << buf << endl;
        execl("/bin/wc", "wc", "-l", NULL); 
        perror("grand parent fail"); 
        
    }

    return EXIT_SUCCESS; 
}