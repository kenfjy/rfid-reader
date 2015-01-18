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
package nfclib.com.example.kenfujiyoshi.myapp01.iso15693;

import android.content.res.Resources;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.kenfujiyoshi.myapp01.R;

import java.nio.ByteBuffer;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.InventoryRequest;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.InventoryResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadMultipleBlocksRequest;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadMultipleBlocksResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadSingleBlockRequest;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.ReadSingleBlockResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.SystemInformationRequest;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.SystemInformationResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.WriteResponse;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.command.WriteSingleBlockRequest;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ErrorCode;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.MemorySizeInfo;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.nfc.NfcException;
import nfclib.com.example.kenfujiyoshi.myapp01.nfc.NfcTag;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.ADDRESSED_MODE;
import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.DATA_RATE_HIGH;
import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.INVENTORY_FLAG_ON;
import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.NB_SLOT_1;
import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.Flags.OPTION_COMMAND_ON;

/**
 * ISO15693コマンドに準拠したタグのクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/08
 * @since Android API Level 9
 *
 */

public class ISO15693Tag extends NfcTag {
    /** Parcelable need CREATOR field **/ 
    public static final Parcelable.Creator<ISO15693Tag> CREATOR = 
        new Parcelable.Creator<ISO15693Tag>() {
            public ISO15693Tag createFromParcel(Parcel in) {
                return new ISO15693Tag(in);
            }
            
            public ISO15693Tag[] newArray(int size) {
                return new ISO15693Tag[size];
            }
        };

