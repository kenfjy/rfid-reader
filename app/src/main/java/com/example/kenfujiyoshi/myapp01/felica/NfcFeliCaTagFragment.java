package com.example.kenfujiyoshi.myapp01.felica;

import android.nfc.tech.NfcF;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.kenfujiyoshi.myapp01.AbstractNfcTagFragment;
import com.example.kenfujiyoshi.myapp01.R;
import com.example.kenfujiyoshi.myapp01.felica.suica.Suica;

import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaException;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaLiteTag;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.FeliCaTag;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.command.ReadResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.IDm;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.MemoryConfigurationBlock;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.ServiceCode;
import nfclib.com.example.kenfujiyoshi.myapp01.felica.lib.FeliCaLib.SystemCode;
import nfclib.com.example.kenfujiyoshi.myapp01.nfc.NfcTag;

/**
 * Created by Ken Fujiyoshi on 2014/09/01.
 */
public class NfcFeliCaTagFragment extends AbstractNfcTagFragment {
    public static final String TAG = "NfcFeliCaTagFragment";

    /**
     * コンストラクタ
     * @param activity アクティビティをセット
     */
    public NfcFeliCaTagFragment(FragmentActivity activity) {
        super(activity, NfcFeliCaTagFragment.TAG);

        // FeliCa及びFeliCaLiteは NFC-F のみ
        mTechList = new String[][]{ new String[] { NfcF.class.getName() }};
    }
    /**
     * FeliCa Liteデバイスか否かを検査します
     * @return boolean 読み込み対象がFeliCa Liteの場合trueが戻ります
     * @throws FeliCaException
     */
    public boolean isFeliCaLite()  {
        FeliCaTag f = new FeliCaTag(mNfcTag);
        //polling は IDm、PMmを取得するのに必要
        IDm idm;
        try {
            idm = f.pollingAndGetIDm(FeliCaLib.SYSTEMCODE_FELICA_LITE);
        } catch (FeliCaException e) {
            return false;
        }
        return idm != null;
    }
    /**
     * FeliCatTagクラスのインスタンスを生成します
     * @return FeliCaTag 生成したFeliCaTagクラスのインスタンスが戻ります
     */
    public FeliCaTag createFeliCaTag() {
        return new FeliCaTag(mNfcTag);
    }
    /**
     * FeliCaLiteTagクラスのインスタンスを生成します
     * @return FeliCaLiteTag 生成したFeliCaLiteTagクラスのインスタンスが戻ります
     */
    public FeliCaLiteTag createFeliCaLiteTag() {
        return new FeliCaLiteTag(mNfcTag);
    }
    /* (non-Javadoc)
     * @see net.kazzz.AbstractNfcTagFragment#createNfcTag()
     */
    @Override
    public NfcTag createNfcTag() {
        return this.isFeliCaLite()
                ? this.createFeliCaLiteTag()
                : this.createFeliCaTag();
    }
    /* (non-Javadoc)
     * @see net.kazzz.NfcTagFragment#dumpTagData()
     */
    @Override
    public String dumpTagData() {
        StringBuilder sb = new StringBuilder();
        try {
            if ( this.isFeliCaLite() ) {
                sb.append("\n");
                sb.append(getString(R.string.device_type, "FeliCa Lite"));
                sb.append("\n------------------------\n\n");

                // FeliCa Lite 読み込み
                FeliCaLiteTag ft =  this.createFeliCaLiteTag();
                ft.polling();
                sb.append("  " + ft.toString());
                sb.append("\n------------------------\n\n");

                //0ブロック目読み込み
                ReadResponse rr = ft.readWithoutEncryption((byte)0);
                sb.append("  " + rr.toString());
                sb.append("\n------------------------\n\n");

                //MemoryConfig 読み込み
                MemoryConfigurationBlock mb = ft.getMemoryConfigBlock();
                sb.append("  " + mb.toString());
                sb.append("\n------------------------\n\n");

                String result = sb.toString();
                Log.d(TAG, result);
                return result;
            }

            // FeliCa
            FeliCaTag ft = this.createFeliCaTag();
            IDm idm = ft.pollingAndGetIDm(FeliCaLib.SYSTEMCODE_ANY);
            if ( idm != null ) {
                sb.append("\n");
                sb.append(getString(R.string.device_type, "FeliCa"));
                sb.append("\n------------------------\n\n");
                sb.append(ft.toString());

                // enum systemCode
                sb.append("\n");
                sb.append("  " + getString(R.string.system_code_list));
                sb.append("\n------------------------\n\n");
                SystemCode[] scs = ft.getSystemCodeList();
                for ( SystemCode sc : scs ) {
                    sb.append("  ").append(sc.toString()).append("\n");
                }

                // enum serviceCode
                sb.append("\n");
                sb.append("  " + getString(R.string.service_code_list));
                sb.append("\n------------------------\n\n");
                ServiceCode[] svs = ft.getServiceCodeList();
                for ( ServiceCode sc : svs ) {
                    sb.append("  ").append(sc.toString()).append("\n");
                }
            } else {
                sb.append(R.string.device_read_failed);
            }

        } catch (Exception e) {
            String result = sb.toString();
            Log.d(TAG, result);
            e.printStackTrace();
            return result;
        }
        String result = sb.toString();
        Log.d(TAG, result);
        return result;
    }
    /**
     * FeliCa 使用履歴をダンプします
     * @return
     */
    public String dumpFeliCaHistoryData() throws Exception {
        try {
            if ( this.isFeliCaLite() ) {
                throw new FeliCaException("Tag is not FeliCa (maybe FeliCaLite)");
            }
            FeliCaTag f = this.createFeliCaTag();

            //polling は IDm、PMmを取得するのに必要
            f.polling(FeliCaLib.SYSTEMCODE_PASMO);

            //read
            ServiceCode sc = new ServiceCode(FeliCaLib.SERVICE_SUICA_HISTORY);
            byte addr = 0;
            ReadResponse result = f.readWithoutEncryption(sc, addr);

            StringBuilder sb = new StringBuilder();
            while ( result != null && result.getStatusFlag1() == 0  ) {
                sb.append("履歴 No.  " + (addr + 1) + "\n");
                sb.append("---------\n");
                sb.append("\n");
                Suica.History s = new Suica.History(result.getBlockData(), this.getActivity());
                sb.append(s.toString());
                sb.append("\n------------------------\n\n");

                addr++;
                //Log.d(TAG, "addr = " + addr);
                result = f.readWithoutEncryption(sc, addr);
            }

            String str = sb.toString();
            Log.d(TAG, str);
            return str;
        } catch (FeliCaException e) {
            e.printStackTrace();
            Log.e(TAG, "readHistoryData", e);
            throw e;
        }
    }

}
