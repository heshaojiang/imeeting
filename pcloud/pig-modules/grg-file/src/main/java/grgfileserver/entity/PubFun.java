package grgfileserver.entity;

public class PubFun {
    public static String StrAddMask(String str){
        String strWithMask;
        char[] cStr = str.toCharArray();
        int i;
        for (i=3; i<=str.length()-3; i++){
            cStr[i] = '*';
        }
        strWithMask = new String(cStr);//Arrays.toString(cStr);
        return strWithMask;
    }

}