    protected Tag mNfcTag;
    protected UID mUID;
    protected byte mDsfId;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public ISO15693Tag(Parcel in) {
        this.readFromParcel(in);
    }
    /**
     * コンストラクタ
     * 
     * @param nfcTag NFCTagへの参照をセット
     */
    public ISO15693Tag(Tag nfcTag) {
        mNfcTag =  nfcTag;
        NfcV nfcV = NfcV.get(mNfcTag);
        mDsfId = nfcV.getDsfId();
        mUID = new UID(mNfcTag.getId());
    }
    /*
     * @see com.example.kenfujiyoshi.myapp01.nfc.NfcTag#writeToParcel(android.os.Parcel, int)
     * @param dest Parcel object
     * @param flags int
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mNfcTag, 0);
        dest.writeParcelable(mUID, 0);
        dest.writeByte(mDsfId);
    }
    /*
     * @see com.example.kenfujiyoshi.myapp01.nfc.NfcTag#readFromParcel(android.os.Parcel)
     * @param source Parcel object
     */
    @Override
    public void readFromParcel(Parcel source) {
        ClassLoader cl = this.getClass().getClassLoader();
        mNfcTag = source.readParcelable(cl);
        mUID = source.readParcelable(cl);
        mDsfId = source.readByte();
    }    
    /**
     * 通信領域にあるタグのUIDを棚卸しします
     * 
     * @return InventoryResponse デバイスに送信したコマンドのレスポンスが戻ります
     * @throws ISO15693Exception
     */
    public InventoryResponse inventory() throws ISO15693Exception {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        // read single block
        InventoryRequest req = 
            new InventoryRequest((byte) 
                     (( DATA_RATE_HIGH | INVENTORY_FLAG_ON | NB_SLOT_1 ) & 0xff) 
                    , (byte)0x00
                    , (byte)0x00
                    , new byte[]{});
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            if ( result == null ) {
                throw new ISO15693Exception(R.string.trancieve_failure + " : request = " + req.toString());
            }
            return new InventoryResponse(result);
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
    }
    /**
     * シングルブロックモードでデータを読み込みます
     * 
     * @param blockNumber 読み込むブロックの番号 (0オリジン)をセット
     * @return ReadResponse デバイスに送信したコマンドのレスポンスが戻ります
     * @throws ISO15693Exception
     */
    public ReadSingleBlockResponse readSingleBlock(byte blockNumber) throws ISO15693Exception {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        // read single block
        ReadSingleBlockRequest req = 
            new ReadSingleBlockRequest((byte) 
                     (( DATA_RATE_HIGH | ADDRESSED_MODE ) & 0xff) 
                    , mUID
                    , blockNumber);
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            if ( result == null ) {
                throw new ISO15693Exception(R.string.trancieve_failure + " : request = " + req.toString());
            }
            return new ReadSingleBlockResponse(result); 
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
    }
    /**
     * マルチブロックモードでデータを読み込みます
     * 
     * @param blockNumber 読み込む開始ブロック番号 (0オリジン)をセット
     * @param blockSize 読み込むブロックサイズ(byte) をセット
     * @param numberOfBlocks まとめて読み込むブロック数 をセット
     * @return ReadMultiBlockResponse デバイスに送信したコマンドのレスポンスが戻ります
     * @throws ISO15693Exception
     */
    public ReadMultipleBlocksResponse readMultipleBlocks(byte blockNumber
            , byte blockSize, byte numberOfBlocks) throws ISO15693Exception {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        // read multiple block
        ReadMultipleBlocksRequest req = 
            new ReadMultipleBlocksRequest((byte) 
                     (( DATA_RATE_HIGH | ADDRESSED_MODE | OPTION_COMMAND_ON) & 0xff) 
                    , mUID
                    , blockNumber
                    , numberOfBlocks);
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            if ( result == null ) {
                throw new ISO15693Exception(R.string.trancieve_failure + " : request = " + req.toString());
            }
            return new ReadMultipleBlocksResponse(result, blockSize, numberOfBlocks); 
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
    }
    /**
     * シングルブロックモードでデータを書き込みます
     * <pre>
     *  データはブロックサイズ(4byte)で書き出されます。はみ出したデータは無視されます
     * </pre>
     * @param blockNumber 書きこむブロックの番号をセット
     * @param data 書きこむデータをセット
     * @return WriteResponse デバイスに送信したコマンドのレスポンスが戻ります
     * @throws ISO15693Exception
     */
    public WriteResponse writeSingleBlock(byte blockNumber, byte[] data) throws ISO15693Exception {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        // read single block
        WriteSingleBlockRequest req = 
            new WriteSingleBlockRequest((byte) 
                     (( DATA_RATE_HIGH | ADDRESSED_MODE ) & 0xff) 
                    , mUID
                    , blockNumber
                    , data);
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            if ( result == null ) {
                throw new ISO15693Exception(R.string.trancieve_failure + " : request = " + req.toString());
            }
           return new WriteResponse(result); 
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
    }
    /**
     * マルチブロックモードでデータを書き込みます
     * <pre>
     *  データはブロックサイズ(4byte)で均等に分割されて、ブロックサイズ×書きこむブロック数の
     *  バイトで書き出されます。はみ出したデータは無視されます
     *  
     *  ICODE SLIは WriteMultipleBlocksをサポートしていません。
     *  なので、writeSingleBlockコマンドを複数回実行することで代替えしています
     * </pre>
     * @param firstBlockNumber 書きこむブロックの開始番号をセット
     * @param numberOfBlocks 一度に書きこむブロックの数をセット
     * @param data 書きこむデータをセット
     * @return WriteResponse デバイスに送信したコマンドのレスポンスが戻ります
     * @throws ISO15693Exception
     */
    public WriteResponse writeMultipleBlocks(byte firstBlockNumber
            , byte numberOfBlocks, byte[] data) throws ISO15693Exception {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        SystemInformationResponse sysInfo = this.getSystemInformation();
        if ( sysInfo == null || sysInfo.hasError()) {
            throw new ISO15693Exception(
                    Resources.getSystem().getString(R.string.get_system_info_failed, "ISO15693")
                            + sysInfo.getErrorCode());
        }
        
        final MemorySizeInfo memInfo = sysInfo.getMemoryInfo();
        if ( memInfo == null || memInfo.getNumberOfBlocks() == 0) {
            throw new ISO15693Exception(
                    Resources.getSystem().getString(R.string.get_memory_size_failed, "ISO15693")
            );
        }        
        
        //空のバッファを用意
        int blockSize = memInfo.getBlockSize();
        int length = numberOfBlocks * blockSize;
        ByteBuffer buff = ByteBuffer.allocate(length);
        if ( data.length < length ) {
            buff.put(data, 0, data.length);
        } else {
            buff.put(data, 0, length);
        }
        buff.position(0);
        
        byte[] b = new byte[blockSize];
        WriteResponse resp = null;
        for ( int i = firstBlockNumber; i < firstBlockNumber + numberOfBlocks; i++) {
            buff.get(b, 0, blockSize);
            resp = this.writeSingleBlock((byte)i, b);
            if ( resp.hasError() ) {
                throw new ISO15693Exception( resp.getErrorCode() + ":" 
                        + ErrorCode.getErrorMessage(resp.getErrorCode()));
            }
        }
        return resp;
        /*
        // write multiple blocks
        WriteMultipleBlocksRequest req = 
            new WriteMultipleBlocksRequest((byte) 
                     (( DATA_RATE_HIGH | ADDRESSED_MODE | OPTION_COMMAND_ON) & 0xff) 
                    , mUID
                    , firstBlockNumber
                    , (byte)((numberOfBlocks - 1) & 0xff) // 0オリジン
                    , data);
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            return new WriteResponse(result); 
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
        */
    }
    /**
     * システム情報を取得します
     *  
     * @return GetSystemInformationResponse コマンド実行結果の応答を返します
     * @throws ISO15693Exception
     */
    public SystemInformationResponse getSystemInformation() throws ISO15693Exception  {
        if ( mNfcTag == null ) {
            throw new ISO15693Exception(Resources.getSystem().getString(R.string.null_service));
        }
        // get System Information
        SystemInformationRequest req = 
            new SystemInformationRequest((byte) 
                     (( DATA_RATE_HIGH | ADDRESSED_MODE ) & 0xff) 
                    , mUID );
        
        try {
            byte[] result = ISO15693Lib.transceive(mNfcTag, req.getBytes());
            if ( result == null ) {
                throw new ISO15693Exception(R.string.trancieve_failure + " : request = " + req.toString());
            }
           return new SystemInformationResponse(result); 
        } catch (NfcException e) {
            throw new ISO15693Exception(e);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("ISO15693Tag \n");
       //if ( mNfcTag != null )
       //    sb.append(mNfcTag.toString()).append("\n");
       if ( mUID != null ) sb.append(mUID.toString());
       sb.append("　DsfId:　").append(Util.getHexString(mDsfId)).append("\n\n");
       return sb.toString();
    }
}
