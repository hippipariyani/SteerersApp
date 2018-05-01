//package com.heisen_berg.steerersapp.ui;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Typeface;
//import android.os.Build;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.heisen_berg.steerersapp.R;
//import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
//
//
///**
// * Created by heisen-berg on 21/02/18.
// */
//
//public class SweetAlertDialogIosFont extends SweetAlertDialog implements DialogInterface.OnShowListener {
//
//    public SweetAlertDialogIosFont(Context context) {
//        super(context);
//        setOnShowListener(this);
//    }
//
//    public SweetAlertDialogIosFont(Context context, int alertType) {
//        super(context, alertType);
//        setOnShowListener(this);
//    }
//
//    @Override
//    public void onShow(DialogInterface dialogInterface) {
//        SweetAlertDialog sweetAlertDialog = (SweetAlertDialog) dialogInterface;
//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/ios_font_regular.otf");
//        TextView title = (TextView) sweetAlertDialog.findViewById(R.id.title_text);
//        TextView content = (TextView) sweetAlertDialog.findViewById(R.id.content_text);
//        Button confirmButton = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
//        Button cancleButton = (Button) sweetAlertDialog.findViewById(R.id.cancel_button);
//        title.setTypeface(tf);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            title.setLetterSpacing(0.02f);
//        }
//        content.setTypeface(tf);
//        confirmButton.setTypeface(tf);
//        cancleButton.setTypeface(tf);
//    }
//}
