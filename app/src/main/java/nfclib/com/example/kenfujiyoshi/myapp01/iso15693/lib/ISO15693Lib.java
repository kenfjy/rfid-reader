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
package nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib;

import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.NfcV;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.ISO15693ByteData;
import nfclib.com.example.kenfujiyoshi.myapp01.nfc.NfcException;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

/**
 * ISO15693 デバイスにアクセスするためのコマンドとデータ操作をライブラリィとして提供します
 * 
 * @author Kazzz
 * @date 2011/07/11
 * @since Android API Level 10
 *
 */

public class ISO15693Lib {
    //標準コマンド
    public static final byte COMMAND_INVENTORY = 0x01;
    public static final byte COMMAND_STAY_QUIET = 0x02;
    public static final byte COMMAND_READ_SINGLE_BLOCK = 0x20;
    public static final byte COMMAND_WRITE_SINGLE_BLOCK = 0x21;
    public static final byte COMMAND_LOCK_BLOCK = 0x22;
    public static final byte COMMAND_READ_MULTIPLE_BLOCKS = 0x23;
    public static final byte COMMAND_WRITE_MULTIPLE_BLOCKS = 0x24;
    public static final byte COMMAND_SELECT = 0x25;
    public static final byte COMMAND_RESET_TO_READY = 0x26;
    public static final byte COMMAND_WRITE_AFI = 0x27;
    public static final byte COMMAND_LOCK_AFI = 0x28;
    public static final byte COMMAND_WRITE_DSFID = 0x29;
    public static final byte COMMAND_LOCK_DSFID = 0x2A;
    public static final byte COMMAND_GET_SYSTEM_INFORMATION = 0x2B;
    public static final byte COMMAND_GET_MULTIPLE_BLOCKS_SECURITY_STATUS = 0x2C;
    
    public static final Map<Byte, String> commandMap = new HashMap<Byte, String>();
    
    //command code and name dictionary
    static {
        commandMap.put(COMMAND_INVENTORY, "Inventory");
        commandMap.put(COMMAND_STAY_QUIET, "Stay Quiet");
        commandMap.put(COMMAND_READ_SINGLE_BLOCK, "Read Single Block");
        commandMap.put(COMMAND_WRITE_SINGLE_BLOCK, "Write Single Block");
        commandMap.put(COMMAND_LOCK_BLOCK, "Lock Block");
        commandMap.put(COMMAND_READ_MULTIPLE_BLOCKS, "Read Multiple Blocks");
        commandMap.put(COMMAND_WRITE_MULTIPLE_BLOCKS, "Write Multiple Blocks");
        commandMap.put(COMMAND_SELECT, "Select");
        commandMap.put(COMMAND_RESET_TO_READY, "Reset to Ready");
        commandMap.put(COMMAND_WRITE_AFI, "Write AFI");
        commandMap.put(COMMAND_LOCK_AFI, "Lock AFI");
        commandMap.put(COMMAND_WRITE_DSFID, "Write DSFID");
        commandMap.put(COMMAND_LOCK_DSFID, "Lock DSFID");
        commandMap.put(COMMAND_GET_SYSTEM_INFORMATION, "Get System Information");
        commandMap.put(COMMAND_GET_MULTIPLE_BLOCKS_SECURITY_STATUS, "Get Mutiple Blocks Security Status");
    }

