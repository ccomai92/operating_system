#include <stdlib.h>  //exit
#include <stdio.h>   //perror
#include <unistd.h>  //fork, pipe
#include <sys/wait.h>   //wait
#include <iostream>
using namespace std;

// ps -A | grep argv[1] | wc -l 
int main(int argc, char *argv[]) {
    if (argc < 2 || argc > 2) {
        perror("Require one argument"); 
        exit(EXIT_FAILURE); 
    }
    
    int status; 
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
        int pipeFD1[2]; 
        if (pipe(pipeFD1) < 0) {
            perror("Error in creating pipe1"); 
            exit(EXIT_FAILURE); 
        }

        pid_t childPid = fork();
        if (childPid < 0) {
            perror("Error during second fork"); 
            exit(EXIT_FAILURE); 
        }

        if (childPid == 0) { // child process
            cerr << "cccccchild" << endl; 
            close(pipeFD1[READ]); 
            dup2(pipeFD1[WRITE], 1); // std::cout is now child write
            execl("/bin/ps", "ps", "-A", (char*)0);  
            perror("child failed");  
        } else { // parent process
            cerr << "parentttttt" << endl; 
            wait(NULL); 
            cerr << "child done" << endl; 
            close(pipeFD1[WRITE]); 
            dup2(pipeFD1[READ], 0); 
            //char buf[50000]; 
            //int n = read(pipeFD[READ], buf, 50000); 
            //buf[n] = '\0'; 
            dup2(pipeFD[WRITE], 1);
            //cout << buf << endl; 
            execl("/bin/grep", "grep", argv[1], "-", (char*)0); 
            perror("parent failed"); 
        } 
    } else { // grand parent process 
        cerr << "grand parent" << endl; 
        int saved_stdout = dup(1); 
        wait(NULL); 
        cerr << "parent done" << endl; 
        close(pipeFD[WRITE]); 
        dup2(pipeFD[READ], 0); 
        dup2(saved_stdout, 1); 
        //char buf[50000]; 
        //int n = read(pipeFD[READ], buf, 50000); 
        //buf[n] = '\0';  
        //cout << buf << endl; 
        execl("/usr/bin/wc", "wc", "-l", (char*)0); 
        perror("grand parent failed"); 
    }


    return EXIT_SUCCESS; 
}