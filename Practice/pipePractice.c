#include <stdio.h>
#include <stdlib.h>
#include <unistd.h> 

int main(int argc, char *argv[]) {
    int pid; 
    int mypipefd[2]; 
    int ret;
    char buf[20]; 

    ret = pipe(mypipefd); 
    if (ret == -1) {
        perror("pipe error"); 
        exit(-1); 
    }

    pid = fork(); 
    if (pid == 0) {
        // child process 
        printf("Child process\n"); 
        write(mypipefd[1], "Hello there!", 12); 
        printf("mypipe fd[1] = %d\n", mypipefd[1]); 
        printf("end child process\n"); 
    } else {
        // parent process 
        printf("parent process\n"); 
        read(mypipefd[0], buf, 15); 
        printf("buff: %s\n", buf); 
        printf("mypipefd[0] = %d\n", mypipefd[0]); 
        printf("end parent process"); 
    }
    
    return 0;
}