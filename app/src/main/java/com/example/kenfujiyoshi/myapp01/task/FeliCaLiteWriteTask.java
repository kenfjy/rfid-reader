package com.example.kenfujiyoshi.myapp01.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kenfujiyoshi.myapp01.R;

import java.nio.charset.Charset;

import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaException;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaLiteTag;

/**
 * Created by Ken Fujiyoshi on 2014/09/01.
 */
public class FeliCaLiteWriteTask extends AsyncTask<Void, Void, String> {
    private final static String TAG =  FeliCaLiteWriteTask.class.getSimpleName();
    private Activity mActivity;
    private Tag mNfcTag;
    private ProgressDialog mDialog;

    /**
     * コンストラクタ
     * @param activity アクティビティをセット
     * @param nfcTag NFCTagをセット
     */
    public FeliCaLiteWriteTask(Activity activity, Tag nfcTag) {
        mActivity = activity;
        mNfcTag = nfcTag;
        mDialog = new ProgressDialog(mActivity);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
    }
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        mDialog.setMessage("書き込み処理を実行中です...");
        mDialog.show();
    }
    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            // FeliCaLite データ書き出し
            final EditText editWrite = (EditText) mActivity.findViewById(R.id.edit_write);
            final CharSequence cData = editWrite.getText();
            byte addr = (byte) (((Integer)editWrite.getTag()) & 0xff);
            FeliCaLiteTag f = new FeliCaLiteTag(mNfcTag);

            //データはUTF-8でエンコード
            Charset utfEncoding = Charset.forName("UTF-8");
            String result = cData.toString();
            byte[] textBytes = cData.toString().getBytes(utfEncoding);

            if ( f.writeWithoutEncryption(addr, textBytes).getStatusFlag1() == 0 ) {
                return result;
            } else {
                return null;
            }

        } catch (FeliCaException e) {
            e.printStackTrace();
            Log.e(TAG, "writeData", e);
            Toast.makeText(mActivity
                    , "書きこみ失敗 : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        if ( mDialog != null ) mDialog.dismiss();
        if ( result != null && result.length() > 0) {
            //tv_tag.setText(readData());
            Toast.makeText(mActivity
                    , "書きこみ成功 : " + result.toString() , Toast.LENGTH_LONG).show();

            //終了して自身を起動 (リフレッシュ)
            mActivity.finish();
            Intent intent = new Intent(mActivity, mActivity.getClass());
            intent.putExtra("nfcTag", mNfcTag);
            mActivity.startActivity(intent);
        }
    }
}
