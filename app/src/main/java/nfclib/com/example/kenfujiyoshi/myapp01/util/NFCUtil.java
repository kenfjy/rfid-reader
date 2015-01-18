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
package nfclib.com.example.kenfujiyoshi.myapp01.util;

import android.nfc.Tag;
import android.nfc.tech.TagTechnology;

/**
 * NFCのためのユーティリティクラスを提供します
 * 
 * @author Kazzz.
 * @date 2011/08/04
 * @since Android API Level 10
 *
 */

public class NFCUtil {
    /**
     * Tegテクノロジを持っているか否かを検査します
     * @param tag NFCタグをセット
     * @param klassName 対象のテクノロジクラスをセット
     * @return boolean 対象のテクノロジクラス含んでいる場合はtrueが戻ります
     */
    static public boolean hasTech(Tag tag, String klassName) {
        for (String tech : tag.getTechList()) {
            if (tech.equals(klassName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Tegテクノロジを持っているか否かを検査します
     * @param tag NFCタグをセット
     * @param tech TagTechnologyクラスをセット
     * @return boolean 対象のテクノロジクラス含んでいる場合はtrueが戻ります
     */
    static public boolean hasTech(Tag tag, Class< ? extends TagTechnology> tech) {
        return hasTech(tag, tech.getCanonicalName());
    }
}
