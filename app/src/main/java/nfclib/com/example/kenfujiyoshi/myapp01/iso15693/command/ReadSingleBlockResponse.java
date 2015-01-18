/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.ByteBuffer;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ResponseFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.ERROR_DETECT_ERROR;

/**
 * ReadSingleBlock実行時のレスポンスを抽象化したクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/13
 * @since Android API Level 10
 *
 */

public class ReadSingleBlockResponse extends ResponseFormat {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<ReadSingleBlockResponse> CREATOR = 
        new Parcelable.Creator<ReadSingleBlockResponse>() {
            public ReadSingleBlockResponse createFromParcel(Parcel in) {
                return new ReadSingleBlockResponse(in);
            }
            
            public ReadSingleBlockResponse[] newArray(int size) {
                return new ReadSingleBlockResponse[size];
            }
        };
    final byte mBlockSecurityStatus;
    final byte[] mData;
   /**
     * コンストラクタ 
     * @param bytes バイト列をセット
     */
    public ReadSingleBlockResponse(byte[] bytes) {
        super(bytes);
        if ( hasError() ) { 
            mBlockSecurityStatus = 0;
        } else {
            mBlockSecurityStatus = bytes[1];
        }
        mData = Arrays.copyOfRange(bytes, 1, bytes.length);
    }
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public ReadSingleBlockResponse(Parcel in) {
        super(in);
        if ( hasError() ) { 
            mBlockSecurityStatus = 0;
        } else {
            mBlockSecurityStatus = in.readByte();
        }
        mData = new byte[in.readInt()];
        in.readByteArray(mData);
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if ( hasError() ) {
            //mBlockSecurityStatus = 0;
        } else {
            dest.writeByte(mBlockSecurityStatus);
            //配列長を先に書きだしておく
            dest.writeInt(mData.length);
            dest.writeByteArray(mData);
        }
        
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate( superData.length + 1 + mData.length);
        buff.put(superData);
        if ( !hasError() ) {
            buff.put(mBlockSecurityStatus).put(mData);
        }
        return buff.array();
    }
    /**
     * blockSecurityStatusを取得します
     * @return byte blockSecurityStatusが戻ります
     */
    public byte getBlockSecurityStatus() {
        return mBlockSecurityStatus;
    }
    /**
     * ブロックがロックされているか否かを検査します
     * @return boolean ロックされている場合はtrueが戻ります
     */
    public boolean isLocked() {
        return ((mBlockSecurityStatus & ERROR_DETECT_ERROR ) & 0xFF) == ERROR_DETECT_ERROR;
    }
    /**
     * dataを取得します
     * @return byte[] dataが戻ります
     */
    public byte[] getData() {
        return mData;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // KEN
        sb.append("ReadSingleBlockResponse [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append("　オプションフラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append("　エラーコード:" +   Util.getHexString(mErrorCode) + "(" + ISO15693Lib.ErrorCode.errorMap.get(mErrorCode) + ")\n");
        sb.append("　ブロックセキュリティステータス:" + Util.getHexString(mBlockSecurityStatus) + "\n");
        sb.append("　データ:" + Util.getHexString(mData) + "\n");
        if (Integer.parseInt(Util.getHexString(mErrorCode)) == 0) {
            for (int i = 0; i < mData.length - 1; i++) {
                sb.append(mData.toString());
            }
        }
        return sb.toString();
    }
}
