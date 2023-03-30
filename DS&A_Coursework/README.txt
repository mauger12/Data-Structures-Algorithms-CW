Hi :)

My source .java and .class files are stored in the src folder.
In testArea folder is a huffman.jar file and a copy of SteamedHams.txt.
The jar file is a jar of my code that can be run in cmd.
To run the jar, the text file/ any other relevant files must be in the same folder and will be created in said folder.
See the video to see how to compress and decompress the SteamedHams.txt provided

to run the program:
*open cmd
*cd to the testArea folder
*java -jar huffman.jar
*press either c or d to select compression or decompression respectively

-if c pressed-
*enter the textfile you wish to compress {SteamedHams.txt}
*enter data tree you wish to compress with {if first time press any key} {else SteamedHams.ser}
*it will work its magic, print out stats and leave in the folder your compressed file {SteamedHams-C.bin}
*the file name is suffixed with -C to show its been compressed

-if d pressed-
*enter the .bin file you wish to decompress {SteamedHams-C.bin}
*enter the tree you wish to decompress with {SteamedHams.ser}
*it will work its magic, print out stats and leave in the folder your decompressed file {SteamedHams-C-U.bin}
*the file name is suffixed with -U to show its been decompressed (u for uncompressed?)

In the code for decompression, because it takes a long time, it is set to only decompress 1/1000th of the code. 
This needs changing to decompress more of the file