/*
* example program which shows usage of a pipe for communication
*/

#include <stdlib.h>  //exit
#include <stdio.h>   //perror
#include <unistd.h>  //fork, pipe
#include <sys/wait.h>   //wait
#include <iostream>
#include <string.h>
using namespace std;

int main()
{
   enum {READ, WRITE};
   pid_t pid;
   int pipeFD[2];

   if (pipe(pipeFD) < 0)
   {
      perror("Error in creating pipe");
      exit(EXIT_FAILURE);
   }

   pid = fork();
   if (pid < 0)
   {
      perror("Error during fork");
      exit(EXIT_FAILURE);
   }

   if (pid == 0)  //Child
   {
      close(pipeFD[WRITE]);      

      char buf[256];
      int n = read(pipeFD[READ], buf, 256);
	  buf[n] = '\0';
      cout << buf << endl << "Child read from parent." << endl;
      sleep(2);
   }
   else   //Parent 
   {
      close(pipeFD[READ]);
 
      write(pipeFD[WRITE], "News from the parent:  Hello!!\n", strlen("News from the parent:  Hello!!\n"));
           
      wait( NULL );
      cout << "Parent exiting" <<endl;
   }
   exit(EXIT_SUCCESS);
} 
