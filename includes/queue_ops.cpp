/*
    This file is part of Syn-C.

    Syn-C is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Syn-C is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Syn-C.  If not, see <http://www.gnu.org/licenses/>.
*/

#include "queue_ops.h"
#include <stdio.h>


using namespace std;


queue_ops::queue_ops(){
    //Open log file for reading
    log_file = fopen("log.txt", "w+");
    fprintf(log_file, "Log file for ANSI C file produced by Syn-C\n");

    parser_obj.xml_reader();
}

queue_ops::~queue_ops(){
    fclose(log_file);
}

void queue_ops::push_into_queue(string entry, int direct_flag){
    //For direct_flag, if=1 => to be inserted directly into the output_queue | if=2 => value to be looked up in hash map | if=3 => A header file
    string map_value, rep_ret_s, header_ret_s;
    char *rep_ret, *header_ret, *line;
    int i;
    bool header_ret_flag;
       
    if(direct_flag == 1){
        input_queue.push(entry);

    }
    else if (direct_flag == 2)  //=> An identifier which cannot be stored directly
    {
        //Check for value in header hash map
        map_value= parser_obj.map_find(entry, 'f');
        if(map_value!="no"){
            header_ret_flag = 1;            
            line = new char [map_value.size() + 1] ;
            strcpy(line, map_value.c_str()) ;
            rep_ret= strtok(line, ";");
            header_ret = strtok(NULL, ";");
            rep_ret_s = string(rep_ret);
            header_ret_s = string(header_ret);
            input_queue.push(rep_ret_s);
            fprintf(log_file, "Replaced %s by %s\n", entry.c_str(), rep_ret_s.c_str());
            
            //Look if the header file already exists in the array.
            for (i = 0; i < count; ++i){
               if(header_array[i] == header_ret_s){
                    header_ret_flag = 0;
                    break;
               }
            }
            if(header_ret_flag == 1){
                header_array[count++]= header_ret_s;
            }
        }
            else
                input_queue.push(entry);
    }

    else if (direct_flag == 3)  //=> A header file
    {   
        int pos1, pos2;
        pos1= entry.find_first_of('<');
        pos2= entry.find_first_of('.');
        string filename= entry.substr(pos1+1, (pos2-pos1-1));
        map_value= parser_obj.map_find(filename, 'h');
        if(map_value== "true") {
            header_array[count++]= filename;
        }
    }    
}

string queue_ops::pop_from_queue(){
    popped_str = input_queue.front();
    input_queue.pop();
    return popped_str;
}

int queue_ops::generate_output_file(string path, string file) {
    string file_path(path), check_str ;
    int len;
    file_path+= "sync_"+file;
    output_file= fopen(file_path.c_str(), "w");
    if(!output_file)
        return -1;
    for(int i=0;i<count;i++) {
        string include_name("");
        include_name+= "#include <";
        include_name+= header_array[i];
        include_name+= ".h>\n";
        fputs(include_name.c_str(), output_file);
        fprintf(log_file, "Included %s", include_name.c_str());
    }
    while(!input_queue.empty()) {
        check_str= pop_from_queue();
        len= check_str.length();
        fputs(check_str.c_str(), output_file);
    }
    fclose(output_file);
    return 0 ;
}