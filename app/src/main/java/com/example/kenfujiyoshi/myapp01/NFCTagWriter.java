package com.example.kenfujiyoshi.myapp01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.example.kenfujiyoshi.myapp01.felica.NfcFeliCaTagFragment;
import com.example.kenfujiyoshi.myapp01.iso15693.ISO15693TagFragment;
import com.example.kenfujiyoshi.myapp01.task.FeliCaLiteWriteTask;
import com.example.kenfujiyoshi.myapp01.task.ISO15693WriteTask;

import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaException;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaLiteTag;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.command.ReadResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.IDm;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.MemoryConfigurationBlock;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693Exception;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693Tag;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadMultipleBlocksResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadMultipleBlocksResponse.BlockData;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.SystemInformationResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.MemorySizeInfo;
import nfclib.com.example.kenfujiyoshi.myapp01.util.ArrayUtil;


/**
 * Created by Ken Fujiyoshi on 2014/09/01.
 */
public class NFCTagWriter extends FragmentActivity implements View.OnClickListener {
    private String TAG = "NFCTagWriter";

    // FeliCaLiteのブロック番号の規定値 ( S_PAD0～13 )
    private final static String[] blockNums_FelicaLite = new String[] {
            "S_PAD0", "S_PAD1", "S_PAD2", "S_PAD3",
            "S_PAD4", "S_PAD5", "S_PAD6", "S_PAD7",
            "S_PAD8", "S_PAD9", "S_PAD10", "S_PAD11",
            "S_PAD12", "S_PAD13"
    };

    /**
     * リストビューに表示するデータを保持するホルダクラスを提供します
     *
     * @date 2011/02/22
     * @since Android API Level 9
     *
     */
    public class ViewHolder {
        String blockName;
        boolean isWritable;
        String accessMode;
        byte[] data;
    }

    private Tag mNfcTag;
    private NfcFeliCaTagFragment mFeliCafragment;
    private ISO15693TagFragment mISO15693Fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //IMEを自動起動しない
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //レイアウトのインフレート
        setContentView(R.layout.writer);

        final Button btnWrite = (Button) findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(this);
        btnWrite.setEnabled(false);

        final EditText editWrite = (EditText) findViewById(R.id.edit_write);
        editWrite.setEnabled(false);

        final ListView listMemBlock = (ListView) findViewById(R.id.list_memblock);

        final CheckBox chkUseNDEF = (CheckBox)  findViewById(R.id.chk_useNDEF);

        Intent intent = getIntent();
        this.mNfcTag = (Tag)intent.getParcelableExtra("nfcTag");

