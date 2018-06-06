package com.example.eng_mahnoud83coffey.embeatit.Interface;

import android.view.View;


//انتيرفيس callBack مشترك بستخدمه عشان اكشن عمليه الضغط على item معين
public interface ItemClickListener
{

    void onClick(View view,int position,boolean isLongClick);

}
