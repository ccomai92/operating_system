#include <stdio.h>   // for printf
#include <stdlib.h>  // for exit 
#include <unistd.h>  // for fork, execlp
#include <sys/wait.h>   //wait

int main(int argc, char *argv[])
{
    int pid; // process ID
    printf("Starting in the original, soon-to-be-parent process\n");
    // fork another process 
    pid = fork();
    if (pid < 0) { // error occurred
        fprintf(stderr, "Fork Failed\n");
        exit(EXIT_FAILURE);
    }
    // ---------- CHILD SECTION -----------   
    else if (pid == 0) {
        printf( "In the new, child process\n");
    }
    // ---------- PARENT SECTION ----------
    else {
        printf("In the parent process\n");

        // try commenting this out and see what happens:
        wait(NULL); // wait for child before exiting
    }
}
