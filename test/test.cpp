#include <iostream>
#include <fstream>
using namespace std;

#define test_msg 1;

int main () {

    #if A && (B <= 1000)
        int test = 10;
    #endif

    #ifdef test_msg
	    ofstream myfile;
	    #ifdef test
           printf("hahahihihoho");
        #else
           int y = 0;
        #endif
    #endif

    myfile.open ("example.txt");

    myfile << "Writing this to a file.\n";
    myfile.close();

    #if foo
        int thisisatest = 0;
    #endif

  return 0;
}