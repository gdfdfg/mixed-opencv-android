package com.chuangweizong.opencv.helper;

import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class FloodFillAlgorithm {

	private Bitmap inputImage;
	
	private int[] inPixels;
	
	private int width;
	
	private int height;
	
	private int maxStackSize = 500;
	
	private int[] xstack = new int[maxStackSize];
	
	private int[] ystack = new int[maxStackSize];
	
	private int stackSize;
	
    /**
     * 边界的颜色
     */
    private int mBorderColor = -1;

    private boolean hasBorderColor = false;

    private Stack<Point> mStacks = new Stack<Point>();

	public FloodFillAlgorithm(Bitmap bitmap){
		
		this.inputImage = bitmap;
		
		
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		
		inPixels = new int[width*height];
		bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
		
//		for(int a=0;a<inPixels.length;a++){
//			if(inPixels[a]!=){
//				
//			}
//		}
		
	}
	
    public Bitmap getCurrentBitMap(){
//    	inputImage = Bitmap.createBitmap(inPixels, width, height, Bitmap.Config.ARGB_8888);
    	return inputImage;
    }

    /**
     * 得到坐标的图片
     * @param x
     * @param y
     * @return
     */
	public int getColor(int x, int y) { 
		int index = y * width + x; 
		return inPixels[index];
	} 
	
	private void setColor(int x, int y, int newColor) 
    { 
        int index = y * width + x; 
        inPixels[index] = newColor; 
    }
    
    /**
     * 4位填充
     * @param x
     * @param y
     * @param newColor
     * @param oldColor
     */
    public void floodFill4(int x, int y, int newColor, int oldColor)
    { 
        if(x >= 0 && x < width && y >= 0 && y < height  
                && getColor(x, y) == oldColor && getColor(x, y) != newColor)  
        {  
            setColor(x, y, newColor); //set color before starting recursion  
            floodFill4(x + 1, y,     newColor, oldColor); 
            floodFill4(x - 1, y,     newColor, oldColor); 
            floodFill4(x,     y + 1, newColor, oldColor); 
            floodFill4(x,     y - 1, newColor, oldColor); 
        }    
    } 
    
    /**
     * 8位填充
     * @param x
     * @param y
     * @param newColor
     * @param oldColor
     */
    public void floodFill8(int x, int y, int newColor, int oldColor) 
    { 
        if(x >= 0 && x < width && y >= 0 && y < height &&  
                getColor(x, y) == oldColor && getColor(x, y) != newColor)  
        {  
            setColor(x, y, newColor); //set color before starting recursion  
            floodFill8(x + 1, y,     newColor, oldColor); 
            floodFill8(x - 1, y,     newColor, oldColor); 
            floodFill8(x,     y + 1, newColor, oldColor); 
            floodFill8(x,     y - 1, newColor, oldColor); 
            floodFill8(x + 1, y + 1, newColor, oldColor); 
            floodFill8(x - 1, y - 1, newColor, oldColor); 
            floodFill8(x - 1, y + 1, newColor, oldColor); 
            floodFill8(x + 1, y - 1, newColor, oldColor); 
        }    
    }
    
    /**
     * 线性填充
     * @param x
     * @param y
     * @param newColor
     * @param oldColor
     */
    public void floodFillScanLine(int x, int y, int newColor, int oldColor) 
    { 
        if(oldColor == newColor) return; 
        if(getColor(x, y) != oldColor) return; 
           
        int y1; 
         
        //draw current scanline from start position to the top  
        y1 = y; 
        while(y1 < height && getColor(x, y1) == oldColor) 
        { 
            setColor(x, y1, newColor); 
            y1++; 
        }     
         
        //draw current scanline from start position to the bottom  
        y1 = y - 1; 
        while(y1 >= 0 && getColor(x, y1) == oldColor) 
        { 
            setColor(x, y1, newColor); 
            y1--; 
        } 
         
        //test for new scanlines to the left  
        y1 = y; 
        while(y1 < height && getColor(x, y1) == newColor) 
        { 
            if(x > 0 && getColor(x - 1, y1) == oldColor)  
            { 
                floodFillScanLine(x - 1, y1, newColor, oldColor); 
            }  
            y1++; 
        } 
        y1 = y - 1; 
        while(y1 >= 0 && getColor(x, y1) == newColor) 
        { 
            if(x > 0 && getColor(x - 1, y1) == oldColor)  
            { 
                floodFillScanLine(x - 1, y1, newColor, oldColor); 
            } 
            y1--; 
        }  
         
        //test for new scanlines to the right   
        y1 = y; 
        while(y1 < height && getColor(x, y1) == newColor) 
        { 
            if(x < width - 1 && getColor(x + 1, y1) == oldColor)  
            {            
                floodFillScanLine(x + 1, y1, newColor, oldColor); 
            }  
            y1++; 
        } 
        y1 = y - 1; 
        while(y1 >= 0 && getColor(x, y1) == newColor) 
        { 
            if(x < width - 1 && getColor(x + 1, y1) == oldColor)  
            { 
                floodFillScanLine(x + 1, y1, newColor, oldColor); 
            } 
            y1--; 
        } 
    } 
   
    /**
     * 栈线性填充
     * @param x
     * @param y
     * @param newColor
     * @param oldColor
     */
    public void floodFillScanLineWithStack(int x, int y, int newColor, int oldColor , int threshould) 
    { 
        if(oldColor == newColor) { 
            return; 
        } 
        emptyStack(); 
        int y1;  
        boolean spanLeft, spanRight; 
        push(x, y); 
         
        while(true) 
        {
            x = popx(); 
            if(x == -1) return; 
            y = popy(); 
            y1 = y; 
            while(y1 >= 0 && Math.abs(getColor(x, y1)-oldColor) < threshould) {
            	y1--;
            	if(y1 >= 0){
//                	Log.i("test", "a:"+getColor(x, y1));
            	}
            }; // go to line top/bottom  
            y1++; // start from line starting point pixel  
            spanLeft = spanRight = false; 
            while(y1 < height && Math.abs(getColor(x, y1)-oldColor) < threshould) 
            {
                setColor(x, y1, newColor); 
                if(!spanLeft && x > 0 && Math.abs(getColor(x - 1, y1)-oldColor) < threshould)// just keep left line once in the stack  
                { 
                    push(x - 1, y1); 
                    spanLeft = true; 
                } 
                else if(spanLeft && x > 0 && Math.abs(getColor(x - 1, y1)-oldColor) > threshould) 
                { 
                    spanLeft = false; 
                } 
                if(!spanRight && x < width - 1 && Math.abs(getColor(x + 1, y1)-oldColor) < threshould ) // just keep right line once in the stack  
                { 
                    push(x + 1, y1); 
                    spanRight = true; 
                } 
                else if(spanRight && x < width - 1 && Math.abs(getColor(x + 1, y1)-oldColor) > threshould) 
                { 
                    spanRight = false; 
                }  
                y1++; 
//                Log.i("test", "容差:"+Math.abs(getColor(x, y1)-oldColor));
            } 
        } 
    } 

    public void fillColorToSameArea(int x, int y,int newColor)
    {
        int pixel = inputImage.getPixel(x, y);
        if (pixel == Color.TRANSPARENT || (hasBorderColor && mBorderColor == pixel))
        {
            return;
        }
        //填色
        fillColor(inPixels, width, height, pixel, newColor, x, y);
        //重新设置bitmap
        inputImage.setPixels(inPixels, 0, width, 0, 0, width, height);

    }
    
    /**
     * @param pixels   像素数组
     * @param w        宽度
     * @param h        高度
     * @param pixel    当前点的颜色
     * @param newColor 填充色
     * @param i        横坐标
     * @param j        纵坐标
     */
    private void fillColor(int[] pixels, int w, int h, int pixel, int newColor, int i, int j)
    {
        //步骤1：将种子点(x, y)入栈；
        mStacks.push(new Point(i, j));
        //步骤2：判断栈是否为空，
        // 如果栈为空则结束算法，否则取出栈顶元素作为当前扫描线的种子点(x, y)，
        // y是当前的扫描线；
        while (!mStacks.isEmpty())
        {
            /**
             * 步骤3：从种子点(x, y)出发，沿当前扫描线向左、右两个方向填充，
             * 直到边界。分别标记区段的左、右端点坐标为xLeft和xRight；
             */
            Point seed = mStacks.pop();
            //L.e("seed = " + seed.x + " , seed = " + seed.y);
            int count = fillLineLeft(pixels, pixel, w, h, newColor, seed.x, seed.y);
            int left = seed.x - count + 1;
            count = fillLineRight(pixels, pixel, w, h, newColor, seed.x + 1, seed.y);
            int right = seed.x + count;
            /**
             * 步骤4：
             * 分别检查与当前扫描线相邻的y - 1和y + 1两条扫描线在区间[xLeft, xRight]中的像素，
             * 从xRight开始向xLeft方向搜索，假设扫描的区间为AAABAAC（A为种子点颜色），
             * 那么将B和C前面的A作为种子点压入栈中，然后返回第（2）步；
             */
            //从y-1找种子
            if (seed.y - 1 >= 0)
                findSeedInNewLine(pixels, pixel, w, h, seed.y - 1, left, right);
            //从y+1找种子
            if (seed.y + 1 < h)
                findSeedInNewLine(pixels, pixel, w, h, seed.y + 1, left, right);
        }
    }

    
    
    
    /**
     * 在新行找种子节点
     *
     * @param pixels
     * @param pixel
     * @param w
     * @param h
     * @param i
     * @param left
     * @param right
     */
    private void findSeedInNewLine(int[] pixels, int pixel, int w, int h, int i, int left, int right)
    {
        /**
         * 获得该行的开始索引
         */
        int begin = i * w + left;
        /**
         * 获得该行的结束索引
         */
        int end = i * w + right;

        boolean hasSeed = false;

        int rx = -1, ry = -1;

        ry = i;

        /**
         * 从end到begin，找到种子节点入栈（AAABAAAB，则B前的A为种子节点）
         */
        while (end >= begin)
        {
            if (pixels[end] == pixel)
            {
                if (!hasSeed)
                {
                    rx = end % w;
                    mStacks.push(new Point(rx, ry));
                    hasSeed = true;
                }
            } else
            {
                hasSeed = false;
            }
            end--;
        }
    }

    /**
     * 往右填色，返回填充的个数
     *
     * @return
     */
    private int fillLineRight(int[] pixels, int pixel, int w, int h, int newColor, int x, int y)
    {
        int count = 0;

        while (x < w)
        {
            //拿到索引
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index))
            {
                setColor(x, y, newColor);
                count++;
                x++;
            } else
            {
                break;
            }

        }

        return count;
    }


    /**
     * 往左填色，返回填色的数量值
     *
     * @return
     */
    private int fillLineLeft(int[] pixels, int pixel, int w, int h, int newColor, int x, int y)
    {
        int count = 0;
        while (x >= 0)
        {
            //计算出索引
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index))
            {
            	setColor(x, y, newColor);
                count++;
                x--;
            } else
            {
                break;
            }

        }
        return count;
    }

    
    /**
     * 
     * @param pixels
     * @param pixel 当前点的颜色
     * @param index 索引 
     * @return
     */
    private boolean needFillPixel(int[] pixels, int pixel, int index)
    {
        if (hasBorderColor)
        {
            return pixels[index] != mBorderColor;
        } else
        {
            return pixels[index] == pixel;
        }
    }
    
    
