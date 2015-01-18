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
import java.nio.charset.Charset;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693ByteData;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.ERROR_DETECT_ERROR;

/**
 * ReadMultipleBlockResponse実行時のレスポンスを抽象化したクラスを提供します
 *
 * @author Kazzz
 * @date 2011/07/13
 * @since Android API Level 10
 *
 */

public class ReadMultipleBlocksResponse extends ReadSingleBlockResponse {
    /** Parcelable need CREATOR field **/
    public static final Parcelable.Creator<ReadMultipleBlocksResponse> CREATOR =
            new Parcelable.Creator<ReadMultipleBlocksResponse>() {
                public ReadMultipleBlocksResponse createFromParcel(Parcel in) {
                    return new ReadMultipleBlocksResponse(in);
                }

                public ReadMultipleBlocksResponse[] newArray(int size) {
                    return new ReadMultipleBlocksResponse[size];
                }
            };
    /**
     *
     * 読み込んだブロックのデータクラスを提供します
     *
     * @author Copyright c 2011-2012 All Rights Reserved.
     * @date 2011/07/17
     * @since Android API Level 10
     *
     */
    public static class BlockData implements Parcelable, ISO15693ByteData {
        public static final Parcelable.Creator<BlockData> CREATOR =
                new Parcelable.Creator<BlockData>() {
                    public BlockData createFromParcel(Parcel in) {
                        return new BlockData(in);
                    }

                    public BlockData[] newArray(int size) {
                        return new BlockData[size];
                    }
                };
        final byte mSecurityStatus;
        final byte[] mData;
        public BlockData(byte securityStatus, byte[] data) {
            mSecurityStatus = securityStatus;
            mData = data;
        }
        /**
         * コンストラクタ
         * @param data データ
         */
        public BlockData(byte[] data) {
            mSecurityStatus = data[0];
            mData = Arrays.copyOfRange(data, 1, data.length);
        }
        /**
         * コンストラクタ
         * @param in データ
         */
        public BlockData(Parcel in) {
            mSecurityStatus = in.readByte();
            mData = new byte[in.readByte()];
            in.readByteArray(mData);
        }
        /* (non-Javadoc)
         * @see com.example.kenfujiyoshi.myapp01.iso15693.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(1 + mData.length);
            buff.put(mSecurityStatus).put(mData);
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
            dest.writeByte(mSecurityStatus);
            dest.writeInt(mData.length);
            dest.writeByteArray(mData);
        }
        /**
         * ブロックがロックされているか否かを検査します
         * @return boolean ロックされている場合はtrueが戻ります
         */
        public boolean isLocked() {
            return ((mSecurityStatus & ERROR_DETECT_ERROR ) & 0xFF) == ERROR_DETECT_ERROR;
        }

        /**
         * securityStatusを取得します
         * @return byte securityStatusが戻ります
         */
        public byte getSecurityStatus() {
            return mSecurityStatus;
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
//            REVISED
// =======BEFORE=======
//            sb.append("BlockData [" + Util.getHexString(this.getBytes()) + "]\n");
//            sb.append("　ブロックセキュリティステータス:" + Util.getHexString(mSecurityStatus) + "\n");
//            sb.append("　データ: " + new String(mData, Charset.defaultCharset()) + "(" + Util.getHexString(mData) + ")"  + "\n");
// =======AFTER=======
            byte zeroByte = 0x00;
            int j=0;
            for (int i=0; i<mData.length; i++) {
                if (mData[i] != zeroByte) {
                    j++;
                } else {
                    break;
                }
            }
            byte finalText[] = new byte[j];
            for (int i=0; i<j; i++) {
                finalText[i] = mData[i];
            }
            sb.append(new String(finalText, Charset.defaultCharset()));
// ==============
            return sb.toString();
        }
    }

    final BlockData[] mBlockDatas;
    /**
     * コンストラクタ 
     * @param bytes バイト列をセット
     * @param blockSize 読み込むブロックサイズ(byte)をセット
     * @param numberOfBlocks 読み込むブロックの数をセット
     */
    public ReadMultipleBlocksResponse(byte[] bytes, int blockSize
            , int numberOfBlocks) {
        super(bytes);
        if ( hasError() ) {
            mBlockDatas = null;
        } else {
            mBlockDatas = new BlockData[numberOfBlocks];
            int start = 0;
            for ( int i = 0; i < numberOfBlocks; i++) {
                mBlockDatas[i] = new BlockData(
                        Arrays.copyOfRange(mData, start, start + blockSize+1));
                start = start + blockSize+1;
            }
        }
    }
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public ReadMultipleBlocksResponse(Parcel in) {
        super(in);
        if ( hasError() ) {
            mBlockDatas = null;
        } else {
            int numberOfBlocks = in.readInt();
            mBlockDatas = new BlockData[numberOfBlocks];
            for ( int i = 0; i < numberOfBlocks; i++) {
                mBlockDatas[i] = new BlockData(in);
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
            dest.writeInt(mBlockDatas.length);
            for (BlockData d : mBlockDatas) {
                d.writeToParcel(dest, flags);
            }
        }

    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        int length = 0;
        if (!hasError()) {
            for ( BlockData b : mBlockDatas ) {
                length = length + b.getBytes().length;
            }
            ByteBuffer buff = ByteBuffer.allocate( 1 + length );
            buff.put(mFlags);
            for ( BlockData b : mBlockDatas ) {
                buff.put(b.getBytes());
            }
            return buff.array();
        } else {
            length = 2;
            ByteBuffer buff = ByteBuffer.allocate(2);
            buff.put(mFlags).put(mErrorCode);
            return buff.array();
        }

    }

    /**
     * blockDatasを取得します
     * @return BlockData[] getBlockDatas
     */
    public BlockData[] getBlockDatas() {
        return mBlockDatas;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//          REVISED
// =======BEFORE=======
//        sb.append("ReadMultipleBlocksResponse" + "\n");
//        sb.append("　フラグ:" + Util.getHexString(mFlags) + "\n");
//        sb.append("　エラーコード:" +   Util.getHexString(mErrorCode) + "(" + ISO15693Lib.ErrorCode.errorMap.get(mErrorCode) + ")\n");
//        if (Integer.parseInt(Util.getHexString(mErrorCode)) == 0) {
//            int i = 0;
//            for (i = 0; i < mBlockDatas.length - 1; i++) {
//                sb.append("　Block " + i + "\n");
//                sb.append("  " + mBlockDatas[i].toString() + "\n");
//            }
//            For Logging
//            if (i >= 27) {
//                Log.d("BlockNum", "count : " + i);
//            }
//        }
// =======AFTER=======
        if (Integer.parseInt(Util.getHexString(mErrorCode)) == 0) {
            int i = 0;
            for (i = 0; i < mBlockDatas.length - 1; i++) {
                sb.append(mBlockDatas[i].toString());
            }
        }
// ==============
        return sb.toString();
    }
}
