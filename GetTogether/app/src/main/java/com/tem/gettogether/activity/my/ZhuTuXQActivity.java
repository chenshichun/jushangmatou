package com.tem.gettogether.activity.my;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tem.gettogether.R;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseApplication;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.BaseRVAdapter;
import com.tem.gettogether.base.BaseViewHolder;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.ImageDataBean;
import com.tem.gettogether.utils.Base64BitmapUtil;
import com.tem.gettogether.utils.BitnapUtils;
import com.tem.gettogether.utils.Confirg;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tem.gettogether.utils.permissions.AppUtils;
import com.tem.gettogether.utils.permissions.FileUtils;
import com.tem.gettogether.utils.permissions.PermissionsActivity;
import com.tem.gettogether.utils.permissions.PermissionsChecker;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.duduhuo.custoast.CusToast;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

@ContentView(R.layout.activity_tu_wen_xq)
public class ZhuTuXQActivity extends BaseActivity  implements View.OnClickListener{
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.tv_fbShopping)
    private TextView tv_fbShopping;
    private final int FROM_ALBUM_CODE = 102;// 调用相册更改背景图片的请求code
    @ViewInject(R.id.tv_title_right)
    private TextView tv_title_right;
    private PermissionsChecker checker;

    private String compressImageFilePath;
    private String[] strPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODEBBB = 100;
    private ArrayList<String> imagePaths = new ArrayList<>();
    @ViewInject(R.id.publish_task_recyclerView)
    private RecyclerView recyclerView;
    private List<Integer> imageRes = new ArrayList<>();
    private BaseRVAdapter mTaskImgAdapter;
    private    String strImage="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        checker = new PermissionsChecker(this);

        initData();
        initView();
    }

    @Override
    protected void initData() {
        tv_title.setText(getText(R.string.product_master_map));
        tv_title_right.setText(getText(R.string.save));
        cartImage=getIntent().getStringArrayListExtra("listImage2");
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setTextColor(getResources().getColor(R.color.my_yellow));
    }

    @Override
    protected void initView() {
        compressImageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WorksComing/Compress/";
        File folder = new File(compressImageFilePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTaskImgAdapter=new BaseRVAdapter(this,cartImage) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.recy_image_item;
            }

            @Override
            public void onBind(BaseViewHolder holder, int position) {
                ImageView iv_image=holder.getImageView(R.id.iv_image);
                Glide.with(ZhuTuXQActivity.this).load(cartImage.get(position)).error(R.mipmap.myy322x).into(iv_image);
                /*if(position==imagePaths.size()-1){
                    holder.getTextView(R.id.tv_shanchu).setVisibility(View.VISIBLE);
                }else {
                    holder.getTextView(R.id.tv_shanchu).setVisibility(View.GONE);
                }*/
               /* holder.getTextView(R.id.tv_shanchu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(imagePaths.size()>0){
//                            imagePaths.remove(imagePaths.size()-1);
//                        }

                        if(cartImage.size()>0){
                            cartImage.remove(cartImage.size()-1);
                        }

                        mTaskImgAdapter.notifyDataSetChanged();
                    }
                });*/
            }

        };
        recyclerView.setAdapter(mTaskImgAdapter);
    }
    @Event(value = {R.id.rl_close,R.id.tv_fbShopping,R.id.rl_title_right}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.tv_fbShopping:
                showPop(tv_fbShopping);
                break;
            case R.id.rl_title_right:
                if(cartImage.size()>0){
                    for (int i = 0; i < cartImage.size(); i++) {
                        if(i<cartImage.size()-1) {
                            strImage += cartImage.get(i) + ",";
                        }else{
                            strImage += cartImage.get(i);
                        }
                    }
//                    Log.i("===返回图片--",strImage);
                    setResult(RESULT_OK,new Intent().putExtra("goods_content",strImage)
                            .putStringArrayListExtra("listImage2", cartImage));
                    finish();
                }else {
                    CusToast.showToast(getText(R.string.please_add_an_image));
                    return;
                }
                break;
        }
    }
    private PopupWindow mPop;
    //初始化弹窗
    private void initPop() {
        if (mPop == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.pop_layout_chanpin, null);
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

    private void setPopClickListener(View view) {
        TextView tv_xiangche, cancle;
        tv_xiangche = view.findViewById(R.id.tv_xiangche);
        cancle = view.findViewById(R.id.cancle);

        tv_xiangche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePaths.size() >= 9) {
                    CusToast.showToast(getText(R.string.select_up_to_9_images));
                    return;
                }

                Intent intent = new Intent(ZhuTuXQActivity.this, MultiImageSelectorActivity.class);
                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                // 最大图片选择数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);

                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

                startActivityForResult(intent, FROM_ALBUM_CODE);
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
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checker.lacksPermissions(strPermissions)) {
                permissionActivity();
            }
        }
    }
    private void permissionActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODEBBB, strPermissions);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //相册
                case FROM_ALBUM_CODE:
                    if (null != data) {
                        if (!Confirg.compressFile.exists()) {
                            Confirg.compressFile = new File(Confirg.FilesPath);
                            Confirg.compressFile.mkdirs();
                        }
                        List<String> list = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                        System.out.println("========：" + list.size());
                        if (imagePaths.size() < 9) {
                            for (int i = 0; i < list.size(); i++) {
                                System.out.println("选中的路径：" + list.get(i));
                                String pic_path = list.get(i);
                                String targetPath = compressImageFilePath + Confirg.df.
                                        format(new Date()) + ".jpg";
                                //调用压缩图片的方法，返回压缩后的图片path
                                final String compressImage = BitnapUtils.compressImage(pic_path, targetPath, 40);
                                imagePaths.add(list.get(i));
                                Bitmap bitmap = BitmapFactory.decodeFile(compressImage.toString());
                                Map<String,Object> map=new HashMap<>();
                                map.put("image_base_64_arr", "data:image/jpeg;base64,"+ Base64BitmapUtil.bitmapToBase64(bitmap));
                                upMessageData(map);
                            }
                        } else {
                            CusToast.showToast(getText(R.string.unable_to_add_more_images));
                        }
                        mTaskImgAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
    private File mCropImageFile;
    private final int CROP_FROM_CAMERA = 333;
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
    ArrayList<String> cartImage = new ArrayList<>();

    private void upMessageData(Map<String, Object> map) {
        XUtil.Post(URLConstant.SHANGCHUAN_IMAGE, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                Log.i("====上传图片===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        ImageDataBean imageDataBean=gson.fromJson(result,ImageDataBean.class);
                        Map<String,Object> map=new HashMap<>();
                        map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));

                        if(imageDataBean.getResult().getImage_show().size()>=0){
                            map.put("head_pic", imageDataBean.getResult().getImage_show().get(0));
                            cartImage.add(imageDataBean.getResult().getImage_show().get(0));

                        }
                    }else {
                        CusToast.showToast(msg);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                mTaskImgAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity_distributor_rz in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