//    public void floodFillScanLineWithStack(int x, int y, int newColor, int oldColor) 
//    { 
//        if(oldColor == newColor) { 
//            return; 
//        } 
//        emptyStack(); 
//        int y1;  
//        boolean spanLeft, spanRight; 
//        push(x, y); 
//         
//        while(true) 
//        {     
//            x = popx(); 
//            if(x == -1) return; 
//            y = popy(); 
//            y1 = y; 
//            while(y1 >= 0 && getColor(x, y1) == oldColor) y1--; // go to line top/bottom  
//            y1++; // start from line starting point pixel  
//            spanLeft = spanRight = false; 
//            while(y1 < height && getColor(x, y1) == oldColor) 
//            { 
//                setColor(x, y1, newColor); 
//                if(!spanLeft && x > 0 && getColor(x - 1, y1) == oldColor)// just keep left line once in the stack  
//                { 
//                    push(x - 1, y1); 
//                    spanLeft = true; 
//                } 
//                else if(spanLeft && x > 0 && getColor(x - 1, y1) != oldColor) 
//                { 
//                    spanLeft = false; 
//                } 
//                if(!spanRight && x < width - 1 && getColor(x + 1, y1) == oldColor) // just keep right line once in the stack  
//                { 
//                    push(x + 1, y1); 
//                    spanRight = true; 
//                } 
//                else if(spanRight && x < width - 1 && getColor(x + 1, y1) != oldColor) 
//                { 
//                    spanRight = false; 
//                }  
//                y1++; 
//            } 
//        } 
//    }
    
    private void emptyStack() { 
        while(popx() != - 1) { 
            popy(); 
        }
        stackSize = 0; 
    } 
    
    
     void push(int x, int y) { 
        stackSize++; 
        if (stackSize==maxStackSize) { 
            int[] newXStack = new int[maxStackSize*2]; 
            int[] newYStack = new int[maxStackSize*2]; 
            System.arraycopy(xstack, 0, newXStack, 0, maxStackSize); 
            System.arraycopy(ystack, 0, newYStack, 0, maxStackSize); 
            xstack = newXStack; 
            ystack = newYStack; 
            maxStackSize *= 2; 
        } 
        xstack[stackSize-1] = x; 
        ystack[stackSize-1] = y; 
    } 
     
    int popx() { 
        if (stackSize==0) 
            return -1; 
        else 
            return xstack[stackSize-1]; 
    } 
 
    int popy() { 
        int value = ystack[stackSize-1]; 
        stackSize--; 
        return value; 
    }
}
