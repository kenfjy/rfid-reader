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

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_WRITE_MULTIPLE_BLOCKS;

import java.nio.ByteBuffer;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.RequestFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * WriteMultipleBlocksコマンドを発行するリクエストフォーマットを提供します
 * 
 * @author Kazzz
 * @date 2011/07/12
 * @since Android API Level 10
 *
 */

public class WriteMultipleBlocksRequest extends RequestFormat {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<WriteMultipleBlocksRequest> CREATOR = 
        new Parcelable.Creator<WriteMultipleBlocksRequest>() {
            public WriteMultipleBlocksRequest createFromParcel(Parcel in) {
                return new WriteMultipleBlocksRequest(in);
            }
            
            public WriteMultipleBlocksRequest[] newArray(int size) {
                return new WriteMultipleBlocksRequest[size];
            }
        };
    final UID mUID;
    byte mFirstBlockNumber;
    byte mNumberOfBlock;
    byte[] mData;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public WriteMultipleBlocksRequest(Parcel in) {
        super(in);
        mCommand = in.readByte();
        mUID = new UID(in);
        mFirstBlockNumber = in.readByte();
        mNumberOfBlock = in.readByte();
        in.readByteArray(mData);
    }
    /**
     * コンストラクタ
     * @param bytes バイト列をセット
     */
    public WriteMultipleBlocksRequest(byte[] bytes) {
        super(bytes);
        mCommand = COMMAND_WRITE_MULTIPLE_BLOCKS;
        mUID = new UID(Arrays.copyOfRange(bytes, 2, 10));
        mFirstBlockNumber = bytes[10];
        mNumberOfBlock = bytes[11];
        mData = Arrays.copyOfRange(bytes, 12, bytes.length);
    }
    /**
     * コンストラクタ
     * @param flags バイト列をセット
     * @param uid バイト列をセット
     * @param firstBlockNumber バイト列をセット
     * @param numberOfBlock バイト列をセット
     * @param bytes バイト列をセット
     */
    public WriteMultipleBlocksRequest(byte flags, UID uid, byte firstBlockNumber
            , byte numberOfBlock, byte[] data) {
        super(flags, COMMAND_WRITE_MULTIPLE_BLOCKS); 
        mUID = uid;
        mFirstBlockNumber = firstBlockNumber;
        mNumberOfBlock = numberOfBlock;
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
     * firstBlockNumberを取得します
     * @return byte firstBlockNumberが戻ります
     */
    public byte getFirstBlockNumber() {
        return mFirstBlockNumber;
    }
    /**
     * numberOfBlockを取得します
     * @return byte numberOfBlockが戻ります
     */
    public byte getNumberOfBlock() {
        return mNumberOfBlock;
    }
    /**
     * firstBlockNumberを設定します
     * @param firstBlockNumber firstBlockNumberをセットします
     */
    public void setFirstBlockNumber(byte firstBlockNumber) {
        mFirstBlockNumber = firstBlockNumber;
    }
    /**
     * numberOfBlockを設定します
     * @param numberOfBlock numberOfBlockをセットします
     */
    public void setNumberOfBlock(byte numberOfBlock) {
        mNumberOfBlock = numberOfBlock;
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
                superData.length + mUID.getBytes().length + 2 + mData.length);
        
        buff.put(superData)
            .put(mUID.getBytes())
            .put(mFirstBlockNumber)
            .put(mNumberOfBlock)
            .put(mData);
        return buff.array();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WriteMultipleBlocksRequest [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append(" オプションフラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append(" コマンド:" + ISO15693Lib.commandMap.get(mCommand) + 
                "(" + Util.getHexString(mCommand) + ")\n");
        sb.append(" "+ mUID.toString());
        sb.append(" 開始ブロック番号:" + Util.getHexString(mFirstBlockNumber) + "\n");
        sb.append(" 書込みブロック数:" + Util.getHexString(mFirstBlockNumber) + "\n");
        sb.append(" データ:" + Util.getHexString(mData) + "\n");
        return sb.toString();
    }
    
    
}
