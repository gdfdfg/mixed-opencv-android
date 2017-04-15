package com.chuangweizong.opencv.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class AssetsCopyTOSDcard {
    Context context;

    public AssetsCopyTOSDcard(Context context) {
        super();
        this.context = context;
    }

    /**
     * @param context
     * @param assetpath  asset下的路径
     * @param SDpath     SDpath下保存路径
     */
    public void AssetToSD(String assetpath,String SDpath ){

        AssetManager asset=context.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames=null;
        FileOutputStream out = null;
        InputStream in=null;
        try {
            filenames = asset.list(assetpath);

            Log.i("opencv","filename.length:"+filenames.length);

            if(filenames.length>0){//说明是目录
                //创建目录
                getDirectory(assetpath);

                for(String fileName:filenames){
                    AssetToSD(assetpath+"/"+fileName, SDpath+"/"+fileName);
                }
            }else{//说明是文件，直接复制到SD卡
                File SDFlie=new File(context.getFilesDir().getPath().toString()+"/"+assetpath);
                String path = context.getFilesDir().getPath().toString()+"/"+assetpath.substring(0, assetpath.lastIndexOf("/"));

                File pathDir = new File(path);
                if (!pathDir.exists()){
                    pathDir.mkdirs();
                }

                Log.i("opencv", "asset path:" + assetpath + ",SDpath:" + context.getFilesDir().getPath().toString()+"/"+assetpath);

                Log.i("opencv", "file exist:"+SDFlie.exists());
                if(SDFlie.exists()){
                    SDFlie.createNewFile();
                    Log.i("opencv", "----------------------->1");
                }

                Log.i("opencv", "----------------------->2");
                //将内容写入到文件中
                in=asset.open(assetpath);

                Log.i("opencv", "----------------------->3");
                out= new FileOutputStream(SDFlie);
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=in.read(buffer))!=-1){
                    out.write(buffer, 0, byteCount);
                }
                out.flush();

                Log.i("opencv", "----------------------->4");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("opencv", "message:"+e.getMessage());
        }finally{
            try {
                if(out != null){
                    out.close();
                }
                if(in != null){
                    in.close();
                }
                if(asset != null){
                    asset.close();
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    //分级建立文件夹
    public void getDirectory(String path){
        //对SDpath进行处理，分层级建立文件夹
        String[]  s=path.split("/");
        String str=Environment.getExternalStorageDirectory().toString();
        for (int i = 0; i < s.length; i++) {
            str=str+"/"+s[i];
            File file=new File(str);
            if(!file.exists()){
                file.mkdir();
            }
        }

    }
}
