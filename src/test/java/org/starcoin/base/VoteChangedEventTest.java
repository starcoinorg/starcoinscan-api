package org.starcoin.base;

import com.novi.serde.DeserializationError;
import org.junit.jupiter.api.Test;

class VoteChangedEventTest {


    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    @Test
    void bcsDeserialize() throws DeserializationError {
        String hexStr = "0000000000000000b2aa52f94db4516c5beecef363af850ab2aa52f94db4516c5beecef363af850a0100ca9a3b000000000000000000000000";
        byte[] voteBytes = hexToByteArray(hexStr);
        VoteChangedEvent voteEvent = VoteChangedEvent.bcsDeserialize(voteBytes);
        assert voteEvent.agree == true;
        assert voteEvent.proposal_id == 0;
    }
}