package songwei520.map3synthesis1.customView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import songwei520.map3synthesis1.R;

public class MProgressDialog extends Dialog {
    private Context context = null;
    private static MProgressDialog mProgressDialog = null;
     
    public MProgressDialog(Context context){
        super(context);
        this.context = context;
    }
     
    public MProgressDialog(Context context, int theme) {
        super(context, theme);
    }
     
    public static MProgressDialog createDialog(Context context){
        mProgressDialog = new MProgressDialog(context, R.style.MProgressDialog);
        mProgressDialog.setContentView(R.layout.mprogressdialog); 
        mProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
         
        return mProgressDialog;
    }
  
    public void onWindowFocusChanged(boolean hasFocus){
         
        if (mProgressDialog == null){
            return;
        }
         
        ImageView imageView = (ImageView) mProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
  
    /**
     *
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public MProgressDialog setTitile(String strTitle){
        return mProgressDialog;
    }
     
    /**
     *
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public MProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)mProgressDialog.findViewById(R.id.id_tv_loadingmsg);
         
        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }
         
        return mProgressDialog;
    }
}