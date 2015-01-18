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

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_READ_MULTIPLE_BLOCKS;

/**
 * ReadMultipleBlockコマンドを発行するリクエストフォーマットを提供します
 *
 * @author Kazzz
 * @date 2011/07/12
 * @since Android API Level 10
 *
 */

public class ReadMultipleBlocksRequest extends ReadSingleBlockRequest {
    /** Parcelable need CREATOR field **/
    public static final Parcelable.Creator<ReadMultipleBlocksRequest> CREATOR =
            new Parcelable.Creator<ReadMultipleBlocksRequest>() {
                public ReadMultipleBlocksRequest createFromParcel(Parcel in) {
                    return new ReadMultipleBlocksRequest(in);
                }

                public ReadMultipleBlocksRequest[] newArray(int size) {
                    return new ReadMultipleBlocksRequest[size];
                }
            };
    protected byte mNumberOfBlock;
    /**
     * コンストラクタ
     * @param bytes バイト列をセット
     */
    public ReadMultipleBlocksRequest(byte[] bytes) {
        super(bytes);
        mCommand = COMMAND_READ_MULTIPLE_BLOCKS;
        mNumberOfBlock = bytes[11];
    }
    /**
     * コンストラクタ
     * @param in Parcelオブジェクトをセット
     */
    public ReadMultipleBlocksRequest(Parcel in) {
        super(in);
        mCommand = COMMAND_READ_MULTIPLE_BLOCKS;
        mNumberOfBlock = in.readByte();
    }
    /**
     * コンストラクタ
     * @param flags オプションフラグをセット
     * @param uid UIDをセット
     * @param blockNumber 対象のブロック番号をセット
     * @param numberOfBlock 一度に読み込むのブロック数をセット
     */
    public ReadMultipleBlocksRequest(byte flags, UID uid
            , byte blockNumber, byte numberOfBlock) {
        super(flags, uid, blockNumber);
        mCommand = COMMAND_READ_MULTIPLE_BLOCKS;
        mNumberOfBlock = numberOfBlock;
    }

    /**
     * mnumberOfBlockを取得します
     * @return byte mnumberOfBlockが戻ります
     */
    public byte getMnumberOfBlock() {
        return mNumberOfBlock;
    }
    /**
     * mnumberOfBlockを設定します
     * @param mnumberOfBlock mnumberOfBlockをセットします
     */
    public void setMnumberOfBlock(byte mnumberOfBlock) {
        this.mNumberOfBlock = mnumberOfBlock;
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate(superData.length
                + 1 );
        buff.put(superData)
                .put(mNumberOfBlock);
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
            sb.append("　" + mUID.toString() + "\n");
        }
        sb.append(" ブロック番号:" + Util.getHexString(mBlockNumber) + "\n");
        sb.append(" 読込みブロック数:" + Util.getHexString(mNumberOfBlock) + "\n");
        return sb.toString();
    }


}