    /**
     * 
     * ISO15693 UIDを抽象化したクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class UID implements Parcelable, ISO15693ByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<UID> CREATOR = 
            new Parcelable.Creator<UID>() {
                public UID createFromParcel(Parcel in) {
                    return new UID(in);
                }
                
                public UID[] newArray(int size) {
                    return new UID[size];
                }
            };
        final byte[] mICMManufacturerSerialNumber; //6byte
        final byte[] mMfgCode;                     //1byte 
        final byte[] mEOF;                         //1byte
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public UID(Parcel in) {
            mICMManufacturerSerialNumber = new byte[in.readInt()];
            in.readByteArray(mICMManufacturerSerialNumber);
            mMfgCode = new byte[in.readInt()];
            in.readByteArray(mMfgCode);
            mEOF = new byte[2];
            in.readByteArray(mEOF); 
        }
        /**
         * コンストラクタ 
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public UID(byte[] bytes) {
            mICMManufacturerSerialNumber =  Arrays.copyOfRange(bytes, 0, 6);
            mMfgCode = Arrays.copyOfRange(bytes, 6, 7);
            mEOF = Arrays.copyOfRange(bytes, 7, 8);
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
            //配列長を先に書きだしておく
            dest.writeInt(mICMManufacturerSerialNumber.length);
            dest.writeByteArray(mICMManufacturerSerialNumber);
            
            //配列長を先に書きだしておく
            dest.writeInt(mMfgCode.length);
            dest.writeByteArray(mMfgCode);
            
            dest.writeByteArray(mEOF);
        }
        /* (non-Javadoc)
         * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(
                    mICMManufacturerSerialNumber.length + mMfgCode.length + mEOF.length);
            buff.put(mICMManufacturerSerialNumber).put(mMfgCode).put(mEOF);
            return buff.array();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UID (8byte):　[" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append("　製造者シリアル番号:　" + Util.getHexString(mICMManufacturerSerialNumber) + "\n");
            sb.append("　IC製造者コード:　" + Util.getHexString(mMfgCode) + "\n");
            return sb.toString();
        }

        public String toId() {
            StringBuilder sb = new StringBuilder();
            sb.append(Util.getHexString(this.getBytes()));
            return sb.toString();
        }

    }    
    /**
     * 
     * ISO15693 UIDを抽象化したクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class MemorySizeInfo implements Parcelable, ISO15693ByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<MemorySizeInfo> CREATOR = 
            new Parcelable.Creator<MemorySizeInfo>() {
                public MemorySizeInfo createFromParcel(Parcel in) {
                    return new MemorySizeInfo(in);
                }
                
                public MemorySizeInfo[] newArray(int size) {
                    return new MemorySizeInfo[size];
                }
            };
        final byte mNumberOfBlocks; //8bit
        final byte mBlockSize;         //5bit
        /**
         * コンストラクタ
         * (各値は0オリジンで設定されているため、必ず1加算する)
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public MemorySizeInfo(Parcel in) {
            mNumberOfBlocks = (byte)(in.readByte() + 1);
            mBlockSize = (byte)(((in.readByte() & 0x1F) & 0xff) + 1);
        }
        /**
         * コンストラクタ 
         * (各値は0オリジンで設定されているため、必ず1加算する)
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public MemorySizeInfo(byte[] bytes) {
            mNumberOfBlocks = (byte)(bytes[0] + 1);
            mBlockSize = (byte)(((bytes[1] & 0x1F) & 0xff) + 1);
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
            //配列長を先に書きだしておく
            dest.writeByte(mNumberOfBlocks);
            dest.writeByte(mBlockSize);
        }
        /* (non-Javadoc)
         * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(2);
            buff.put(mNumberOfBlocks).put(mBlockSize);
            return buff.array();
        }
        
        /**
         * numberOfBlocksを取得します
         * @return byte numberOfBlockSizeが戻ります
         */
        public byte getNumberOfBlocks() {
            return mNumberOfBlocks;
        }
        /**
         * blockSizeを取得します
         * @return byte blockSizeが戻ります
         */
        public byte getBlockSize() {
            return mBlockSize;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MemorySizeInfo (2byte):　[" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append("　ブロック数:　" + Util.getHexString(mNumberOfBlocks) + "\n");
            sb.append("　ブロックサイズ:　" + Util.getHexString(mBlockSize) + "\n");
            return sb.toString();
        }    
    }
   /**
     * 
     * ISO15693 Option Flagsを抽象化したクラスを提供します
     * 
     * @author Kazzz
     * @date 2011/01/20
     * @since Android API Level 9
     */
    public static class Flags  {
      //オプションフラグ (bit1～bit4)
        //public static final byte SUBCARRIER_SINGLE      = 0x00;//VICC シングルサブキャリア
        public static final byte SUBCARRIER_DOUBLE      = 0x01;//VICC ダブルサブキャリア
        //public static final byte DATA_RATE_LOW          = 0x02;//データレート低
        public static final byte DATA_RATE_HIGH         = 0x02;//データレート高
        //public static final byte INVENTORY_FLAG_OFF     = 0x04;//インベントリフラグOff
        public static final byte INVENTORY_FLAG_ON      = 0x04;//インベントリフラグOn
        //public static final byte PROTOCOL_EXTENSION_OFF = 0x06;//プロトコルエクステンション off
        public static final byte PROTOCOL_EXTENSION_ON  = 0x08;//プロトコルエクステンション on
        
