package com.example.kenfujiyoshi.myapp01.iso15693;

import android.nfc.tech.Ndef;
import android.nfc.tech.NfcV;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.kenfujiyoshi.myapp01.AbstractNfcTagFragment;
import com.example.kenfujiyoshi.myapp01.R;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693Exception;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693Tag;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadMultipleBlocksResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.SystemInformationResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.MemorySizeInfo;
import nfclib.com.example.kenfujiyoshi.myapp01.nfc.NfcTag;

/**
 * Created by Ken Fujiyoshi on 2014/09/01.
 */
public class ISO15693TagFragment extends AbstractNfcTagFragment {
    public static final String TAG = "ISO15693TagFragment";

    /**
     * @param activity
     */
    public ISO15693TagFragment(FragmentActivity activity) {
        super(activity, ISO15693TagFragment.TAG);

        //ISO15693は NFC-V
        mTechList = new String[][]{ new String[] { Ndef.class.getName(), NfcV.class.getName() }};
    }
    /**
     * ISO15693Tagクラスのインスタンスを生成します
     * @return ISO15693Tag 生成したISO15693Tagクラスのインスタンスが戻ります
     */
    public ISO15693Tag createTag() {
        return new ISO15693Tag(mNfcTag);
    }

    /* (non-Javadoc)
     * @see net.kazzz.AbstractNfcTagFragment#createNfcTag()
     */
    @Override
    public NfcTag createNfcTag() {
        return this.createTag();
    }
    /* (non-Javadoc)
     * @see net.kazzz.NfcTagFragment#dumpTagData()
     */
    @Override
    public String dumpTagData() {
        StringBuilder sb = new StringBuilder();
        try {
            // ISO15693Tag
            ISO15693Tag tag = this.createTag();
            if ( tag != null ) {
//                REVISED

// ======BEFORE======
//                sb.append("\n");
//                sb.append(getString(R.string.device_type, "ISO15693"));
//                sb.append("\n------------------------\n\n");
//                ISO15693Lib.ResponseFormat rf = tag.getSystemInformation();
//                sb.append(rf.toString());
//                sb.append("\n");
// ============

                //全てのブロックを読む
                SystemInformationResponse sysInfo = tag.getSystemInformation();
                if ( sysInfo == null || sysInfo.hasError()) {
                    throw new ISO15693Exception(
                            getString(R.string.get_system_info_failed, "ISO15693")
                                    + sysInfo.getErrorCode());
                }

                final MemorySizeInfo memInfo = sysInfo.getMemoryInfo();
                if ( memInfo == null || memInfo.getNumberOfBlocks() == 0) {
                    throw new ISO15693Exception(getString(R.string.get_memory_size_failed, "ISO15693"));
                }
                ReadMultipleBlocksResponse resp =
                        tag.readMultipleBlocks((byte)0, memInfo.getBlockSize(), memInfo.getNumberOfBlocks());
//                REVISED
// =======BEFORE=======
//                sb.append("  " + resp.toString());
//                sb.append("\n------------------------\n\n");
// =======AFTER=======
                sb.append(resp.toString());
                sb.append("?q=");
                sb.append(tag.getSystemInformation().getUID().toId());
// ==============
                Log.v("TAG_ID", sb.toString());

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
}
