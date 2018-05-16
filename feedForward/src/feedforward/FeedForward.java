package feedforward;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author Basem Elsayed
 */
public class FeedForward {

    public static void main(String[] args) throws IOException {
        
        int [][] pixels = readImage("C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\cameraMan.jpg");
        Compress(pixels,125,125,5);
        Decompress(125,125);
    }
    
    public static void Compress(int [][] pixels, int row , int col, int bits) throws FileNotFoundException, IOException
    {
        List <Integer> pixelOneDiemension = new ArrayList<>();
        List <Integer> differences = new ArrayList<>();
        List <Node> Table = new ArrayList<>();
        List <Integer> compressed = new ArrayList<>();
        
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                pixelOneDiemension.add(pixels[i][j]);
            }
        }
        
        differences.add(pixelOneDiemension.get(0));
        for(int i=1; i<pixelOneDiemension.size() ;i++)
        {
            differences.add(pixelOneDiemension.get(i) - pixelOneDiemension.get(i-1));
        }
        /*for(int i=0;i<differences.size();i++)
        {
            System.out.print(differences.get(i) + "  ");
        }
        System.out.println("");*/
        
        int maxDifference = Collections.max(differences);
        int minDifference = Collections.min(differences);
        int step = (int) ((maxDifference - minDifference)/Math.pow(2, bits));
        System.out.println("Max = " + maxDifference);
        System.out.println("Min = " + minDifference);
        System.out.println("Step = " + step);
        
        Node node = new Node();
        node.lowRange = minDifference;
        node.HighRange = node.lowRange + step;
        node.quantization = 0;
        node.qMinusOne = (node.lowRange + node.HighRange) / 2;
        Table.add(node);
        for (int i=1;i<Math.pow(2,bits);i++)
        {
            node = new Node();
            node.lowRange = Table.get(i - 1).HighRange;
            node.lowRange += 1;
            node.HighRange = node.lowRange + step;
            node.quantization = i;
            node.qMinusOne = ((node.lowRange + node.HighRange)/2);
            Table.add(node);
        }
        /*
        for(int i=0; i<Table.size() ;i++)
        {
            System.out.println(Table.get(i).quantization + "  " + Table.get(i).lowRange + " => " + Table.get(i).HighRange + "  " + Table.get(i).qMinusOne);
        }*/
        
        compressed.add(differences.get(0));
        for(int i =1 ; i<differences.size() ; i++)
        {
            compressed.add(checkRange(differences.get(i), Table));
        }
        
        /*for(int i=0;i<compressed.size();i++)
        {
            System.out.print(compressed.get(i) + "  ");
        }
        System.out.println("");*/
        
        FileOutputStream output = new FileOutputStream(new File("C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\compressedData.txt"));
        for(int i=0;i<compressed.size();i++)
        {
            output.write(compressed.get(i));
        }
        output.close();
        
        File file=new File("C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\compressedTable.txt");
        PrintWriter writer = new PrintWriter(file);
        for(int i=0;i<Table.size();i++)
        {
            String str=new String (Table.get(i).lowRange + "|" +Table.get(i).HighRange);
            writer.write(str);
            writer.write("$");
        }
        writer.close();
        
    }
    
    
    public static void Decompress(int row , int col) throws FileNotFoundException, IOException
    {
        List <Node> Table = new ArrayList<>();
        List <Integer> decompressed = new ArrayList<>();
        List <Integer> deQuantization = new ArrayList<>();
        List <Integer> decoded = new ArrayList<>();
        
        String tableFileText =new Scanner(new File("C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\compressedTable.txt")).useDelimiter("\\Z").next();
        String []rows=tableFileText.split("\\$");
        
        for (int i=0;i<rows.length;i++)
        {
            String []rowss = rows[i].split("\\|");
            Node node = new Node();
            node.lowRange = Integer.parseInt(rowss[0]);
            node.HighRange = Integer.parseInt(rowss[1]);
            node.qMinusOne = (node.lowRange + node.HighRange)/2;
            Table.add(node);
            for (int j=2;j<rowss.length;j+=2)
            {
                Node temp = new Node();
                temp.lowRange = Integer.parseInt(rowss[j]);
                temp.HighRange = Integer.parseInt(rowss[j+1]);
                temp.qMinusOne = (temp.lowRange + temp.HighRange)/2;
                Table.add(temp);
            }          
        }
        for(int i=0; i<Table.size() ;i++)
        {
            Table.get(i).quantization = i;
            //System.out.println(Table.get(i).quantization + "  " + Table.get(i).lowRange + " => " + Table.get(i).HighRange + "  " + Table.get(i).qMinusOne);
        }
        
        FileInputStream input = new FileInputStream(new File("C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\compressedData.txt"));
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                decompressed.add(input.read());
            }   
        }
        input.close();
        
        deQuantization.add(decompressed.get(0));
        for(int i=1;i<decompressed.size();i++)
        {
            deQuantization.add(Table.get(decompressed.get(i)).qMinusOne);
        }
        
        decoded.add(deQuantization.get(0));
        for(int i=1; i<deQuantization.size() ;i++)
        {
            int decode = decoded.get(i-1)+deQuantization.get(i);
            if ( decode < 0 )
            {
                decoded.add(0);
            }
            else if(decode > 256)
            {
                decoded.add(255);
            }
            else
            {
                decoded.add(decoded.get(i-1) + deQuantization.get(i));
            } 
        }
        
        
        int [][] result = new int [row][col];
        int counter = 0;
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<col;j++)
            {
                result[i][j] = decoded.get(counter);
                counter++;
            }
        }
        
        writeImage(result, "C:\\Users\\Basem Elsayed\\Desktop\\feedForward\\cameraManOut.jpg");
        
    }
    
    
    
    public static int checkRange(int number , List <Node> table)
    {
        int index = -1;
        for (int i=0;i<table.size();i++)
        {
            if (table.get(i).lowRange <= number && number <= table.get(i).HighRange )
            {
               index = table.get(i).quantization;
               break;
            }
        }
        return index;
    }
    
    
    public static int[][] readImage(String path)  
    {
        BufferedImage img;
        try 
        {
            img = ImageIO.read(new File(path));
            int hieght = img.getHeight();
            int width = img.getWidth();
            int[][] imagePixels = new int[hieght][width];
            for (int x = 0; x < width; x++) 
            {
                for (int y = 0; y < hieght; y++) 
                {
                    int pixel = img.getRGB(x, y);
                    int red = (pixel & 0x00ff0000) >> 16;
                    int grean = (pixel & 0x0000ff00) >> 8;
                    int blue = pixel & 0x000000ff;
                    int alpha = (pixel & 0xff000000) >> 24;
                    imagePixels[y][x] = red;
                }
            }

            return imagePixels;
        } 
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            return null;
        }
    }
    
    public static void writeImage(int[][] imagePixels, String outPath) 
    {
        BufferedImage image = new BufferedImage(imagePixels.length, imagePixels[0].length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < imagePixels.length; y++) 
        {
            for (int x = 0; x < imagePixels[y].length; x++) 
            {
                int value = -1 << 24;
                value = 0xff000000 | (imagePixels[y][x] << 16) | (imagePixels[y][x] << 8) | (imagePixels[y][x]);
                image.setRGB(x, y, value);
            }
        }
        File ImageFile = new File(outPath);
        try 
        {
            ImageIO.write(image, "jpg", ImageFile);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

    }
   
}
