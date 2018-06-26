#include <stdlib.h>     //exit
#include <stdio.h>      //perror
#include <unistd.h>     //fork, pipe
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

    if (pipe(pipeFD) < 0) {                     // make pipe between Grand parent and parent 
        perror("Error in creating pipe"); 
        exit(EXIT_FAILURE); 
    }

    pid = fork();                               // fork Grand parent and parent  
    if (pid < 0) {
        perror("Error during first fork()"); 
        exit(EXIT_FAILURE); 
    } 

    if (pid == 0) {                             // Parent process
        
        int pipeFD1[2]; 

        if (pipe(pipeFD1) < 0) {                // make pipe between parent and child
            perror("Error in creating pipe1"); 
            exit(EXIT_FAILURE); 
        }

        pid_t childPid = fork();                // fork parent and child 
        if (childPid < 0) {
            perror("Error during second fork"); 
            exit(EXIT_FAILURE); 
        }

        if (childPid == 0) {                    // child process
            cerr << "child" << endl; 
            close(pipeFD1[READ]); 
            dup2(pipeFD1[WRITE], 1);            // std::cout is now pipe1 write
            execl("/bin/ps", "ps", "-A", (char*)0); // execute ps -A and write to pipe
            perror("child failed");             
        } else {                                // parent process
            cerr << "parent" << endl;       
            wait(NULL);                         // wait for child process to be done
            cerr << "child done" << endl;           
            close(pipeFD1[WRITE]);                  
            dup2(pipeFD1[READ], 0);             // set std::cin to piepe1 
            dup2(pipeFD[WRITE], 1);             // set std::cout to pipe out
            execl("/bin/grep", "grep", argv[1], "-", (char*)0);  // execute grep argv[1]
            perror("parent failed"); 
        } 
    } else {                                    // grand parent process 
        cerr << "grand parent" << endl; 
        int saved_stdout = dup(1); 
        wait(NULL);                             // wait for parent process to be done
        cerr << "parent done" << endl; 
        close(pipeFD[WRITE]);                      
        dup2(pipeFD[READ], 0);                  // set std::cin to pipe 
        dup2(saved_stdout, 1);                  // set std::cout back 
        execl("/usr/bin/wc", "wc", "-l", (char*)0); // execute wc -l 
        perror("grand parent failed"); 
    }

    return EXIT_SUCCESS; 
}