        if ( isFeliCaLiteTag(mNfcTag) ) {
            //FeliCaLite
            chkUseNDEF.setVisibility(View.GONE);
            mFeliCafragment = new NfcFeliCaTagFragment(this);
            prepareFeliCaLite(btnWrite, editWrite, listMemBlock);
        } else
        if ( isISO15693Tag(mNfcTag) ) {
            //ISO15693
            chkUseNDEF.setVisibility(View.VISIBLE);
            mISO15693Fragment = new ISO15693TagFragment(this);
            prepareISO15693(btnWrite, editWrite, listMemBlock);
        }
    }
    /**
     * タグの種別がFeliCa(Lite)か否かを検査します
     *
     * @param tag タグをセット
     * @return boolean タグがFeliCaliteの場合trueが戻ります
     */
    private boolean isFeliCaLiteTag(Tag tag) {
        String[] techs = this.mNfcTag.getTechList();
        return ArrayUtil.contains(techs, NfcF.class.getName());
    }
    /**
     * タグの種別がISO15693か否かを検査します
     *
     * @param tag タグをセット
     * @return boolean タグがFeliCaliteの場合trueが戻ります
     */
    private boolean isISO15693Tag(Tag tag) {
        String[] techs = this.mNfcTag.getTechList();
        return ArrayUtil.contains(techs, NfcV.class.getName());
    }


    /**
     * ISO15693 Tagのための準備処理を実行します
     *
     *
     * @param btnWrite 書き込みボタンの参照をセット
     * @param editWrite 編集ボタンの参照をセット
     * @param listMemBlock　メモリブロックを表示するリストビューをセット
     * @param useNDEF　NDEFフォーマットを使用するか否かをセット
     */
    private void prepareISO15693(final Button btnWrite, final EditText editWrite,
                                 final ListView listMemBlock) {
        final CheckBox chkUseNDEF = (CheckBox)  findViewById(R.id.chk_useNDEF);

        //データ読み込み
        ISO15693Tag ft = new ISO15693Tag(this.mNfcTag);
        try {
            SystemInformationResponse sysInfo = ft.getSystemInformation();
            if ( sysInfo == null || sysInfo.hasError()) {
                throw new ISO15693Exception(
                        "ISO15693 デバイスからシステム情報を取得できませんでした : "
                                + sysInfo.getErrorCode());
            }

            final MemorySizeInfo memInfo = sysInfo.getMemoryInfo();
            if ( memInfo == null || memInfo.getNumberOfBlocks() == 0) {
                throw new ISO15693Exception("ISO15693 メモリサイズ情報を取得できませんでした");
            }

            chkUseNDEF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listMemBlock.setEnabled(!chkUseNDEF.isChecked());
                    listMemBlock.setClickable(!chkUseNDEF.isChecked());
                    //NDEFと非NDEFで書込みのガイドとサイズを変える
                    if ( chkUseNDEF.isChecked() ) {
                        btnWrite.setEnabled(true);
                        editWrite.setEnabled(true);
                        //NDEFの場合、ヘッダに10byte消費する
                        editWrite.setHint((memInfo.getBlockSize() *  memInfo.getNumberOfBlocks()) -10
                                + "バイト以内で入力");
                    } else {
                        if ( editWrite.getText().length() > 0 ) {
                            btnWrite.setEnabled(true);
                            editWrite.setEnabled(true);
                        } else {
                            btnWrite.setEnabled(false);
                            editWrite.setEnabled(false);
                        }
                        editWrite.setHint(memInfo.getBlockSize() + "バイト以内で入力");
                    }
                }
            });

            ReadMultipleBlocksResponse rbResp = ft.readMultipleBlocks((byte)0
                    , memInfo.getBlockSize(), memInfo.getNumberOfBlocks());
            if ( rbResp == null || rbResp.hasError()) {
                throw new ISO15693Exception(
                        "ISO15693 ReadMultipleBlockコマンドが失敗しました"
                                + sysInfo.getErrorCode());
            }

            //メモリブロックを全て読み込んで配列に保存
            ViewHolder[] holders = new ViewHolder[memInfo.getNumberOfBlocks()];
            for ( byte i = 0; i <  memInfo.getNumberOfBlocks(); i++) {
                BlockData bd = rbResp.getBlockDatas()[i];

                ViewHolder holder = new ViewHolder();
                holder.blockName = "block " + i;
                holder.isWritable = !bd.isLocked();
                holder.accessMode = bd.isLocked() ? "(Lock)" : "(UnLock)";

                //メモリブロックの内容を取得
                if ( bd.getData() != null && bd.getData().length > 0) {
                    holder.data = bd.getData();
                } else {
                    holder.data =new byte[memInfo.getBlockSize()];
                    for ( int b = 0; b < holder.data.length; b++ ) {
                        holder.data[b] = 0x00; //空データで埋める
                    }
                }
                holders[i] = holder;
            }
            final LayoutInflater layoutInflater =
                    (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //ArrayAdapter構成 (ビューの編集をオーバライド
            ArrayAdapter<ViewHolder> adapter =
                    new ArrayAdapter<ViewHolder>(this, android.R.layout.simple_list_item_multiple_choice, holders) {

                        /* (non-Javadoc)
                         * @see android.widget.BaseAdapter#isEnabled(int)
                         */
                        @Override
                        public boolean isEnabled(int position) {
                            SparseBooleanArray arry = listMemBlock.getCheckedItemPositions();
                            if ( arry.indexOfValue(true) != -1
                                    && !arry.get(position) //既に選択されていない
                                    &&  ( !arry.get(position-1) && !arry.get(position+1) ) ) //連続していない
                            {
                                //不連続は選択させない
                                return false;
                            }
                            return super.isEnabled(position);
                        }

                        /* (non-Javadoc)
                         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
                         */
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {

                            if (null == convertView) {
                                convertView =
                                        layoutInflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
                            }

                            ViewHolder holder = this.getItem(position);

                            if ( holder != null ) {

                                StringBuilder sb = new StringBuilder();
                                sb.append(holder.blockName).append(" ").append(holder.accessMode).append("\n");
                                if ( holder.data != null && holder.data.length > 0 ) {
                                    //UTF-8でエンコード
                                    Charset utfEncoding = Charset.forName("UTF-8");
                                    sb.append("データ : ").append(new String(holder.data, utfEncoding).trim());
                                }
                                ((TextView)convertView).setText(sb.toString());
                            }
                            return convertView;
                        }

                    };


            listMemBlock.setAdapter(adapter);

            // フォーカスが当たらないよう設定
            //listMemBlock.setItemsCanFocus(false);
            //選択モードを変更
            listMemBlock.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            listMemBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    //選択中の行の文字列を束ねる
                    SparseBooleanArray arry = listMemBlock.getCheckedItemPositions();

                    ByteBuffer buff = ByteBuffer.allocate(arry.size() * memInfo.getBlockSize());
                    int writableBlockCount = 0;
                    for ( int i = 0; i < arry.size(); i++ ) {
                        int key = arry.keyAt(i);
                        //選択されている行のインデクス
                        Object o = parent.getItemAtPosition(key);
                        if ( o != null && o instanceof ViewHolder ) {
                            ViewHolder holder = (ViewHolder)o;
                            if ( holder.isWritable ) {
                                writableBlockCount++;
                                if ( holder.data != null && holder.data.length > 0) {
                                    buff.put(holder.data);
                                }
                            }
                        }
                    }
                    //UTF-8でエンコード
                    StringBuilder sb = new StringBuilder();
                    Charset utfEncoding = Charset.forName("UTF-8");
                    sb.append( new String(buff.array(), utfEncoding).trim() );

                    editWrite.setHint(memInfo.getBlockSize() * writableBlockCount  + "バイト以内で入力");

                    if ( writableBlockCount > 0 ) {
                        editWrite.setText( sb.toString().trim() );
                        editWrite.setTag(arry);
                        //書きこみ可能な場合、ビューを有効にするわさ
                        btnWrite.setEnabled(true);
                        editWrite.setEnabled(true);
                    } else {
                        btnWrite.setEnabled(false);
                        editWrite.setEnabled(false);
                    }

                }
            });
        } catch (ISO15693Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**
     * FeliCaLite Tagのための準備処理を実行します
     *
     * @param btnWrite 書き込みボタンの参照をセット
     * @param editWrite 編集ボタンの参照をセット
     * @param listMemBlock　メモリブロックを表示するリストビューをセット
     */
    private void prepareFeliCaLite(final Button btnWrite
            , final EditText editWrite, final ListView listMemBlock) {
        //データ読み込み
        ViewHolder[] holders = new ViewHolder[14];
        FeliCaLiteTag ft = new FeliCaLiteTag(this.mNfcTag);
        try {
            IDm idm = ft.pollingAndGetIDm();
            if ( idm == null ) {
                throw new FeliCaException("FeliCa Lite デバイスからIDmを取得できませんでした");
            }
            //MemoryConfig 読み込み
            MemoryConfigurationBlock mb = ft.getMemoryConfigBlock();

            //スクラッチパッドのブロックを全て読み込んで配列に保存
            for ( byte i = 0; i < 14; i++) {
                ViewHolder holder = new ViewHolder();
                holder.blockName = blockNums_FelicaLite[i];
                holder.isWritable = mb.isWritable(i);
                holder.accessMode = mb.isWritable(i) ? "(R/W)" : "(RO)";

                //メモリブロックの内容を取得
                ReadResponse rr = ft.readWithoutEncryption(i);
                if ( rr != null && rr.getStatusFlag1() == 0) {
                    holder.data = rr.getBlockData();
                }
                holders[i] = holder;
            }
        } catch (FeliCaException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        final LayoutInflater layoutInflater =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //ArrayAdapter構成 (ビューの編集をオーバライド
        ArrayAdapter<ViewHolder> adapter =
                new ArrayAdapter<ViewHolder>(this, android.R.layout.simple_list_item_1, holders) {

                    /* (non-Javadoc)
                     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
                     */
                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {

                        if (null == convertView) {
                            convertView =
                                    layoutInflater.inflate(android.R.layout.simple_list_item_1, null);
                        }

                        ViewHolder holder = this.getItem(position);

                        if ( holder != null ) {

                            StringBuilder sb = new StringBuilder();
                            sb.append(holder.blockName).append(" ").append(holder.accessMode).append("\n");
                            if ( holder.data != null && holder.data.length > 0 ) {
                                //UTF-8でエンコード
                                Charset utfEncoding = Charset.forName("UTF-8");
                                sb.append("データ : ").append(new String(holder.data, utfEncoding).trim());
                            }
                            ((TextView)convertView).setText(sb.toString());
                        }
                        return convertView;
                    }

                };


        listMemBlock.setAdapter(adapter);

        // 行が選択され際にデータをEditTextに転送
        listMemBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = parent.getItemAtPosition(position);

                if ( o != null && o instanceof ViewHolder ) {
                    ViewHolder holder = (ViewHolder)o;
                    if ( holder.data != null && holder.data.length > 0 ) {
                        //UTF-8でエンコード
                        Charset utfEncoding = Charset.forName("UTF-8");
                        editWrite.setText( new String(holder.data, utfEncoding).trim() );
                        editWrite.setTag(position);
                    }

                    //書きこみ可能な場合、ビューを有効にするわさ
                    btnWrite.setEnabled(holder.isWritable);
                    editWrite.setEnabled(holder.isWritable);
                } else {
                    btnWrite.setEnabled(false);
                    editWrite.setEnabled(false);
                }

            }
        });
    }

    /**
     * 書き込みボタンが押下された際の処理
     * 実際にVICCにデータを書きこみます
     *
     */
    public void onClick(final View v) {
        try {
            final int id = v.getId();
            if ( id != R.id.btn_write ) return;

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);

            AsyncTask<Void, Void, ?> writeDataTask = null;
            if ( isFeliCaLiteTag(mNfcTag) ) {
                // FeliCatLite用
                writeDataTask = new FeliCaLiteWriteTask(this,  mNfcTag);
            } else

            if ( isISO15693Tag(mNfcTag) ) {
                final CheckBox chkUseNDEF = (CheckBox)  findViewById(R.id.chk_useNDEF);
                //ISO15693用
                writeDataTask = new ISO15693WriteTask(this,  mNfcTag, chkUseNDEF.isChecked());
            }
            // 非同期タスク実行
            writeDataTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "writeData", e);
            Toast.makeText(v.getContext()
                    , "書きこみ失敗 : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
