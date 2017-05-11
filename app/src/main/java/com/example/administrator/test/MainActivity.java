package com.example.administrator.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditText;
    private Button sa;
    private static final int CODE_PERSON = 1;
    String upId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.list_item);
        sa = (Button) findViewById(R.id.sa);
        sa.setOnClickListener(this);
        mEditText.setFilters(new InputFilter[]{new MyInputFilter()});
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sa:
                upServiceId();
                break;
        }
    }
    /**
     * 识别输入框的是不是@符号
     */
    private class MyInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                goAt();
            }
            return source;
        }
    }
    private void goAt() {
//         StringBuffer tmp = new StringBuffer();
        // 把选中人的id已空格分隔，拼接成字符串
//        for (Map.Entry<String, String> entry : cidNameMap.entrySet()) {
//            tmp.append(entry.getKey() + " ");
//        }
        Intent intent = new Intent(this, PersonActivity.class);
//       intent.putExtra(PersonActivity.KEY_SELECTED, tmp.reverse().toString());
//        intent.putExtra(PersonActivity.KEY_SELECTED, a);
        startActivityForResult(intent, CODE_PERSON);
    }

    //上传需要的id值
    public void  upServiceId() {
        ArrayList<String> list = new ArrayList<String>();
        String content = String.valueOf(mEditText.getText().toString().trim());
        String[] sss = content.split(" ");
        for (String s : sss) {
            list.add(s);
        }
        for (int i = 0; i < list.size(); i++) {
            String keys = "";
            Iterator it = cidNameMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object obj = entry.getValue();
                if (obj != null && obj.equals(list.get(i))) {
                    keys = (String) entry.getKey();
                }
            }
            if (TextUtils.isEmpty(upId)) {
                upId = keys;
            } else {
                upId = upId + "," + keys;
            }
        }
    }
    /**
     * 存储@的cid、name对
     */
    private Map<String, String> cidNameMap = new HashMap<String, String>();
    private String selectedCids; //选中的@的人的cid,进入@列表时，需要传递过去
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            int curIndex = mEditText.getSelectionStart();
//            if (curIndex >= 1) {
//                mEditText.getText().replace(curIndex - 1, curIndex, "");
//            }
//            return;
//        }
        switch (requestCode) {
            case CODE_PERSON:
                String tmpCidStr = data.getStringExtra(PersonActivity.KEY_CID);
                String tmpNameStr = data.getStringExtra(PersonActivity.KEY_NAME);

                String[] tmpCids = tmpCidStr.split(" ");
                String[] tmpNames = tmpNameStr.split(" ");

                if (tmpCids != null && tmpCids.length > 0) {
                    for (int i = 0; i < tmpCids.length; i++) {
                        if (tmpNames.length > i) {
                            cidNameMap.put(tmpCids[i], tmpNames[i]);
                        }
                    }
                }
//                if (selectedCids == null) {
//                    selectedCids = tmpCidStr;
//                } else {
//                    selectedCids = selectedCids + "," + tmpCidStr;
//                }
// 这里的lists集合，是把输入框里已经有的人名放进集合中，再在一个一个的比较选择返回的人名，如果是一样的就return，并且不再显示到输入框
                ArrayList<String> lists = new ArrayList<String>();
                String content = String.valueOf(mEditText.getText().toString().trim());
                String[] ssss = content.split(" ");
                for (String s : ssss) {
                    lists.add(s);
                }
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).equals(tmpNameStr.trim())) {
                        int curIndex = mEditText.getSelectionStart();
                        if (curIndex >= 1) {
                            mEditText.getText().replace(curIndex - 1, curIndex, "");
                        }
                        return;
                    }
                }
                //返回的人名，自增加
                if (nameStr == null) {
                    nameStr = tmpNameStr;
                } else {
                    nameStr = nameStr + tmpNameStr;

                }
                lastNameStr = tmpNameStr;
                // 获取光标当前位置
                int curIndex = mEditText.getSelectionStart();
                // 把要@的人插入光标所在位置
                mEditText.getText().insert(curIndex, lastNameStr);
                // 通过输入@符号进入好友列表并返回@的人，要删除之前输入的@
                if (curIndex >= 1) {
                    mEditText.getText().replace(curIndex - 1, curIndex, "");
                }
                setAtImageSpan(nameStr);
                break;
        }
    }

    /**
     * 返回的所有的用户名,用于识别输入框中的所有要@的人
     * 如果用户删除过，会出现不匹配的情况，需要在for循环中做处理
     */
    private String nameStr;
    /**
     * 上一次返回的用户名，用于把要@的用户名拼接到输入框中
     */
    private String lastNameStr;
    private void setAtImageSpan(String nameStr) {
        String content = String.valueOf(mEditText.getText());
        if (content.endsWith("@") || content.endsWith("＠")) {
            content = content.substring(0, content.length() - 1);
        }
        String tmp = content;
        SpannableString ss = new SpannableString(tmp);
        if (nameStr != null) {
            String[] names = nameStr.split(" ");
            if (names != null && names.length > 0) {
                for (String name : names) {
                    if (name != null && name.trim().length() > 0) {
                        //把获取到的名字转为bitmap对象
                        final Bitmap bmp = getNameBitmap(name);
                        // 这里会出现删除过的用户，需要做判断，过滤掉
                        if (tmp.indexOf(name) >= 0
                                && (tmp.indexOf(name) + name.length()) <= tmp
                                .length()) {
                            // 把取到的要@的人名，用DynamicDrawableSpan代替
                            ss.setSpan(
                                    new DynamicDrawableSpan(
                                            DynamicDrawableSpan.ALIGN_BASELINE) {
                                        @Override
                                        public Drawable getDrawable() {
                                            BitmapDrawable drawable = new BitmapDrawable(
                                                    getResources(), bmp);
                                            drawable.setBounds(0, 0,
                                                    bmp.getWidth(),
                                                    bmp.getHeight());
                                            return drawable;
                                        }
                                    }, tmp.indexOf(name),
                                    tmp.indexOf(name) + name.length(),
                                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }
        mEditText.setTextKeepState(ss);
    }
    /**
     * 把返回的人名，转换成bitmap
     * @param name
     * @return
     */
    private Bitmap getNameBitmap(String name) {
        /* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        name = "" + name;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置字体画笔的颜色
//        paint.setColor(getResources().getColor(R.color.color_blue));
        paint.setTextSize(30);
        Rect rect = new Rect();
        paint.getTextBounds(name, 0, name.length(), rect);
        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));
        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(getResources().getColor(R.color.color_blue));
        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);
        return bmp;
    }
}