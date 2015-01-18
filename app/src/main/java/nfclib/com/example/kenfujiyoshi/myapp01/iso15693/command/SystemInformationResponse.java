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

import java.nio.ByteBuffer;
import java.util.Arrays;

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.*;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ErrorCode;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.MemorySizeInfo;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ResponseFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * GetSystemInformation実行時のレスポンスを抽象化したクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/13
 * @since Android API Level 10
 *
 */

public class SystemInformationResponse extends ResponseFormat {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<SystemInformationResponse> CREATOR = 
        new Parcelable.Creator<SystemInformationResponse>() {
            public SystemInformationResponse createFromParcel(Parcel in) {
                return new SystemInformationResponse(in);
            }
            
            public SystemInformationResponse[] newArray(int size) {
                return new SystemInformationResponse[size];
            }
        };
    protected final byte mInfoFlags;
    protected final UID mUID;
    protected final byte mDSFID;
    protected final byte mAFI;
    protected final MemorySizeInfo mMemoryInfo;
   /**
     * コンストラクタ 
     * @param bytes バイト列をセット
     */
    public SystemInformationResponse(byte[] bytes) {
        super(bytes);
        if ( hasError() ) {
            mInfoFlags = 0;
            mUID = null;
            mDSFID = 0;
            mAFI = 0;
            mMemoryInfo = null;
        } else {
            mInfoFlags = bytes[1]; 
            mUID = new UID(Arrays.copyOfRange(bytes, 2, 10));

            int index = 10;
            if ( (mInfoFlags & DSFID_SUPPORTED) == DSFID_SUPPORTED ) {
                mDSFID = bytes[index];
                index++;
            } else {
                mDSFID = 0;
            }

            if ( (mInfoFlags & AFI_SUPPORTED) == AFI_SUPPORTED ) {
                mAFI =  bytes[index];
                index++;
            } else {
                mAFI = 0;
            }

            if ( (mInfoFlags & VICC_MEMORYSIZE_SUPPORTED) == VICC_MEMORYSIZE_SUPPORTED ) {
                mMemoryInfo = new MemorySizeInfo(Arrays.copyOfRange(bytes, index, index+2));
            } else {
                mMemoryInfo = null;
            }
            
        }
    }
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public SystemInformationResponse(Parcel in) {
        super(in);
        if ( hasError() ) {
            mInfoFlags = 0; 
            mUID = null;
            mDSFID = 0;
            mAFI = 0;
            mMemoryInfo = null;
        } else {
            mInfoFlags = in.readByte(); 
            mUID = new UID(in);
            
            if ( (mInfoFlags & DSFID_SUPPORTED) == DSFID_SUPPORTED ) {
                mDSFID = in.readByte();
            } else {
                mDSFID = 0;
            }

            if ( (mInfoFlags & AFI_SUPPORTED) == AFI_SUPPORTED ) {
                mAFI = in.readByte();
            } else {
                mAFI = 0;
            }

            if ( (mInfoFlags & VICC_MEMORYSIZE_SUPPORTED) == VICC_MEMORYSIZE_SUPPORTED ) {
                mMemoryInfo = MemorySizeInfo.CREATOR.createFromParcel(in);  
            } else {
                mMemoryInfo = null;
            }

            if ( (mInfoFlags & IC_REFERENCE_SUPPORTED) == IC_REFERENCE_SUPPORTED ) {
                // 使わない
            }
        }
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
            dest.writeByteArray(mUID.getBytes());
            dest.writeByte(mDSFID);
            dest.writeByte(mAFI);
            mMemoryInfo.writeToParcel(dest, flags);
        }
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate( superData.length 
                + mUID.getBytes().length +  1 + 1 + mMemoryInfo.getBytes().length);
        
        if ( hasError()) {
        } else {
            buff.put(superData)
                .put(mUID.getBytes())
                .put(mDSFID)
                .put(mAFI)
                .put(mMemoryInfo.getBytes());
        }
        return buff.array();
    }
    
    /**
     * uIDを取得します
     * @return UID uIDが戻ります
     */
    public UID getUID() {
        return mUID;
    }
    /**
     * dSFIDを取得します
     * @return byte dSFIDが戻ります
     */
    public byte getDSFID() {
        return mDSFID;
    }
    /**
     * aFIを取得します
     * @return byte aFIが戻ります
     */
    public byte getAFI() {
        return mAFI;
    }
    /**
     * fieldを取得します
     * @return byte[] fieldが戻ります
     */
    public MemorySizeInfo getMemoryInfo() {
        return mMemoryInfo;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GetSystemInformationResponse [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append("　フラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append("　インフォメーションフラグ:" + Util.getHexString(mInfoFlags) + "\n");
        sb.append("　エラーコードe:" +   Util.getHexString(mErrorCode) + "(" + ErrorCode.errorMap.get(mErrorCode) + ")\n");
        if ( mUID != null ) {
            sb.append(mUID.toString());
        }
        sb.append("　DSFID:" + Util.getHexString(mDSFID) + "\n");
        sb.append("　AFI:" + Util.getHexString(mAFI) + "\n");
        if ( mMemoryInfo != null ) {
            sb.append(mMemoryInfo.toString());
        }
        return sb.toString();
    }
}
