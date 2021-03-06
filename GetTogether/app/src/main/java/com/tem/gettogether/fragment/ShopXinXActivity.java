package com.tem.gettogether.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tem.gettogether.R;
import com.tem.gettogether.ShowImageDetail;
import com.tem.gettogether.activity.LoginActivity;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseApplication;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.ImageDataBean;
import com.tem.gettogether.bean.MyShopDataBean;
import com.tem.gettogether.utils.Base64BitmapUtil;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tem.gettogether.utils.permissions.AppUtils;
import com.tem.gettogether.utils.permissions.FileUtils;
import com.tem.gettogether.utils.permissions.PermissionsActivity;
import com.tem.gettogether.utils.permissions.PictureUtil;
import com.tem.gettogether.utils.xutils3.MyCallBack;
import com.tem.gettogether.utils.xutils3.XUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.duduhuo.custoast.CusToast;

@ContentView(R.layout.activity_shop_xin_x)
public class ShopXinXActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.tv_title_right)
    private TextView tv_title_right;
    @ViewInject(R.id.et_ShopName)
    private EditText et_ShopName;
    @ViewInject(R.id.et_appJJ)
    private EditText et_appJJ;

    @ViewInject(R.id.et_lianxiren)
    private EditText et_lianxiren;
    @ViewInject(R.id.et_zuoji)
    private EditText et_zuoji;
    @ViewInject(R.id.et_shopphone)
    private EditText et_shopphone;
    @ViewInject(R.id.et_chuanz)
    private EditText et_chuanz;
    @ViewInject(R.id.et_email)
    private EditText et_email;
    @ViewInject(R.id.et_kfQQ)
    private EditText et_kfQQ;
    @ViewInject(R.id.et_wechat)
    private EditText et_wechat;
    @ViewInject(R.id.et_whatApp)
    private EditText et_whatApp;
    @ViewInject(R.id.et_facebook)
    private EditText et_facebook;
    @ViewInject(R.id.ll_banner)
    private LinearLayout ll_banner;
    @ViewInject(R.id.ll_logo)
    private LinearLayout ll_logo;
    @ViewInject(R.id.iv_image2)
    private ImageView iv_image2;
    @ViewInject(R.id.iv_image1)
    private ImageView iv_image1;
    @ViewInject(R.id.tv_image2)
    private TextView tv_image2;
    @ViewInject(R.id.tv_image1)
    private TextView tv_image1;
    //系统相机
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 101;
    //系统相册
    public static final int REQUEST_CODE_PHOTO_PERMISSION = 102;
    //所需要的权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //拍照所需要的权限
    static final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String IMAGE_FILE_NAME = "user_head_icon.jpg";
    private final int PHOTO_PICKED_FROM_CAMERA = 111; // 用来标识头像来自系统拍照
    private final int PHOTO_PICKED_FROM_FILE = 222; // 用来标识从相册获取头像
    private final int CROP_FROM_CAMERA = 333;
    private File mCropImageFile;
    private int imageType=0;
    private String Image_1,Image_2;
    private ArrayList<String> imagePaths1 = new ArrayList<>();
    private ArrayList<String> imagePaths2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {
        tv_title.setText(R.string.dianpuguanli);
//        tv_title_right.setText("保存");
//        tv_title_right.setVisibility(View.VISIBLE);
        upMyTJSHData();
    }

    @Override
    protected void initView() {

    }
    @Event(value = {R.id.rl_close,R.id.tv_fbcp,R.id.iv_image1,R.id.iv_image2,R.id.ll_logo,R.id.ll_banner}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.ll_logo:
                imageType=1;
                showPop(ll_logo);
                break;
            case R.id.ll_banner:
                imageType=2;
                showPop(ll_banner);
                break;
            case R.id.iv_image1:
                if(Image_1!=null&&!Image_1.equals("")){
                    Intent intent = new Intent(this, ShowImageDetail.class);
                    intent.putStringArrayListExtra("paths", imagePaths1);
                    intent.putExtra("index", 1);
                    startActivity(intent);
                }

                break;
            case R.id.iv_image2:
                if(Image_2!=null&&!Image_2.equals("")){
                    Intent intent2 = new Intent(this, ShowImageDetail.class);
                    intent2.putStringArrayListExtra("paths", imagePaths2);
                    intent2.putExtra("index", 1);
                    startActivity(intent2);
                }
                break;
            case R.id.tv_fbcp:
                Map<String,Object> map=new HashMap<>();
                map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));

                if(et_ShopName.getText().toString().equals("")){
                    CusToast.showToast(getText(R.string.enter_store_name));
                    return;
                }
                map.put("store_name",et_ShopName.getText().toString());
                if(Image_1!=null&&!Image_1.equals("")){
                    map.put("app_store_logo",Image_1);
                }
                if(Image_2!=null&&!Image_2.equals("")){
                    map.put("app_store_banner",Image_2);

                }
                map.put("store_contact_name",et_lianxiren.getText().toString());
                map.put("store_telephone",et_zuoji.getText().toString());
                map.put("store_fax",et_chuanz.getText().toString());
                map.put("store_email",et_email.getText().toString());
                map.put("store_qq",et_kfQQ.getText().toString());
                map.put("store_phone",et_shopphone.getText().toString());
                if(et_appJJ.getText().toString().equals("")){
                    CusToast.showToast(getText(R.string.qsrdpjj));
                    return;
                }
                map.put("store_des",et_appJJ.getText().toString());
                map.put("store_wx",et_wechat.getText().toString());
                map.put("store_whatsapp",et_whatApp.getText().toString());
                map.put("store_facebook",et_facebook.getText().toString());
                upTJSHData(map);
                break;
        }
    }
    private void upTJSHData(Map<String, Object> map){
        showDialog();
        XUtil.Post(URLConstant.DIANPU_XINXI, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                Log.i("====店铺信息保存===", result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    CusToast.showToast(msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                closeDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();
                closeDialog();
            }
        });
    }
    private void upMyTJSHData(){
        Map<String, Object> map=new HashMap<>();
        map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));

        showDialog();
        XUtil.Post(URLConstant.MYDEDIANPOU_XINXI, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                Log.i("====我的店铺信息===", result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    CusToast.showToast(msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        MyShopDataBean myShopDataBe=gson.fromJson(result,MyShopDataBean.class);
                        et_ShopName.setText(myShopDataBe.getResult().getStore_name());
                        et_appJJ.setText(myShopDataBe.getResult().getStore_des());
                        et_lianxiren.setText(myShopDataBe.getResult().getStore_contact_name());
                        et_zuoji.setText(myShopDataBe.getResult().getStore_telephone());
                        et_shopphone.setText(myShopDataBe.getResult().getStore_phone());
                        et_chuanz.setText(myShopDataBe.getResult().getStore_fax());


                        if(myShopDataBe.getResult().getApp_store_logo()!=null&&!myShopDataBe.getResult().getApp_store_logo().equals("")){
                            tv_image1.setVisibility(View.GONE);
                            Glide.with(ShopXinXActivity.this).load(myShopDataBe.getResult().getApp_store_logo()+"").asBitmap().error( R.mipmap.myy322x).centerCrop().into(iv_image1);
                        }
                        if(myShopDataBe.getResult().getApp_store_banner()!=null&&!myShopDataBe.getResult().getApp_store_banner().equals("")){
                            tv_image2.setVisibility(View.GONE);
                            Glide.with(ShopXinXActivity.this).load(myShopDataBe.getResult().getApp_store_banner()+"").asBitmap().error( R.mipmap.myy322x).centerCrop().into(iv_image2);


                        }

                        et_email.setText(myShopDataBe.getResult().getStore_email());
                        et_kfQQ.setText(myShopDataBe.getResult().getStore_qq());
                        et_wechat.setText(myShopDataBe.getResult().getStore_wx());
                        et_whatApp.setText(myShopDataBe.getResult().getStore_whatsapp());
                        et_facebook.setText(myShopDataBe.getResult().getStore_facebook());


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                closeDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();
                closeDialog();
            }
        });
    }
    private PopupWindow mPop;

    //显示弹窗
    private void showPop(View v) {
        initPop();
        if (mPop.isShowing())
            return;
        //设置弹窗底部位置
        mPop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
    }

    //初始化弹窗
    private void initPop() {
        if (mPop == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
            mPop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //点击弹窗外消失mPop
            mPop.setFocusable(true);
            mPop.setOutsideTouchable(true);
            //设置背景，才能使用动画效果
            mPop.setBackgroundDrawable(new BitmapDrawable());
            //设置动画
            mPop.setAnimationStyle(R.style.PopWindowAnim);
            //设置弹窗消失监听
            mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                }
            });
            //设置弹窗内的点击事件
            setPopClickListener(view);
        }
    }
    private void setPopClickListener(View view) {
        TextView tv_iteam1, photo, cancle;
        photo = view.findViewById(R.id.photo);
        cancle = view.findViewById(R.id.cancle);
        tv_iteam1 = view.findViewById(R.id.tv_iteam1);
        tv_iteam1.setTextSize(16);
        tv_iteam1.setText(getText(R.string.take_photo));
        photo.setText(getText(R.string.album));
        tv_iteam1.setTextColor(getResources().getColor(R.color.black));
        photo.setTextColor(getResources().getColor(R.color.black));
        tv_iteam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionsActivity.startActivityForResult((Activity) mContext, REQUEST_CODE_CAMERA_PERMISSION, PERMISSIONS_CAMERA);//打开系统相机需要相机权限


                mPop.dismiss();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到系统相册
                PermissionsActivity.startActivityForResult((Activity) mContext, REQUEST_CODE_PHOTO_PERMISSION, PERMISSIONS);
                mPop.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();
            }
        });
    }

    /**
     * 设置图片
     */
    public void setPicToView() {
        if (mCropImageFile != null) {
            String path = mCropImageFile.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(mCropImageFile.toString());

            Map<String,Object> map=new HashMap<>();
            map.put("image_base_64_arr", "data:image/jpeg;base64,"+ Base64BitmapUtil.bitmapToBase64(bitmap));
            upInputImageData(map,bitmap);
        }
    }
    private void upInputImageData(Map<String, Object> map, final Bitmap bitmap) {
        showDialog();
        XUtil.Post(URLConstant.SHANGCHUAN_IMAGE, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                Log.i("====上传图片===", result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    CusToast.showToast(msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        ImageDataBean imageDataBean=gson.fromJson(result,ImageDataBean.class);
                        if(imageType==1){
                            iv_image1.setImageBitmap(bitmap);
                            iv_image1.setVisibility(View.VISIBLE);

                        } else if(imageType==2){
                            iv_image2.setImageBitmap(bitmap);
                            iv_image2.setVisibility(View.VISIBLE);

                        }
                        if(imageType==1){
                            Image_1=imageDataBean.getResult().getImage_show().get(0);
                            tv_image1.setVisibility(View.GONE);
                            imagePaths1.add(Image_1);
                        }else if(imageType==2){
                            Image_2=imageDataBean.getResult().getImage_show().get(0);
                            tv_image2.setVisibility(View.GONE);
                            imagePaths2.add(Image_2);
                        }
                    }else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                closeDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();
                closeDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //系统相机权限
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            } else {
                imageCapture();//系统相机拍照
            }
        }
        //拍照完成的回调
        if (requestCode == PHOTO_PICKED_FROM_CAMERA && resultCode == Activity.RESULT_OK) {//Activity.RESULT_OK可以确保拍照后有回调结果，屏蔽了返回键的回调
            startSystemCamera();
        }
        //裁剪的图片的回调
        if (requestCode == CROP_FROM_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Uri cropUri = Uri.fromFile(mCropImageFile);
                setPicToView();
            }
        }
        //系统相册
        if (requestCode == REQUEST_CODE_PHOTO_PERMISSION) {
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            } else {
                chooseImageSys();//打开系统相册
            }
        }
        //从相册选择图片之后
        if (requestCode == PHOTO_PICKED_FROM_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = PictureUtil.getImageUri(this, data);
                startPhotoZoom(uri);
            }
        }
    }
    //调用系统相册
    private void chooseImageSys() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_PICKED_FROM_FILE);
    }

    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        //getMyPetRootDirectory()得到的是Environment.getExternalStorageDirectory() + File.separator+"."
        //也就是我之前创建的存放头像的文件夹（目录）
        File pictureFile = new File(PictureUtil.getMyPetRootDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //这一句非常重要
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //""中的内容是随意的，但最好用package名.provider名的形式，清晰明了
            pictureUri = FileProvider.getUriForFile(this,
                    "com.tem.gettogether.FileProvider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照,拍照的结果存到pictureUri对应的路径中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, PHOTO_PICKED_FROM_CAMERA);
    }

    /**
     * 系统拍照后裁剪
     */
    public void startSystemCamera() {
        File pictureFile = new File(PictureUtil.getMyPetRootDirectory(), IMAGE_FILE_NAME);
        Uri pictureUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pictureUri = FileProvider.getUriForFile(this,
                    "com.tem.gettogether.FileProvider", pictureFile);
        } else {
            pictureUri = Uri.fromFile(pictureFile);
        }
        startPhotoZoom(pictureUri);
    }

    public void startPhotoZoom(Uri uri) {
        try {
            if (AppUtils.existSDCard()) {
                mCropImageFile = FileUtils.createTmpFile(this);
                Intent intent = new Intent("com.android.camera.action.CROP");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                if (Build.MANUFACTURER.equals("HUAWEI")) {//解决华为手机调用裁剪出现圆形裁剪框
                    intent.putExtra("aspectX", 9998);
                    intent.putExtra("aspectY", 9999);
                } else {
                    intent.putExtra("aspectX", 1); // 裁剪框比例
                    intent.putExtra("aspectY", 1);
                }
                intent.putExtra("outputX", 300); // 输出图片大小
                intent.putExtra("outputY", 300);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                intent.putExtra("circleCrop", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCropImageFile));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, CROP_FROM_CAMERA);
            }
        } catch (Exception e) {

        }

    }
}
