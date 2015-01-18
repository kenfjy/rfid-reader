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

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693ByteData;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

/**
 * ISO15693 Inventory Response Formatを抽象化したクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/22
 * @since Android API Level 10
 *
 */

public class InventoryResponse implements Parcelable, ISO15693ByteData {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<InventoryResponse> CREATOR = 
        new Parcelable.Creator<InventoryResponse>() {
            public InventoryResponse createFromParcel(Parcel in) {
                return new InventoryResponse(in);
            }
            
            public InventoryResponse[] newArray(int size) {
                return new InventoryResponse[size];
            }
        };
        
        
    public static class InventoryData implements Parcelable, ISO15693ByteData {
        public static final Parcelable.Creator<InventoryData> CREATOR = 
                new Parcelable.Creator<InventoryData>() {
                    public InventoryData createFromParcel(Parcel in) {
                        return new InventoryData(in);
                    }
                    
                    public InventoryData[] newArray(int size) {
                        return new InventoryData[size];
                    }
                };
        
        final byte mDSFID;      
        final UID mUID;
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public InventoryData(Parcel in) {
            mDSFID = in.readByte();
            mUID = new UID(in);
        }
        /**
         * コンストラクタ 
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public InventoryData(byte[] bytes) {
            mDSFID =  bytes[0];
            mUID = new UID(Arrays.copyOfRange(bytes, 1, bytes.length));
        }
        /* (non-Javadoc)
         * @see com.example.kenfujiyoshi.myapp01.iso15693.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            byte[] uidBytes = mUID.getBytes();
            ByteBuffer buff = ByteBuffer.allocate( 1 + uidBytes.length );
            buff.put(mDSFID).put(uidBytes);
            return buff.array();
        }
        /* (non-Javadoc)
         * @see android.os.Parcelable#describeContents()
         */
        @Override
        public int describeContents() {
            return 0;
        }
        /* (non-Javadoc)
         * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(mDSFID);
            mUID.writeToParcel(dest, flags);
        }
        
        /**
         * dSFIDを取得します
         * @return byte dSFIDが戻ります
         */
        public byte getDSFID() {
            return mDSFID;
        }
        /**
         * uIDを取得します
         * @return UID uIDが戻ります
         */
        public UID getUID() {
            return mUID;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("InventoryData [" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append("　DSFID:" + Util.getHexString(mDSFID) + "\n");
            sb.append(" " + mUID.toString() + "\n");
            return sb.toString();
        }
    }
    //byte SOF;   //ignore for Android NFC 
    protected final byte mFlags;
    protected final InventoryData[] mInventoryDatas;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public InventoryResponse(Parcel in) {
        mFlags = in.readByte();
        int length = in.readInt(); //配列の長さ
        mInventoryDatas = new InventoryData[length];
        
        for ( int i = 0; i < length; i++) {
            mInventoryDatas[i] = new InventoryData(in);
        }
        
    }
    /**
     * コンストラクタ 
     * @param bytes IDmの格納されているバイト列をセットします
     */
    public InventoryResponse(byte[] bytes) {
        mFlags =  bytes[0];

        int length = bytes.length / 9;
        mInventoryDatas = new InventoryData[length];
        
        int start = 0;
        for ( int i = 0; i < length; i++) {
            mInventoryDatas[i] = 
                    new InventoryData(Arrays.copyOfRange(bytes, start, start + 9));
            start = start + 9;
        }
        
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mFlags);
        for ( InventoryData d : mInventoryDatas ) {
            d.writeToParcel(dest, flags);
        }
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        ByteBuffer buff = ByteBuffer.allocate( 1 + (mInventoryDatas.length * 9));
        buff.put(mFlags);
        for ( InventoryData d : mInventoryDatas ) {
            buff.put(d.getBytes());
        }
        return buff.array();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResponseFormat [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append("　フラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append("　データ:"+ ")\n");
        for ( InventoryData d : mInventoryDatas ) {
            sb.append(d.toString());
        }
        return sb.toString();
    }
    /**
     * flagsを取得します
     * @return byte flagsが戻ります
     */
    public byte getFlags() {
        return mFlags;
    }
    /**
     * inventoryDatasを取得します
     * @return InventoryData[] inventoryDatasが戻ります
     */
    public InventoryData[] getInventoryDatas() {
        return mInventoryDatas;
    }
    
}
