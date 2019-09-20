package com.huaying.italker.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huaying.italker.factory.Factory;
import com.huaying.italker.utils.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * @authoer johnyin2015
 */
public class UploadHelper {

    private static final String TAG = UploadHelper.class.getSimpleName();
    public static final String ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    //仓库名
    private static final String BUCKET_NAME = "italker-new";

    public static OSS getClient(){

        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        @SuppressWarnings("deprecation")
        OSSCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider
                        ("LTAIYQD07p05pHQW",
                                "2txxzT8JXiHKEdEjylumFy6sXcDQ0G");

        OSS oss = new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
        return oss;
    }

    //objKey 上传上 服务器 key
    //return 存储地址
    private static String upload(String objKey,String path){
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);

        try {
            OSS client = getClient();
            PutObjectResult result = client.putObject(request);

            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            Log.d(TAG,String.format("PublicObjectURL:%s",url));
            return url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadImage(String path){
        String key = getImageObjKey(path);
        return upload(key,path);
    }
    public static String uploadPortrait(String path){
        String key = getPortraitObjKey(path);
        return upload(key,path);
    }
    public static String uploadAudio(String path){
        String key = getAudioObjKey(path);
        return upload(key,path);
    }

    private static String getImageObjKey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg",dateString,fileMd5);
    }
    private static String getPortraitObjKey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }
    private static String getAudioObjKey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }
    private static String getDateString(){
        return DateFormat.format("yyyyMM",new Date()).toString();
    }
}
