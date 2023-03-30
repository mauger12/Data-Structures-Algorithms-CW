import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Driver {
    public static void main( String[] args ){
        Scanner scanner = new Scanner(System.in);
        clearScreen();
        System.out.println("enter c to compress or d to decompress, default is compress if neither is pressed");
        String smallOrBig = scanner.nextLine();
        String inputFile;
        String outputFile;
        Node tree;
        String treeInput;
        Compression compressor = new Compression();

        if ("d".equals(smallOrBig)) {
            System.out.println("Enter file to be decompressed in form \"filename.bin\"");
            inputFile = scanner.nextLine();
            outputFile = inputFile.substring(0, inputFile.length() - 4) + "-U.txt";

            System.out.println("Enter tree to use for decompression in form \"filename.ser\". if no tree is selected then decompression will fail");
            treeInput = scanner.nextLine();

            if (Pattern.matches("(.*).ser", treeInput)) {
                //load tree
                try {
                    // Reading the object from a file
                    FileInputStream file = new FileInputStream(treeInput);
                    ObjectInputStream in = new ObjectInputStream(file);

                    // Method for deserialization of object
                    tree = (Node) in.readObject();
                    compressor.setTree(tree);

                    in.close();
                    file.close();

                    System.out.println("Tree has been loaded");
                } catch (IOException ex) {
                    System.out.println("Tree not found, ending program");
                    System.exit(0);
                    tree = createTree(compressor, inputFile, treeInput);
                } catch (ClassNotFoundException ex) {
                    System.out.println("Node class doesnt exist");
                    System.exit(0);
                    tree = createTree(compressor, inputFile, treeInput);
                }
            } else {
                System.out.println("no tree selected, decompression failed");
                System.exit(0);
                tree = createTree(compressor, inputFile, treeInput);
            }

            //load tree or fail
            compressor.generateBinary(tree, "");
            decompressFile(compressor,inputFile,outputFile,treeInput);
        }
        else {
            System.out.println("Enter file to be compressed in form \"filename.txt\"");
            inputFile = scanner.nextLine();
            outputFile = inputFile.substring(0, inputFile.length() - 4) + "-C.bin";

            System.out.println("Enter tree to use for compression in form \"filename.ser\". if entered otherwise new tree will be generated");
            treeInput = scanner.nextLine();

            if (Pattern.matches("(.*).ser", treeInput)) {
                //load tree
                try {
                    // Reading the object from a file
                    FileInputStream file = new FileInputStream(treeInput);
                    ObjectInputStream in = new ObjectInputStream(file);

                    // Method for deserialization of object
                    tree = (Node) in.readObject();
                    compressor.setTree(tree);

                    in.close();
                    file.close();

                    System.out.println("Tree has been loaded");
                } catch (IOException ex) {
                    System.out.println("Tree not found, generating new tree");
                    tree = createTree(compressor, inputFile, treeInput);
                } catch (ClassNotFoundException ex) {
                    System.out.println("this will literally never happen but has to go in anyways :D");
                    tree = createTree(compressor, inputFile, treeInput);
                }
            } else {
                tree = createTree(compressor, inputFile, inputFile.substring(0,inputFile.length()-4));
            }
            //load tree or generate if no tree selected
            compressor.generateBinary(tree, "");
            compressFile(compressor, inputFile, outputFile, treeInput +".ser");
        }
    }

    public static Node createTree(Compression compressor, String inputFile, String treeName){
        Node tree;

        FileInputStream in = null;
        Reader reader;
        try{
            in = new FileInputStream(inputFile);
            reader = new InputStreamReader(in);
            int byteRead;
            while ((byteRead = reader.read()) != -1) {
                compressor.addCharacter(String.valueOf(byteRead));
                //take each letter and add to hashmap of letters
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        finally {//always close the streams
            try {
                if (in != null) in.close();
            }
            catch (IOException ex) {
                System.out.println("Fail to close files.");
            }
        }

        compressor.generateForest();
        tree = compressor.generateTree(treeName);
        return tree;
    }

    public static void compressFile(Compression compressor, String inputFile, String outputFile, String treeFile){
        FileInputStream in = null;
        OutputStream outputStream = null;
        Reader reader;
        String binaryString;
        StringBuilder bitString = new StringBuilder();
        StringBuilder backupBitString = new StringBuilder();
        int byteRead;
        double percentageDiff;
        byte outputByte[];
        long startTime;
        long endTime;
        long totalTime;
        //set variables and such

        //try compress inputFile into outputFile
        try{
            in = new FileInputStream(inputFile);
            reader = new InputStreamReader(in);
            outputStream = new FileOutputStream(outputFile);
            System.out.println();
            System.out.println("Initial file size: " + new File(inputFile).length() + " Bytes!");
            startTime = System.nanoTime();

            boolean announce = true;
            while ((byteRead = reader.read()) != -1) {
                //take each letter, get its binary representation and add it to string of binary
                try {
                    bitString.append(compressor.getBinary(byteRead));
                }
                catch(OutOfMemoryError er){
                    if(announce){
                        System.out.println(bitString.length());
                        announce = false;
                    }
                    backupBitString.append(compressor.getBinary(byteRead));
                    //if file is too big, string builder gets full so need more space
                    System.out.println(backupBitString.length());
                }
            }

            binaryString = bitString.toString()+backupBitString.toString();
            outputByte = GetByteBinary(binaryString);
            outputStream.write(outputByte);
            endTime = System.nanoTime();
            totalTime = endTime - startTime;
            percentageDiff = Math.round((((double)new File(inputFile).length() - (new File(outputFile).length() + new File(treeFile).length())) / (double)new File(inputFile).length()) * 10000) / 100.0;
            System.out.println("File compressed in: " + ((double)totalTime)/1000000 + " MilliSeconds!");
            System.out.println("Final file and tree size: " + (new File(outputFile).length() + new File(treeFile).length()) + " Bytes!");
            System.out.println("File compressed by: " + percentageDiff + "%!");
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Failed to compress!");
        } finally {//always close the streams
            try {
                if (in != null) in.close();
                if(outputStream!=null) outputStream.close();
            }
            catch (IOException ex) {
                System.out.println("Failed to close files.");
            }
        }
    }

    public static void decompressFile(Compression compressor, String inputFile, String outputFile, String treeFile){
        OutputStream outputStream = null;
        byte[] allBytes;
        String binaryString;
        StringBuilder characterBinary = new StringBuilder();
        StringBuilder finalText = new StringBuilder();
        char character;
        long startTime;
        long endTime;
        long totalTime;
        try {
            outputStream = new FileOutputStream(outputFile);
            allBytes = Files.readAllBytes(Paths.get(inputFile));
            binaryString = GetString(allBytes);
            //read the compressed file into a string

            System.out.println();
            System.out.println("Compressed file and tree size: " + (new File(inputFile).length() + new File(treeFile).length()) + " Bytes!");
            startTime = System.nanoTime();

            //for each binary digit, add it to a new string and check if thats a character or not
            //only a thousandth of the file is decompressed to check it worked quickly, remove /1000 to decompress the whole file
            for (int i = 0; i < (binaryString.length()); i++) {
                characterBinary.append(binaryString.toCharArray()[i]);
                //if a character matches, write he uncompressed character to file
                if(compressor.isBinaryValid(characterBinary.toString())){
                    character = (compressor.getChar(characterBinary.toString())).toCharArray()[0];
                    finalText.append(character);
                    characterBinary = new StringBuilder();
                }
            }

            byte[] pog = finalText.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(pog);

            endTime = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("File decompressed in: " + ((double)totalTime)/1000000000 + " Seconds!");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {//always close the streams
            try {
                if(outputStream!=null) outputStream.close();
            }
            catch (IOException ex) {
                System.out.println("Failed to close files.");
            }
        }
        System.out.println("Decompressed file size: " + new File(outputFile).length() + " Bytes!");
    }

    static String GetString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public static byte[] GetByteBinary(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        s = sBuilder.toString();

        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }

    public static void clearScreen() {
        try {
            if(System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex){ }
        //clear terminal
    }
}