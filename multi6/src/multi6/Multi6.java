package multi6;
 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Multi6 
{
    public static int Row;
    public static int Col;
    public static List <vector> list=new ArrayList<vector>();
    public static List <vector> vectors=new ArrayList <vector>();
    public static String compressListFilePath = "C:\\Users\\dell\\Desktop\\multi6\\compressed.txt";
    public static String compressAveragesListPath = "C:\\Users\\dell\\Desktop\\multi6\\compressedAverages.txt";
    public static int counter = 0;
    public static int sizeColumnsDividedByvectorCol;
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        int[][] example = {{1,2,7,9,4,11},
                          {3,4,6,6,12,12},
                          {4,9,15,14,9,9},
                          {10,10,20,18,8,8},
                          {4,3,17,16,1,4},
                          {4,5,18,18,5,6}};
        int [][]pixels = readImage("C:\\Users\\dell\\Desktop\\Multimedia5\\pic.jpg");
        get_ready(pixels,2,2);
        get_average();
        set_left_right_vectors(4);
        compresse(list,vectors);
        Reconstruct();
        int [][] arr=convertTo2D(list,2, 2);
        writeImage( arr,"C:\\Users\\dell\\Desktop\\outpic.jpg",Row,Col);
        //writeImage(int[][] pixels,String outputFilePath,int width,int height)
       /* for(int i=0;i<vectors.size();i++)
        {
            System.out.println("aver");
            for(int j=0;j<vectors.get(i).data.size();j++)
            {
                System.out.print(vectors.get(i).data.get(j)+ " ");
            }
            System.out.println("");
            System.out.println("Lists : ");
            for (int j=0;j<vectors.get(i).associated.size();j++)
            {
                for (int a=0;a<vectors.get(i).associated.get(j).data.size();a++)
                {
                    System.out.print(vectors.get(i).associated.get(j).data.get(a)+ " ");
                }
                System.out.println("");
            }
        }*/

      }
    
    public static int get_power(int level)
    {
        int pow = 0;
        for(int i=0;i<level;i++)
        {
            int power = (int) Math.pow(2, i);
            if(level == power)
            {
                pow = i;
                break;
            }
        }
        return pow;
    }
    public static int[][] readImage(String filePath)
    {
	int width=0;
        int height=0;
        File file=new File(filePath);
        BufferedImage image=null;
        try
        {
            image=ImageIO.read(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

          width=image.getWidth();
          height=image.getHeight();
        int[][] pixels=new int[height][width];

        for(int x=0;x<width;x++)
        {
            for(int y=0;y<height;y++)
            {
                int rgb=image.getRGB(x, y);
                int alpha=(rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;

                pixels[y][x]=r;
            }
        }

        return pixels;
    }
    
    public static void writeImage(int[][] pixels,String outputFilePath,int width,int height)
    {
        File fileout=new File(outputFilePath);
        BufferedImage image2=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );
        for(int x=0;x<width ;x++)
        {
            for(int y=0;y<height;y++)
            {
                image2.setRGB(x,y,(pixels[y][x]<<16)|(pixels[y][x]<<8)|(pixels[y][x]));
            }
        }
        try
        {
            ImageIO.write(image2, "jpg", fileout);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void get_ready(int [][]pixels,int row,int col)
    {
        //row wa col btoo3 el vector size
        //6*6 =36 36/2*2 =9 0 list hnkrythm
        int pixelsRow = pixels.length , pixelsCol = pixels[0].length;
        int increaseRow = 0 , increaseCol = 0;
        int mod = pixelsRow % row;
        if(mod != 0)
        {
            increaseRow = row - mod;
        }
        mod = pixelsCol % col;
        if(mod != 0)
        {
            increaseCol = col - mod;
        }
       
        
        pixelsRow += increaseRow;
        pixelsCol += increaseCol;
        Row=pixelsRow;
        Col=pixelsCol;
//        System.out.println(pixelsRow + "*" + pixelsCol);
        
        int [][]newPixels = new int[pixelsRow][pixelsCol];
        for(int i=0;i<pixelsRow;i++)
        {
            if(i<pixels.length)
            {
                for(int j=0;j<pixelsCol;j++)
                {
                     if(j<pixels[0].length)
                     {
                         newPixels[i][j] = pixels[i][j];
                     }
                     else
                     {
                         newPixels[i][j] = 0;
                     }
                }
            }
            else
            {
                for(int j=0;j<pixelsCol;j++)
                {
                    newPixels[i][j] = 0;
                }
            }
        }
        
        
        int no_list=newPixels.length;
        no_list=no_list*newPixels[0].length;
        no_list=no_list/(row*col);
        for (int i=0;i<no_list;i++)
        {
            vector v=new vector();
            list.add(v);
        }
        sizeColumnsDividedByvectorCol = newPixels[0].length / col;
        for (int i=0;i<newPixels.length;i++)
        {
            for(int a=counter;a<sizeColumnsDividedByvectorCol+counter;a++)
            {
                for (int j=0;j<newPixels[i].length;j+=col)
                {
                    for(int b=0;b<col;b++)
                    {
                        if(j+b<newPixels[i].length)
                        {
                            list.get(a).data.add(newPixels[i][j+b]);
                        }   
                    }
                    a++;
                }   
            }
            if(list.get((sizeColumnsDividedByvectorCol+counter)-1).data.size() == row*col)
            {
                counter += sizeColumnsDividedByvectorCol;
            }
        }
    }
    // b3d ma gebt awl average begeb el left wel right bto3o wa bdeefhm fy list el vectors
    public static void set_left_right_vectors(int codeBook)
    {
        List <Integer> first_average=new ArrayList <Integer> ();
        first_average=get_average();
        vector first_avg=new vector (first_average);
        
        vectors.add(first_avg);
        int power = get_power(codeBook);
        System.out.println("power = " + power);
        for(int a=0;a<power;a++)
        {
            List<vector> vectorsTemp = new ArrayList<vector>();
            for(int i = 0 ;i<vectors.size(); i++)
            {
                vector left=new vector();
                vector right=new vector();
                for(int j=0;j<vectors.get(i).data.size();j++)
                {
                    int avg_1=vectors.get(i).data.get(j)-1;
                    int avg_r=vectors.get(i).data.get(j)+1;
                    left.data.add(avg_1);
                    right.data.add(avg_r);
                }
                vectorsTemp.add(left);
                vectorsTemp.add(right);
            }
            vectors=new ArrayList <vector>();
            for(int i=0;i<vectorsTemp.size();i++)
            {
                vectors.add(vectorsTemp.get(i));
            }
            vector data=new vector ();
            int way=0;
            for (int i=0;i<list.size();i++)
            {
                way = which_way(list.get(i),vectors);
                vectors.get(way).associated.add(list.get(i));       
            }
            vectorsTemp = new ArrayList<vector>();
            for(int i= 0;i<vectors.size();i++)
            {
                vector avg = new vector();
                if(vectors.get(i).associated.size()!= 0)
                {
                    avg = get_averge_vector(vectors.get(i));
                    vectorsTemp.add(avg);
                }
            }

            
            vectors=new ArrayList <vector>();
            for(int i=0;i<vectorsTemp.size();i++)
            {
                vectors.add(vectorsTemp.get(i));
            }

        }
        
        fixed_itteration(list,vectors);
        
    }
    // bt2oly adeef el vector da feen
    public static int which_way(vector data , List <vector> Vvector)
    {
        int minm=0 , nextMinm = 0, index = 0;
        for (int j=0;j<data.data.size();j++)
        {
            minm += Math.pow((data.data.get(j)- Vvector.get(0).data.get(j)), 2); 
        }
        for (int i=1;i<Vvector.size();i++)
        {
            for (int j=0;j<data.data.size();j++)
            {
                nextMinm += Math.pow((data.data.get(j)- Vvector.get(i).data.get(j)), 2); 
            }
            if(minm > nextMinm)
            {
                minm = nextMinm;
                index = i;
            }
            nextMinm=0;
        }
        
        return index;
       
    }
    public static List <Integer> get_average()
    {
        List < Integer > fisrt_average=new ArrayList <Integer> ();
        for (int i=0;i<list.get(0).data.size();i++)
        {
            int avg=0;
            for (int j=0;j<list.size();j++)
            {
                avg+=(int)list.get(j).data.get(i);                
            }
            int avgr=avg/list.size();
            fisrt_average.add(avgr);
        }
        return  fisrt_average;
    }
    public static vector get_averge_vector(vector  v)
    {
        vector average= new vector();
        
        for (int i=0;i<v.associated.get(0).data.size();i++)
        {
            int avg=0;
            for (int j=0;j<v.associated.size();j++)
            {
                avg+=v.associated.get(j).data.get(i);
            }
            average.data.add(avg/v.associated.size());
        }
        return average;
    } 
    public static void fixed_itteration(List <vector> allVectors, List <vector> averageVector)
    {
        int Max_Itteration=4;
        for (int j=0;j<Max_Itteration;j++)
        {
            int way=0;
            List <vector> newAverages=new ArrayList <vector>();
            for (int i=0;i<allVectors.size();i++)
            {
                way = which_way(allVectors.get(i),averageVector);
                averageVector.get(way).associated.add(allVectors.get(i));       
            }
            for(int i= 0;i<averageVector.size();i++)
            {
                vector avg = new vector();
                if(averageVector.get(i).associated.size()!= 0)
                {
                    avg = get_averge_vector(averageVector.get(i));
                    newAverages.add(avg);
                }
            }
            //true if they are equal false if not
            boolean chk = true;
            for (int i=0;i<averageVector.size();i++)
            {
                for(int k=0;k<averageVector.get(i).data.size();k++)
                {
                    if(averageVector.get(i).data.get(j) != newAverages.get(i).data.get(j))
                    {
                        chk =false;
                        break;
                    }
                }
            }
            if(chk == false)
            {
                averageVector = new ArrayList<vector>();
                for(int i=0;i<newAverages.size();i++)
                {
                    averageVector.add(newAverages.get(i));
                }
            }
            if(chk == true)
            {
                break;
            }
        }
    }
    public static void Reconstruct() throws FileNotFoundException
    {
        list = new ArrayList<vector>();
        vectors = new ArrayList<vector>();
        File file = new File(compressListFilePath);
        String compressed = new Scanner(file.getAbsoluteFile()).useDelimiter("\\Z").next();
        String []texts=compressed.split("\\|");
        File file2= new File(compressAveragesListPath);
        String Averages=new Scanner(file2.getAbsoluteFile()).useDelimiter("\\Z").next();
        String []Avgs= Averages.split("\\$");
        List<Integer> compressedList = new ArrayList<Integer>();
        for (int i=0;i<texts.length;i++)
        {
            compressedList.add(Integer.parseInt(texts[i]));
        }
        for (int i=0;i<Avgs.length;i++)
        {
            String []avg=Avgs[i].split("\\|");
            vector v=new vector();
            for (int j=0;j<avg.length;j++)
            {
                v.data.add(Integer.parseInt(avg[j]));
            }
            vectors.add(v);   
        }
        for (int i=0;i<compressedList.size();i++)
        {
            list.add(vectors.get(compressedList.get(i)));
        }
//        for (int i=0;i<list.size();i++)
//        {
//            for (int j=0;j<list.get(i).data.size();j++)
//            {
//                System.out.print(list.get(i).data.get(j) + " ");
//            }
//            System.out.println();
//        }

        
    }
    public static void compresse(List <vector> allVectors , List <vector> lastAverage) throws FileNotFoundException, IOException
    {
        List<Integer> compressedList = new ArrayList<Integer>();
        for (int i=0;i<lastAverage.size();i++)
        {
            lastAverage.get(i).value=i;
        }
        for(int i=0;i<allVectors.size();i++)
        {
            vector avg = getAverageAccordingToList(allVectors.get(i), lastAverage);
            compressedList.add(avg.value);
        }
        
        FileOutputStream file=new FileOutputStream(compressAveragesListPath);
        PrintWriter writer= new PrintWriter(file);
        for (int i=0;i<lastAverage.size();i++)
        {
            for (int j=0;j<lastAverage.get(i).data.size();j++)
            {
                writer.write(String.valueOf(lastAverage.get(i).data.get(j)));
                writer.write("|");
            }
            writer.write("$");
        }
        writer.close();
        file.close();
        
        file=new FileOutputStream(compressListFilePath);
        writer= new PrintWriter(file);
        
        for(int i=0;i<compressedList.size();i++)
        {
            writer.write(String.valueOf(compressedList.get(i)));
            writer.write("|");
        }
        writer.close();
        file.close();
    
    }
    
    public static vector getAverageAccordingToList(vector smallList, List<vector> lastAverage)
    {
        vector selectedList = null;
        for(int i=0;i<lastAverage.size();i++)
        {
            for(int j=0;j<lastAverage.get(i).associated.size();j++)
            {
                int counter = 0;
                for(int k=0;k<lastAverage.get(i).associated.get(j).data.size();k++)
                {
                    if(lastAverage.get(i).associated.get(j).data.get(k) == smallList.data.get(k))
                    {
                        counter++;
                    }
                }
                
                if(counter == lastAverage.get(i).associated.get(j).data.size())
                {
                    selectedList = lastAverage.get(i);
                    return selectedList;
                }
            }
        }
        return selectedList;
    }
    public static int [][] convertTo2D(List <vector> allVectors,int row,int col)
    {
        int [][] array = new int[Row][Col];
        counter=0;
        String str = "";
        while(true)
        {
            for(int i=0;i<col*row;i+=col)
            {
                for(int a=counter;a<sizeColumnsDividedByvectorCol+counter;a++)
                {
                    for(int j=i;j<col+i;j++)
                    {
                        str += String.valueOf(list.get(a).data.get(j));
                        str +="|";
                    }
                }
                str += "$";
            }
            counter += sizeColumnsDividedByvectorCol;
            if(counter>=list.size())
            {
                break;
            }
        }
        String []arr=str.split("\\$");
        for (int i=0;i<arr.length;i++)
        {
            String []arr2=arr[i].split("\\|");
            for (int j=0;j<arr2.length;j++)
            {
                array[i][j]=Integer.parseInt(arr2[j]);
            }
        }
//        for (int i=0;i<array.length;i++)
//        {
//            for (int j=0;j<array[i].length;j++)
//            {
//                System.out.print(array[i][j] + " ");
//            }
//            System.out.println();
//        }
    return array;
        
    }
}
