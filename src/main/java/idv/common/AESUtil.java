package idv.common;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESUtil {

    // 密钥 (需要前端和后端保持一致)十六位作为密钥
    private static final String KEY = "61CCB8FB5B706943";
    // 密钥偏移量 (需要前端和后端保持一致)十六位作为密钥偏移量
    private static final String IV = "C0F987A69EDD4D34";
    // 算法
    private static final String ALGORITHMSTR = "AES/CBC/PKCS5Padding";

    /**
     * ==============================================AES加密
     * @param rawStr 待加密的
     * @return 加密后的String
     * @throws Exception Exception
     */
    public static String aesEncrypt(String rawStr) throws Exception {
        if (StringUtils.isNotBlank(rawStr)){
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            byte[] temp = IV.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(temp);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY.getBytes(), "AES"), iv);
            byte[] encryptBytes = cipher.doFinal(rawStr.getBytes());
            return base64Encoder(encryptBytes).replace("\n", "");
        }
        return null;
    }

    /**
     * AES===========解密
     * @param encryptStr 待解密的
     * @return 解密后的String
     * @throws Exception Exception
     */
    public static String aesDecrypt(String encryptStr) throws Exception {
        if (StringUtils.isNotBlank(encryptStr)){
            encryptStr = encryptStr.replace("_", "/").replace("-", "+");
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            byte[] temp = IV.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(temp);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.getBytes(), "AES"), iv);
            byte[] decryptBytes = cipher.doFinal(base64Decode(encryptStr));
            return new String(decryptBytes);
        }
        return null;
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : new Base64().decode(base64Code);
    }

    /**
     * base 64 加码
     */
    public static String base64Encoder(byte[] base64Code){
        String dncryptedText = new Base64().encodeAsString(base64Code);
        return dncryptedText.replace("+", "-").replace("/", "_")
                .replace("\r\n", "");
    }

}
