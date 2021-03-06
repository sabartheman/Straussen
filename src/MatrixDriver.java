import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
//this class is designed to do matrix math with the brute force technique and straussens algorithm


public class MatrixDriver {
    
    public static long bruteTime, optimizedTime,straussen2nTime;
    
    public static final int LOOP = 11;
    
    public static Random ran = new Random();
    
    public static void main(String[] args) throws FileNotFoundException {
        
        
        double[] timeR = new double[LOOP-1];
        double[] timeB = new double[LOOP-1];
        int[] powertwo = new int[LOOP-1];
        int S=0;
        for(int u = 0; u<10;u++){
            for(int l = 1;l<LOOP;l++){
                int power=1;
                for(int b = 0;b<l;b++){
                    power = 2*power;
                }

            int[][] third  = new int[power][power];
            int[][] fourth = new int[power][power]; 
            
            S=1;
            for(int b = 0;b<u;b++){
                S = 2*S;
            }
            
            for(int i =0;i<third.length;i++){
                for(int j=0;j<third.length;j++){
                    third[i][j]  = ran.nextInt(3);
                    fourth[i][j] = ran.nextInt(3);
                }
            }    

            //a recursive run of multiplication 2x2
            long timeS           = System.nanoTime();
            int[][] result3 = recursive(third,fourth,S);
            long timeF           = System.nanoTime();
            optimizedTime   = timeF-timeS;
            //converting nano seconds to seconds
            double timeSeconds = optimizedTime/1000000000.000000;
            timeR[l-1] = timeSeconds;
            System.out.println("operating time for size "+power+" is "+timeSeconds + " seconds\nThe value of s for this is "+S +"\n");
            /*
            //brute force run
            timeS = System.nanoTime();
            int[][] result4 =  multiply(third,fourth);
            timeF = System.nanoTime();
            bruteTime = timeF-timeS;
            timeSeconds = bruteTime/1000000000.000000;
            timeB[l-1] = timeSeconds;
            System.out.println("operating time for brute force multiplying of size " + power + " is " + timeSeconds + " seconds\n");
            */
            powertwo[l-1] = power;

            /*
            for(int i =0;i<third.length;i++){
                for(int j=0;j<fourth.length;j++){
                    System.out.print("[" + result3[i][j] + "]");
                }
                System.out.println();
            }
            System.out.println();
            for(int i =0;i<third.length;i++){
                for(int j=0;j<fourth.length;j++){
                    System.out.print("[" + result4[i][j] + "]");
                }
                System.out.println();
            }*/

            }
            String csvname = "time".concat(String.valueOf(u)).concat(".csv");
            
            
        //will output 10 csv files to director above src
        writeToFile(timeR,timeB,powertwo,S,csvname);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // Brute force method to multiply any NxN matrix together
    ///////////////////////////////////////////////////////////////////////////
    
    
    public static int[][] multiply(int[][] a, int[][] b) {
        int rA = a.length;    // rows in a
        int cA = a[0].length; // columns of a which is same as rows in B
        int cB = b[0].length; // columns of b
        int[][] c = new int[rA][cB];
        for(int i = 0; i < rA; i++){
            for(int j = 0; j < cB; j++){
                for(int k = 0; k < cA; k++){
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }



    ///////////////////////////////////////////////////////////////////////////
    // straussens algorithm for multiplying two same size matrices
    // everything below this is for the straussen method
    ///////////////////////////////////////////////////////////////////////////
    public static int[][] recursive(int[][] a, int[][] b, int svalue){
        int n = a.length;

        int[][] result = new int[n][n];
        //deals with odd sized arrays
        if((n%2 != 0 ) && (n !=1)){
            int[][] a1, b1, c1;
            int n1 = n+1;
            a1 = new int[n1][n1];
            b1 = new int[n1][n1];
            c1 = new int[n1][n1];

            for(int i=0; i<n; i++)
                for(int j=0; j<n; j++)
                {
                a1[i][j] =a[i][j];
                b1[i][j] =b[i][j];
                }
            c1 = recursive(a1, b1,svalue);
            for(int i=0; i<n; i++)
                for(int j=0; j<n; j++)
                result[i][j] =c1[i][j];
            return result;
        }
        ////////////////////
        if(n == 1)
        {
                result[0][0] = a[0][0] * b[0][0];
        }else if(svalue >= n){
            result = multiply(a,b);
        }
        ////normal 2^n straussen method here
        else{


        int[][] A00 = new int[n/2][n/2];
        int[][] A01 = new int[n/2][n/2];
        int[][] A10 = new int[n/2][n/2];
        int[][] A11 = new int[n/2][n/2];

        int[][] B00 = new int[n/2][n/2];
        int[][] B01 = new int[n/2][n/2];
        int[][] B10 = new int[n/2][n/2];
        int[][] B11 = new int[n/2][n/2];
        //splitting up the matrices
        divide(a, A00, 0  ,0  );
        divide(a, A01, 0  ,n/2);
        divide(a, A10, n/2,0  );
        divide(a, A11, n/2,n/2);

        divide(b, B00, 0  ,0  );
        divide(b, B01, 0  ,n/2);
        divide(b, B10, n/2,0  );
        divide(b, B11, n/2,n/2);

        //recursively adds chuncked matrices
        int[][] m1 = recursive(add(A00,A11), add(B00,B11),svalue);
        int[][] m2 = recursive(add(A10,A11),B00 ,svalue);
        int[][] m3 = recursive(A00, sub(B01,B11),svalue);
        int[][] m4 = recursive(A11,sub(B10,B00) ,svalue);
        int[][] m5 = recursive(add(A00,A01),B11 ,svalue);
        int[][] m6 = recursive(sub(A10,A00),add(B00,B01),svalue);
        int[][] m7 = recursive(sub(A01,A11),add(B10,B11),svalue);

        int[][] c00 = add(sub(add(m1,m4),m5),m7);
        int[][] c01 = add(m3,m5);
        int[][] c10 = add(m2,m4);
        int[][] c11 = add(sub(add(m1,m3),m2),m6);

        copy(c00,result,0  ,0  );
        copy(c01,result,0  ,n/2);
        copy(c10,result,n/2,0  );
        copy(c11,result,n/2,n/2);

        }

        return result;
    }
    //any size matrix sumation
    public static int[][] add(int[][] a, int[][] b){
        int n = a.length;
        int[][] result = new int[n][n];

        for(int i = 0;i<n;i++){
            for(int j= 0;j<n;j++){
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    //any size matrix subtraction
    public static int[][] sub(int[][] a, int[][] b){
        int n = a.length;
        int[][] result = new int[n][n];

        for(int i = 0;i<n;i++){
            for(int j= 0;j<n;j++){
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }
    //any size matrix division
    public static void divide(int[][] m1, int[][] c1, int ib, int jb){

        for(int i1 = 0, i2=ib; i1<c1.length; i1++, i2++){
            for(int j1 = 0, j2=jb; j1<c1.length; j1++, j2++)
            {
                c1[i1][j1] = m1[i2][j2];
            }
        }
    }

    //copies the results into the final matrics to spit out.
    public static void copy(int[][] c1, int[][] m1, int ib, int jb){
        for(int i1 = 0, i2=ib; i1<c1.length; i1++, i2++){
            for(int j1 = 0, j2=jb; j1<c1.length; j1++, j2++){
                m1[i2][j2] = c1[i1][j1];
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// These methods are for 2x2 matrices only,
    ///////////////////// these were my starting points for this lab.
    ///////////////////// but they were not used in final data projections
    ///////////////////////////////////////////////////////////////////////////
    
    
    

    //this method brute forces the matrix math of a 2x2
    public static int[][] bruteForce(int[][] a, int[][] b, int n){
        long timeS = System.nanoTime();
        int[][] c = {{(a[0][0] *b[0][0]) + (a[0][1]+b[1][0]),(a[0][0] *b[0][1]) + (a[0][1]+b[1][1])}, {(a[1][0] *b[0][0]) + (a[1][1]+b[1][0]),  (a[1][0] *b[0][1]) + (a[1][1]+b[1][1]) } };
        long timeF = System.nanoTime();
        bruteTime = timeF-timeS;

        return c;

    }
    //just the basic straussens algorithm for a 2x2 set of matrices.
    public static int[][] twonS(int[][] a, int[][] b){
        int n = a.length;
        int[][] c = new int[n][n];

        ///anything below here only runs if the matrix is size 2x2
        int m1 = (a[0][0] + a[1][1])*(b[0][0] + b[1][1]);
        int m2 = (a[1][0] + a[1][1])*b[0][0];
        int m3 = a[0][0]*(b[0][1]-b[1][1]);
        int m4 = a[1][1]*(b[1][0] - b[0][0]);
        int m5 = (a[0][0] + a[0][1])*b[1][1];
        int m6 = (a[1][0]-a[0][0])*(b[0][0] + b[0][1]);
        int m7 = (a[0][1] - a[1][1])*(b[1][0] + b[1][1]);

        c[0][0] = m1 + m4-m5+m7;
        c[0][1] = m3+m5;
        c[1][0] = m2+m4;
        c[1][1] = m1+m3-m2+m6;

        return c;
    }
    ///function to write to a csv file so that data can be exported easily.
    public static void writeToFile(double[] timeR,double[] timeB, int[] size,int svalue,String filename) throws FileNotFoundException{
        PrintWriter pw = new PrintWriter(new File(filename));
        StringBuilder sb = new StringBuilder();
        sb.append("Size,Time,sSize,\n");
        
        for(int i = 0; i< timeR.length;i++){
            sb.append(size[i]+",");
            sb.append(timeR[i]+",");
            sb.append(svalue+",\n");
        }
        
        pw.print(sb);
        ///end of file.
        pw.close();
         
       
    }
}