      //インベントリフラグ(bit3)=0の場合 (bit5～bit8)
        //public static final byte SELECT_ANY_ADDRESS  = 0x08;//全てのアドレスを選択
        public static final byte SELECT_ONLY_ADDRESS = 0x10;//指定アドレスのみ選択
        //public static final byte NON_ADDRESS_MODE    = 0x0a;//アドレス指定無しモード
        public static final byte ADDRESSED_MODE      = 0x20;//アドレス指定モード
        //public static final byte OPTION_COMMAND_OFF  = 0x0c;//コマンドにより操作される
        public static final byte OPTION_COMMAND_ON   = 0x40;//コマンドにより操作される
       

      //インベントリフラグ(bit3)=1の場合 (bit5～bit8)
        //public static final byte AFI_NOT_PRESENT     = 0x08;//AFI指定無し
        public static final byte AFI_PRESENT         = 0x10;  //AFI指定あり
        //public static final byte NB_SLOT_16          = 0x0a;//16スロットモード
        public static final byte NB_SLOT_1           = 0x20;   //1スロットモード
        //bit7は同上
       
      //ブロックセキュリティステータス
        public static final byte STATUS_NOT_LOCKED   = 0x00;//ロック無し
        public static final byte STATUS_LOCKED       = 0x01;//ロック無し
        
      //レスポンスで戻るエラーフラグ (bit1)
        public static final byte ERROR_NOERROR      = 0x00;//エラー無し
        public static final byte ERROR_DETECT_ERROR = 0x01;//エラー検出
        
      //Get System Informationで戻るインフォメーションフラグ
        //public static final byte DSFID_NOT_SUPPORTED  = 0x00;//DSFIDサポート無し
        public static final byte DSFID_SUPPORTED      = 0x01;//DSFIDサポート有り
        //public static final byte AFI_NOT_SUPPORTED  = 0x00;//AFIサポート無し
        public static final byte AFI_SUPPORTED      = 0x02;//AFIサポート有り
        //public static final byte VICC_MEMORYSIZE_NOT_SUPPORTED  = 0x00;//VICCメモリサイズサポート無し
        public static final byte VICC_MEMORYSIZE_SUPPORTED  = 0x04;//VICCメモリサイズサポート有り
        //public static final byte IC_REFERENCE_NOT_SUPPORTED = 0x00;//ICリファレンスサポート無し
        public static final byte IC_REFERENCE_SUPPORTED     = 0x08;//ICリファレンスサポート有り
    }        
    
    public static class ErrorCode {
        public static final byte NOERROR = 0x00;
        public static final byte COMMAND_NOT_SUPPORTED = 0x01;
        public static final byte COMMAND_NOT_RECOGNISED = 0x02;
        public static final byte OPTION_NOT_SUPPORTED = 0x03;
        public static final byte UNKNOWN_ERROR = 0x0F;
        public static final byte BLOCK_NOT_AVAILABLE = 0x10;
        public static final byte BLOCK_ALREADY_LOCKED = 0x11;
        public static final byte BLOCK_CONTENT_LOCKED = 0x12;
        public static final byte BLOCK_UNSUCCESSFULLY_PRAGRAMMED = 0x13;
        public static final byte BLOCK_UNSUCCESSFULLY_LOCKED = 0x14;
        
        public static final Map<Byte, String> errorMap = new HashMap<Byte, String>();
        
        //command code and name dictionary
        static {
            errorMap.put(NOERROR, "OK! NOERROR");
            errorMap.put(COMMAND_NOT_SUPPORTED, "The command is not supported.");
            errorMap.put(COMMAND_NOT_RECOGNISED, "The command is not recognised.");
            errorMap.put(OPTION_NOT_SUPPORTED, "The Option is not supported");
            errorMap.put(UNKNOWN_ERROR, "Unknown error.");
            errorMap.put(BLOCK_NOT_AVAILABLE, "The specified block is not available.");
            errorMap.put(BLOCK_ALREADY_LOCKED, "The specified block is already -locked and thus cannot be locked again");
            errorMap.put(BLOCK_CONTENT_LOCKED, "The specified block is locked and its content cannot be changed.");
            errorMap.put(BLOCK_UNSUCCESSFULLY_PRAGRAMMED, "The specified block was not successfully programmed.");
            errorMap.put(BLOCK_UNSUCCESSFULLY_LOCKED, "The specified block was not successfully locked.");
        }
        
        public static String getErrorMessage(byte errorCode) {
            return ErrorCode.errorMap.get(errorCode);
        }
    }
    
