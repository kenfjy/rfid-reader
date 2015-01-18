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
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ErrorCode;
import nfclib.com.example.kenfujiyoshi.myapp01.iso15693.lib.ISO15693Lib.ResponseFormat;
import nfclib.com.example.kenfujiyoshi.myapp01.util.Util;

/**
 * Write系コマンド(singleBlock, multipleBlocks)のレスポンスクラスを提供します
 * 
 * @author Kazzz
 * @date 2011/07/15
 * @since Android API Level 10
 *
 */

public class WriteResponse extends ResponseFormat {
    /**
     * コンストラクタ
     * @param in 入力するパーセル化オブジェクトをセット
     */
    public WriteResponse(Parcel in) {
        super(in);
    }
    /**
     * コンストラクタ 
     * @param bytes IDmの格納されているバイト列をセットします
     */
    public WriteResponse(byte[] bytes) {
        super(bytes);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WriteSingleBlockResponse [" + Util.getHexString(this.getBytes()) + "]\n");
        sb.append("　フラグ:" + Util.getHexString(mFlags) + "\n");
        sb.append("　エラーコード:" +   Util.getHexString(mErrorCode) + "(" + ErrorCode.errorMap.get(mErrorCode) + ")\n");
        return sb.toString();
    }
}
