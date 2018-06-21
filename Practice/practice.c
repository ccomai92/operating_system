#include <stdio.h>   // for printf
#include <stdlib.h>  // for exit 
#include <unistd.h>  // for fork, execlp
#include <sys/wait.h>   //wait

int main(int argc, char *argv[])
{
   pid_t childPIDorZero = fork(); 
   if (childPIDorZero < 0) {
       perror("for() error"); 
       exit(-1); 
   }

   if (childPIDorZero != 0) {
       printf("I'm the parent %d, my child is %d\n", getpid(), childPIDorZero);
       wait(NULL); // wait for child process to join with this parent 
       printf("done"); 

   } else {
       printf("I'm the child %d, my parent is %d\n", getpid(), getppid()); 
       execl("/bin/echo", "echo", "Hello, World", NULL); 
   }


   return 0; 
}
