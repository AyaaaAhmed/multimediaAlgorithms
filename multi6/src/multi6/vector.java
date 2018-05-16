package multi6;

import java.util.ArrayList;
import java.util.List;

public class vector 
{
    vector left;
    vector right;
    List <Integer> data = new ArrayList <Integer> ();
    List <vector> associated = new ArrayList <vector> ();
    int value;
    public vector ()
    {}
    public vector(vector l ,vector r, List <Integer> d)
    {
        left=l;
        right=r;
        data=d;
    }
    public vector (List <Integer> d)
    {
        data=d;
    }
    
    
    
    
    
//    int width;
//    int height;
//    double[][] data;
//    public vector() 
//    {}
//    public vector(int width, int height) 
//    {
//        this.width = width;
//        this.height = height;
//        this.data = new double[height][width];
//    }
//    public int getWidth()
//    {
//        return width;
//    }
//    public void setWidth(int width) 
//    {
//        this.width = width;
//    }
//    public int getHeight() 
//    {
//        return height;
//    }
//    public void setHeight(int height) 
//    {
//        this.height = height;
//    }
//    public double[][] getData()
//    {
//        return data;
//    }
//    public void setData(double[][] data)
//    {
//        this.data = data;
//    }    
}
