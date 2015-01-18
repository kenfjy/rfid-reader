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

import static nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.COMMAND_GET_SYSTEM_INFORMATION;

import java.nio.ByteBuffer;
import java.util.Arrays;

import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.RequestFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.UID;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * GetSystemInformationコマンドを発行するリクエストクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/15
 * @since Android API Level 10
 *
 */

public class SystemInformationRequest extends RequestFormat {
    public static final Parcelable.Creator<SystemInformationRequest> CREATOR = 
        new Parcelable.Creator<SystemInformationRequest>() {
            public SystemInformationRequest createFromParcel(Parcel in) {
                return new SystemInformationRequest(in);
            }
            
            public SystemInformationRequest[] newArray(int size) {
                return new SystemInformationRequest[size];
            }
        };

    final UID mUID;
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public SystemInformationRequest(Parcel in) {
        super(in);
        mCommand = in.readByte();
        mUID = new UID(in);
    }
    /**
     * コンストラクタ 
     * @param bytes 格納されているバイト列をセットします
     */
    public SystemInformationRequest(byte[] bytes) {
        super(bytes);
        mCommand = COMMAND_GET_SYSTEM_INFORMATION;
        mUID = new UID(Arrays.copyOfRange(bytes, 2, 10));
    }
    /**
     * コンストラクタ
     * @param flags オプションフラグをセット
     * @param data データをセット
     */
    public SystemInformationRequest(byte flags, UID uid) {
        super(flags, COMMAND_GET_SYSTEM_INFORMATION);
        mUID = uid;
    }
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        mUID.writeToParcel(dest, flags);
    }
    /* (non-Javadoc)
     * @see net.com.example.kenfujiyoshi.myapp01.felica.IISO15693ByteData#getBytes()
     */
    @Override
    public byte[] getBytes() {
        byte[] superData = super.getBytes();
        ByteBuffer buff = ByteBuffer.allocate(superData.length + mUID.getBytes().length);
        buff.put(superData)
            .put(mUID.getBytes());
        return buff.array();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GetSystemInformationRequest [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append(" オプションフラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append(" コマンド:" + ISO15693Lib.commandMap.get(mCommand) + "(" + Util.getHexString(mCommand) + ")\n");
        sb.append(" " + mUID.toString() + "\n");
        return sb.toString();
    }
}
