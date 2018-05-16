package multi7;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Multi7 
{
    public static String table_path="C:\\Users\\Basem Elsayed\\Desktop\\multi7 (1)\\multi7\\multi7\\table.txt";
    public static String img_path="C:\\Users\\Basem Elsayed\\Desktop\\multi7 (1)\\multi7\\multi7\\cameraMan.jpg";
    public static String compressed_path="C:\\Users\\Basem Elsayed\\Desktop\\multi7 (1)\\multi7\\multi7\\compressed.txt";
    public static String decompressed_path="C:\\Users\\Basem Elsayed\\Desktop\\multi7 (1)\\multi7\\multi7\\decompressed.txt";
    public static String out_img_path="C:\\Users\\Basem Elsayed\\Desktop\\multi7 (1)\\multi7\\multi7\\outpic.jpg";
    public static int h;
    public static int w;
    public static int first;
    public static ArrayList<Integer> original;
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        GUI gui=new GUI();
        gui.setVisible(true); 
      /*  int [][] pixels = readImage(img_path);
        h=get_image_hight(img_path);
        w=get_image_width(img_path);
        //h = 2;
        //w = 3;
        //int [][] pixels = {{15,16,24},{33,44,68}};
        original=Convert1D(pixels);
        compress(pixels,8);
        int [][]dec=Decompressed();
    writeImage(dec, out_img_path) ;
*/
    }
   public static ArrayList<Integer> Convert1D(int[][] pixels)
   {
        ArrayList<Integer> pixel = new ArrayList<Integer>();
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pixel.add(pixels[i][j]);
            }
        }
        first=pixel.get(0);
        return pixel;
    }
    public static int get_range(int number,List <row> table)
    {
        int j=-1;
        for (int i=0;i<table.size();i++)
        {
            if (table.get(i).low  <= number && number <=(table.get(i).high) )
            {
               j=table.get(i).q;
               return j;
            }
        }
        return j;
    }
    
    public static void compress(int [][]pixels, int bits , int h , int w) throws FileNotFoundException, IOException
    {
        ArrayList<Integer> converted=Convert1D(pixels);
        int[] diff = Get_Diff(converted);
        Set_Table(diff ,bits);
        List<row> table=Get_Table();
        for(int i=0; i<table.size() ;i++)
        {
            table.get(i).q = i;
        }
        int []compressed=new int [diff.length];
        compressed[0]=first;
        for (int i=1;i<diff.length;i++)
        {
            int quantized = get_range(diff[i],table);
            compressed[i]= quantized;
        }
        int[][] comp=Convert2D(compressed,h,w);
        File file = new File(compressed_path);
        FileOutputStream output = new FileOutputStream(file);
        for(int i =0; i<h ;i++)
        {
            for(int j=0; j<w ;j++)
            {
                output.write(comp[i][j]);
            }
        }
        output.close();
    }
    
    public static int[][] Decompressed(int h, int w) throws FileNotFoundException, IOException
    {
        int [][]compressed = new int[h][w];
        File file = new File(compressed_path);
        FileInputStream input = new FileInputStream(file); 
                
        for(int i=0; i<h ;i++)
        {
            for(int j=0;j<w;j++)
            {
                compressed[i][j] = input.read();
            }
        }
        input.close();
        ArrayList<Integer> comp=Convert1D(compressed);
        int []dequntized=new int [comp.size()];
        int []decoded=new int [comp.size()];
        List<row> table=Get_Table();
        
        
        for(int i=0; i<table.size() ;i++)
        {
            table.get(i).q = i;
        }
        decoded[0]=first;
        dequntized[0]=first;
        for (int i=1; i<comp.size() ;i++)
        {
            int q_1 = (int)table.get(comp.get(i)).q_1;
            dequntized[i] = q_1;
        }
        for (int i=1;i<comp.size();i++)
        {
            if (decoded[i-1]+dequntized[i] <0 )
                decoded[i]=0;
            else if (decoded[i-1]+dequntized[i] > 256)
                decoded[i]=256;
            else
            decoded[i]=decoded[i-1]+dequntized[i]; 
        }
        int [][] decomp=new int [h][w];
        decomp=Convert2D(decoded,h,w);
        File f=new File(decompressed_path);
        FileOutputStream output=new FileOutputStream(f);
        
        
        for (int j = 0; j < h; j++) 
        {
            for (int i = 0; i < w; i++)
            {
                output.write(decomp[j][i]);
            }
        }
        return decomp;
    }
    public static int[][] Convert2D(int[] data, int h, int w)
    {
        int[][] converted = new int[h][w];
        int count=0;
        for(int i=0;i<h;i++)
        {
            for(int j=0;j<w;j++)
            {
                converted[i][j]=data[count];
                count++;
            }
        }
        return converted;
    }

    public static List<row> Get_Table() throws FileNotFoundException
    {
        String tableFileText =new Scanner(new File(table_path)).useDelimiter("\\Z").next();
        String []rows=tableFileText.split("\\$");
        List <row> table=new ArrayList<row>();
        for (int i=0;i<rows.length;i++)
        {
            String []rowss=rows[i].split("\\|");
            row r1=new row();
            r1.low=Integer.parseInt(rowss[0]);
            r1.high=Integer.parseInt(rowss[1]);
            r1.q_1=(r1.low+r1.high)/2;
            table.add(r1);
            for (int j=2;j<rowss.length;j+=2)
            {
                row r=new row();
                r.low=Integer.parseInt(rowss[j]);
                r.high=Integer.parseInt(rowss[j+1]);
                r.q_1=(r.low+r.high)/2;
                table.add(r);
            }          
        }
        return table;
    }
    
    public static int[] Get_Diff( ArrayList<Integer> data) 
    {
        int[] diffrence = new int[data.size()];
        diffrence[0]=first;
        for (int i = 1; i < data.size(); i++)
        {
            diffrence[i] = (int) Math.ceil(data.get(i) - data.get(i-1));
        }
        return diffrence;
    }
    public static void Set_Table( int[] diffrence ,int bits ) throws FileNotFoundException
    {
        List <row> table=new ArrayList<row>();
        int step= (int)(((getMaxValue(diffrence)) - ((getMinValue(diffrence)) ))/Math.pow(2,bits));
        row r1=new row();
        r1.low=getMinValue(diffrence);
        r1.high=r1.low+step;
        r1.q=0;
        r1.q_1=(r1.low+r1.high)/2;
        table.add(r1);
        for (int i=1;i<Math.pow(2,bits);i++)
        {
            row r = new row();
            r.low = table.get(i - 1).high;
            r.low += 1;
            r.high = r.low + step;
            r.q=i;
            r.q_1=((r.low+r.high)/2);
            table.add(r);
        } 
        File file=new File(table_path);
        PrintWriter write_file = new PrintWriter(file);
        for(int i=0;i<table.size();i++)
        {
            String tableStr=new String (table.get(i).low + "|" +table.get(i).high);
            write_file.write(tableStr);
            write_file.write("$");
        }
        write_file.close();
    }
    public static int get_image_hight(String path) throws IOException
    {
        BufferedImage img;
        img = ImageIO.read(new File(path));
        return (img.getHeight());
    }
    public static int get_image_width(String path) throws IOException
    {
        BufferedImage img;
        img = ImageIO.read(new File(path));
        return (img.getWidth());
    }
    
    public static int getMaxValue(int[] numbers) 
    {
        int maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

    public static int getMinValue(int[] numbers) {
        int minValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                minValue = numbers[i];
            }
        }
        return minValue;
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

