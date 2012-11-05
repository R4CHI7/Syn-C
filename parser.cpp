#include<iostream>
#include<fstream>
#include<stdio.h>
#include<cstring>
#include<queue>
#include "modules/modules.h"

using namespace std ;

ifstream in ;
ofstream out ;						//File Pointers


class parser
{
private: 
	
	
public:
	
	parser()
	{
		  
	}
	
	void parseLineFromQueue(queue<char *> &inputLine)					//Replace words in queue
	{
		char *word ;
		while(!inputLine.empty())
		{
			  word = inputLine.front() ;
			  out << word << " " ;
			  inputLine.pop() ;
		}
	}
	
};

int main(int argc, char **argv)
{	
	string sline ;						//Line read from file
	char *line, *word ;
	
	queue<char *> inputLine ;				//Queue in which the words are stored
	
	in.open("input.c",ios::in) ;
	out.open("output.c",ios::out) ;				//File Name to be REVISED!
	
	parser obj ;						//Class Object
	
	while(!in.eof())
	{
		getline(in, sline) ;
		line = new char [sline.size() + 1] ;
		strcpy(line, sline.c_str()) ;
		word = strtok(line, " ") ;
		while(word != NULL)
		{
			inputLine.push(word) ;
			word = strtok(NULL, " ") ;
		}
		obj.parseLineFromQueue(inputLine) ;
		out << "\n" ;
	}
	
	in.close() ;
	out.close() ;
	return 0;
	
}