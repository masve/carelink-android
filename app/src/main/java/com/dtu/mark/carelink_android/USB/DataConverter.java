package com.dtu.mark.carelink_android.USB;

/**
 * Created by marksv on 9/29/14.
 */
public class DataConverter {
    /**
     * Converts an array string of hex values (ex: array[0] = "0x04") to the byte array representation.
     * @param command
     * @return
     */
    public static byte[] hexStringArrayToByteArray(String[] command) {
        byte[] opcode = new byte[command.length];

        for(int i = 0; i < command.length; i++) {
            opcode[i] = Byte.decode(command[i]);
        }

        return opcode;
    }

    public static String byteArrayToString(byte[] data) {
        String[] something = new String[data.length];
        StringBuilder strb = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            something[i] = Integer.toHexString(data[i]);

            if (something[i].length() == 1) {
                something[i] = "0" + something[i];
            }

            //System.out.println(something[i]);

            if (i < data.length - 1) {
                strb.append(something[i] + ' ');
            }
            else {
                strb.append(something[i]);
            }
        }

        return strb.toString();
    }

    public static String strToJSON(String err, String strData) {
        return  "{\"data:\":\"" + strData + "\", \"err\":\"" + err + "\"}";
    }
}
