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

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_WRITE_SINGLE_BLOCK;

import java.nio.ByteBuffer;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.RequestFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * WriteSingleBlockコマンドを発行するリクエストフォーマットを提供します
 * 
 * @author Kazzz
 * @date 2011/07/12
 * @since Android API Level 10
 *
 */

public class WriteSingleBlockRequest extends RequestFormat {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<WriteSingleBlockRequest> CREATOR = 
        new Parcelable.Creator<WriteSingleBlockRequest>() {
            public WriteSingleBlockRequest createFromParcel(Parcel in) {
                return new WriteSingleBlockRequest(in);
            }
            
            public WriteSingleBlockRequest[] newArray(int size) {
                return new WriteSingleBlockRequest[size];
            }
        };
    final UID mUID;
    byte mBlockNumber;
    byte[] mData;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public WriteSingleBlockRequest(Parcel in) {
        super(in);
        mCommand = in.readByte();
        mUID = new UID(in);
        mBlockNumber = in.readByte();
        in.readByteArray(mData);
    }
    /**
     * コンストラクタ
     * @param bytes バイト列をセット
     * @param bytes バイト列をセット
     */
    public WriteSingleBlockRequest(byte[] bytes) {
        super(bytes);
        mCommand = COMMAND_WRITE_SINGLE_BLOCK;
        mUID = new UID(Arrays.copyOfRange(bytes, 2, 10));
        mBlockNumber = bytes[10];
        mData = Arrays.copyOfRange(bytes, 11, bytes.length);
    }
    /**
     * コンストラクタ
     * @param bytes バイト列をセット
     */
    public WriteSingleBlockRequest(byte flags, UID uid, byte blockNumber, byte[] data) {
        super(flags, COMMAND_WRITE_SINGLE_BLOCK); 
        mCommand = COMMAND_WRITE_SINGLE_BLOCK;
        mUID = uid;
        mBlockNumber = blockNumber;
        mData = data;
    }
    /**
     * uIDを取得します
     * @return UID uIDが戻ります
     */
    public UID getUID() {
        return mUID;
    }
    /**
     * blockNumberを取得します
     * @return byte blockNumberが戻ります
     */
    public byte getBlockNumber() {
        return mBlockNumber;
    }
    /**
     * blockNumberを設定します
     * @param blockNumber blockNumberをセットします
     */
    public void setBlockNumber(byte blockNumber) {
        mBlockNumber = blockNumber;
    }
    /**
     * dataを取得します
     * @return byte[] dataが戻ります
     */
    public byte[] getData() {
        return mData;
    }
    /**
     * dataを設定します
     * @param data dataをセットします
     */
    public void setData(byte[] data) {
        mData = data;
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate(
                superData.length + mUID.getBytes().length + 1 + mData.length);
        buff.put(superData)
            .put(mUID.getBytes())
            .put(mBlockNumber)
            .put(mData);
        return buff.array();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WriteSingleBlockRequest [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append(" オプションフラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append(" コマンド:" + ISO15693Lib.commandMap.get(mCommand) + 
                "(" + Util.getHexString(mCommand) + ")\n");
        sb.append(" "+ mUID.toString());
        sb.append(" ブロック番号:" + Util.getHexString(mBlockNumber) + "\n");
        sb.append(" データ:" + Util.getHexString(mData) + "\n");
        return sb.toString();
    }
    
    
}
