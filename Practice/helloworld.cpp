#include <iostream>    //for cout, endl
#include <unistd.h>    //for fork, pipe
#include <stdlib.h>    //for exit
#include <sys/wait.h>  //for wait
using namespace std;

int main(int argc, char *argv[])
{
      cout << "Hello, World" << endl; 
      cerr << "Error!!!" << endl;
	  int x;
	  cout << "Please type a number" << endl;
	  cin >> x;
	  cout << "You typed " << x << " Twice that is: " << (2 * x) << endl;
}
