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

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_READ_SINGLE_BLOCK;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.RequestFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

/**
 * ReadSingleBlockコマンドを発行するリクエストフォーマットを提供します
 * 
 * @author Kazzz
 * @date 2011/07/12
 * @since Android API Level 10
 *
 */

public class ReadSingleBlockRequest extends RequestFormat {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<ReadSingleBlockRequest> CREATOR = 
        new Parcelable.Creator<ReadSingleBlockRequest>() {
            public ReadSingleBlockRequest createFromParcel(Parcel in) {
                return new ReadSingleBlockRequest(in);
            }
            
            public ReadSingleBlockRequest[] newArray(int size) {
                return new ReadSingleBlockRequest[size];
            }
        };
    final UID mUID;
    byte mBlockNumber;
    /**
     * コンストラクタ
     * @param bytes バイト列をセット
     */
    public ReadSingleBlockRequest(byte[] bytes) {
        super(bytes);
        mCommand = COMMAND_READ_SINGLE_BLOCK;
        mUID = new UID(Arrays.copyOfRange(bytes, 2, 10));
        mBlockNumber = bytes[10];
    }
    /**
     * コンストラクタ
     * @param in Parcelオブジェクトをセット
     */
    public ReadSingleBlockRequest(Parcel in) {
        super(in);
        mCommand = in.readByte();
        mUID = new UID(in);
        mBlockNumber = in.readByte();
    }
    /**
     * コンストラクタ
     * @param flags オプションフラグをセット
     * @param uid UIDをセット
     * @param blockNumber 対象のブロック番号をセット
     */
    public ReadSingleBlockRequest(byte flags, UID uid, byte blockNumber) {
        super(flags, COMMAND_READ_SINGLE_BLOCK); 
        mUID = uid;
        mBlockNumber = blockNumber;
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


    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate(superData.length 
                + mUID.getBytes().length + 1 );
        buff.put(superData)
            .put(mUID.getBytes())
            .put(mBlockNumber);
        return buff.array();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReadSingleBlockRequest [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append(" オプションフラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append(" コマンド:" + ISO15693Lib.commandMap.get(mCommand) + 
                "(" + Util.getHexString(mCommand) + ")\n");
        if ( mUID != null ) {
            sb.append( mUID.toString() + "\n");
        }
        sb.append(" ブロック番号:" + Util.getHexString(mBlockNumber) + "\n");
        return sb.toString();
    }
    
    
}
