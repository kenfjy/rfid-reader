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

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_INVENTORY;

import java.nio.ByteBuffer;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.RequestFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Inventoryコマンドを呼び出すためのリクエストクラスを提供します
 * 
 * @author Kazzz.
 * @date 2011/07/22
 * @since Android API Level 10
 *
 */

public class InventoryRequest extends RequestFormat {
    public static final Parcelable.Creator<InventoryRequest> CREATOR = 
            new Parcelable.Creator<InventoryRequest>() {
                public InventoryRequest createFromParcel(Parcel in) {
                    return new InventoryRequest(in);
                }
                
                public InventoryRequest[] newArray(int size) {
                    return new InventoryRequest[size];
                }
            };

        final byte mAFI;
        final byte mMaskLength;
        final byte[] mMaskValue;
        /**
         * コンストラクタ
         * @param in 入力するパーセル化オブジェクトをセット
         */
        public InventoryRequest(Parcel in) {
            super(in);
            mCommand = COMMAND_INVENTORY;
            mAFI = in.readByte();
            mMaskLength = in.readByte();
            mMaskValue = new byte[8];
            in.readByteArray(mMaskValue);
        }
        /**
         * コンストラクタ 
         * @param bytes 入力するバイト列をセット
         */
        public InventoryRequest(byte[] bytes) {
            super(bytes);
            mCommand = COMMAND_INVENTORY;
            mAFI = bytes[2];
            mMaskLength = bytes[3];
            mMaskValue = Arrays.copyOfRange(bytes, 4, 12);
        }
        /**
         * コンストラクタ
         * @param flags フラグ
         * @param afi AFIコードをセット
         * @param maskLength マスク長をセット(オプション)
         * @param maskVaule マスクの値をセット(オプション)
         */
        public InventoryRequest(byte flags, byte afi, byte maskLength, byte[] maskVaule) {
            super(flags, COMMAND_INVENTORY);
            mAFI = afi;
            mMaskLength = maskLength;
            mMaskValue = maskVaule;
        }
        /* (non-Javadoc)
         * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(mAFI);
            dest.writeByte(mMaskLength);
            dest.writeByteArray(mMaskValue);
        }
        /* (non-Javadoc)
         * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
         */
        @Override
        public byte[] getBytes() {
            byte[] superData = super.getBytes();
            ByteBuffer buff = ByteBuffer.allocate(superData.length + 2 + mMaskValue.length);
            buff.put(mAFI)
                .put(mMaskLength)
                .put(mMaskValue);
            return buff.array();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("InventoryRequest [" + Util.getHexString(this.getBytes()) + "]\n");
            sb.append(" フラグ:" + Util.getHexString(mFlags) + "\n");
            sb.append(" コマンド:" + ISO15693Lib.commandMap.get(mCommand) + "(" + Util.getHexString(mCommand) + ")\n");
            sb.append(" AFT:" + Util.getHexString(mAFI) + "\n");
            sb.append(" MaskLength:" + Util.getHexString(mMaskLength) + "\n");
            sb.append(" MaskValue:" + Util.getHexString(mMaskValue) + "\n");
            
            return sb.toString();
        }
}