    /**
     * ISO15693 Request Formatを抽象化したクラスを提供します
     * 
     * @author Copyright c 2011-2012 All Rights Reserved.
     * @date 2011/07/11
     * @since Android API Level 10
     *
     */
    public static class RequestFormat implements Parcelable, ISO15693ByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<RequestFormat> CREATOR = 
            new Parcelable.Creator<RequestFormat>() {
                public RequestFormat createFromParcel(Parcel in) {
                    return new RequestFormat(in);
                }
                
                public RequestFormat[] newArray(int size) {
                    return new RequestFormat[size];
                }
            };
        //byte SOF;   //ignore for Android NFC 
        protected byte mFlags;
        protected byte mCommand;
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public RequestFormat(Parcel in) {
            mFlags = in.readByte();
            mCommand = in.readByte();
        }
        /**
         * コンストラクタ 
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public RequestFormat(byte[] bytes) {
            mFlags =  bytes[0];
            mCommand = bytes[1];
        }
        /**
         * コンストラクタ
         * @param flags オプションフラグをセット
         * @param command コマンドをセット
         */
        public RequestFormat(byte flags, byte command) {
            mFlags =  flags;
            mCommand = command;
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
            dest.writeByte(mCommand);
        }
        /* (non-Javadoc)
         * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(2);
            buff.put(mFlags).put(mCommand);
            return buff.array();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("RequestFormat [" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append(" オプションフラグ:" + Util.getHexString(mFlags) + "\n");
            sb.append(" コマンド:" + commandMap.get(mCommand) + "(" + Util.getHexString(mCommand) + ")\n");
            return sb.toString();
        }
    }
    
    
    
    /**
     * ISO15693 Response Formatを抽象化したクラスを提供します
     * 
     * @author Copyright c 2011-2012 All Rights Reserved.
     * @date 2011/07/11
     * @since Android API Level 10
     *
     */
    public static class ResponseFormat implements Parcelable, ISO15693ByteData {
        /** Parcelable need CREATOR field **/ 
        public static final Parcelable.Creator<ResponseFormat> CREATOR = 
            new Parcelable.Creator<ResponseFormat>() {
                public ResponseFormat createFromParcel(Parcel in) {
                    return new ResponseFormat(in);
                }
                
                public ResponseFormat[] newArray(int size) {
                    return new ResponseFormat[size];
                }
            };
        //byte SOF;   //ignore for Android NFC 
        protected final byte mFlags;
        protected final byte mErrorCode;
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public ResponseFormat(Parcel in) {
            mFlags = in.readByte();
            
            if ( hasError() ) { 
                mErrorCode = in.readByte();
            } else {
                mErrorCode = 0;
            }
        }
        /**
         * コンストラクタ 
         * @param bytes IDmの格納されているバイト列をセットします
         */
        public ResponseFormat(byte[] bytes) {
            mFlags =  bytes[0];
            if ( hasError() ) { 
                mErrorCode = bytes[1];
            } else {
                mErrorCode = 0;
            }
        }
        /**
         * エラーが検出されているか否かを取得します
         * @return boolean エラーが検出されている場合trueが戻ります
         */
        public boolean hasError() {
            return (mFlags & 0x01) != 0;
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
            if ( hasError() ) {
                dest.writeByte(mErrorCode);
            }
        }
        /* (non-Javadoc)
         * @see net.com.example.kenfujiyoshi.myapp01.felica.ISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(2);
            buff.put(mFlags).put(hasError() 
                    ? mErrorCode
                    : 0 );
            return buff.array();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ResponseFormat [" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append("　オプションフラグ:" + Util.getHexString(mFlags) + "\n");
            sb.append("　エラーコード:" +   Util.getHexString(mErrorCode) + "(" + ErrorCode.errorMap.get(mErrorCode) + ")\n");
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
         * errorCodeを取得します
         * @return byte errorCodeが戻ります
         */
        public byte getErrorCode() {
            return mErrorCode;
        }
   }
    /**
     * INfcTag#transceiveを実行します
     * 
     * @param tag Tagクラスの参照をセットします
     * @param data 実行するコマンドパケットをセットします
     * @return byte[] コマンドの実行結果バイト列で戻ります 
     * @throws NfcException コマンドの発行に失敗した場合にスローされます
     */
    public static final byte[] transceive(Tag tag, byte[] data) throws NfcException {
        //NfcVはISO15693
        NfcV nfcV = NfcV.get(tag);
        if ( nfcV == null ) throw new NfcException("tag is not ISO15693(NFC-V) ");
        try {
            nfcV.connect();
            try {
                return nfcV.transceive(data);
            } finally {
                nfcV.close();
            }
        } catch (TagLostException e) {
            return null; //Tag Lost
        } catch (IOException e) {
            throw new NfcException(e);
        }
    }
    
}
