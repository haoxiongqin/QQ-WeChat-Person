 最近在做项目中有一个群组的聊天模块，没有引用第三方的及时通信。这个模块中包含了文字、表情、语音、上传文档、下载文档、上传图片、以及对图片的处理放大（这里的图片处理可以使用PhotoView的开源库，可以下载使用）等。点击打开链接  
        这些都还好只是在文本中增加了一个@某人的功能，还可以@多人并把最后@人对应的id传到服务器中，这种功能类似QQ，微信这中，一开始我在网上找这种功能但是这方面的开源出来的少之又少。于是就自己尝试着写，终于写出来了。现在我把写这个功能遇到的一些问题提出来，并把这个功能的demo也提出来，供大家参考与改进。先上一张图。
    首先我们获取到EditText的控件id,在oncreat()方法里设置mEditText.setFilters(newInputFilter[]{newMyInputFilter()});这里我们要创建MyInputFilter类实现InputFilter接口。这里实现这个接口就是为了监听在输入文本框里有@字符。贴出代码：
<span style="font-size:18px;"><pre name="code" class="java">  /**
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
    }</span>
       这里的goAt();方法就是跳转到你要选择人员列表界面，在这个方法里就会有一个问题：你要不要把显示在文本框中已选择人的id值传过去，到人员列表里进行筛选。如果是这样的话就会出现你传过去id的值会随着你@某人的id进行循环，我这里的a就是在文本框里获取到的id值。这样做有点麻烦，我干脆就不传值过去，选完某人返回值的时候再进行判断。继续看onActivityResult（）里的代码：
<span style="font-size:18px;">    /**
     * 存储@的cid、name对
     */
    private Map<String, String> cidNameMap = new HashMap<String, String>();</span>

           在这里我用了map集合来接收返回来的（key,value）key我接收的是id值，value接收的是name值。这里的resultCode != RESULT_OK注释是我之前把文本框里的id值传到人员列表界面进行判断返回的resultCode值，这个值表示的是文本框里有这个人名了就不再需要再把这个人名放进输入框，就直接return了。显然我不是按这种思路做的，我是按着直接接收到的人名与文本框里的人名进行比对，如果有相同的就return。当然这种方法会有一个问题，万一你的人员列表里有名字相同的呢？那你也可以用id值去判断，id是唯一了吧！
 <span style="font-size:18px;"><span style="font-size:18px;">/**
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
    }</span></span>
     这个保存id值在这里是没有用的，万一当用户在文本框里删掉一个已选的用户，那你这个selectedCids值是不会随之删掉的。当用户删掉一个已选的人名，它对应的id值也随之删掉的这个方法。我们要换一种思路，我们不必去纠结当用户删掉一个已选的人名，它对应的id值也随之删掉。我们要上传到服务器时再去统计文本框里已有人名的id值，根据map的键值对去查找。这里判断完名字后，就应该显示到输入框中了，这里我是用的SpannableString类去实现展示的文本，并且把人名转化为bitmap对象输入到文本框中。SpannableString是修改文本的样式，因为textview我们只能改得了大小、颜色。而SpannableString比textview设置文本的方法更多一些。这里不再详解自己百度。这里有个setAtImageSpan()方法。
    
<span style="font-size:18px;">  /**
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
    }</span>
   上面这个就是设置文字的样式，一体的背景为蓝色的，上面这些就可以满足展示到输入框我们需要的效果了。但是我们不仅要展示，也是把输入框中需要上传的id也能对应的找到。这里我也是用键值对，用人名去查找相对应的id值。请看方法：
    
<span style="font-size:18px;">    //上传需要的id值
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
    }</span>
    这里的upId是定义的一个全局变量String，用来接收的id值，多个是用逗号隔开的。但是当你发送文本成功后一定要在请求接口数据成功中把这个upId值赋值为upId=“”；不然你下次继续@某人时，上次的id值一直在upId中保存着的，这里请注意一下。
     整个@功能都差不多到这里了，基本能满足我们的需求。下面是我提出来的代码demo，可以下载下来进行改进或者使用。里面有一些注释的代码，我没删是为了可以再这基础上进行快速的改进。
     
