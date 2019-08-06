package com.github.pig.admin.common.license;

import com.github.pig.admin.common.license.GetServerInfo.AbstractServerInfos;
import com.github.pig.admin.common.license.GetServerInfo.LinuxServerInfos;
import com.github.pig.admin.common.license.GetServerInfo.WindowsServerInfos;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.LicenseVo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author: wjqiu
 * @date: 2019-02-23
 * @description:
 */
@Component
public class LicenseManage {
    @Value("${license.filePath}")
    private String licenseFilePath;

    private static final int SPLITLENGTH = 4;
    private static final Logger log = LoggerFactory.getLogger(LicenseManage.class);
    //    private LicenseVo licenseVo;
    private String liclimit_machineCode;
    private Date liclimit_dateStart;
    private Date liclimit_dateEnd;
    private int liclimit_maxMeetingNum;
    private int liclimit_maxViewNum;
    private int liclimit_maxVideoNum;
    private boolean licenseLoaded = false;
    private boolean licenseOk = false;

    private LicenseVo licenseInfo;
    private static HashMap<String, String> genDataFromArrayByte(byte[] b) throws IOException {

        BufferedReader br=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)));
        HashMap<String, String> data = new HashMap<String, String>();
        String str;
        while((str = br.readLine()) != null){
            if(StringUtils.isNotEmpty(str)){
                str = str.trim();
                int pos = str.indexOf("=");
                if(pos <= 0 ) continue;
                if(str.length() > pos + 1){
                    data.put(str.substring(0, pos).trim(), str.substring( pos + 1).trim()) ;
                }else{
                    data.put(str.substring(0, pos).trim(), "") ;
                }
            }
        }
        return data;
    }
    private static String formatMachineCode(String str) {
        return getSplitString(str, "-", SPLITLENGTH);
    }
    private static String getSplitString(String str, String split, int length) {
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % length == 0 && i > 0) {
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs = temp.toString().split(split);
        StringBuilder finalString = new StringBuilder();
        for (String attr : attrs) {
            if (attr.length() == length) {
                finalString.append(attr).append(split);
            }
        }
        String result = finalString.toString().substring(0,
                finalString.toString().length() - 1);
        return result;
    }
    public String getLicenseFilePath(){
        String path = licenseFilePath;
        if (StringUtils.isEmpty(path)){
            path = System.getProperty("user.dir");
        }
        return path+"/"+MeetingConstant.LICENSE_FILE;
    }
    public String getMachineCode(){
        //操作系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        AbstractServerInfos abstractServerInfos;
        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        String serverInfo = abstractServerInfos.getServerInfos().toString();
        log.info("getMachineCode serverInfo:"+serverInfo);
        String code = Encrpt.GetMD5Code(serverInfo);
        log.info("getMachineCode:"+code);
        return formatMachineCode(code);
    }
    private void license_initLimit(){
        liclimit_dateStart = new Date();
        liclimit_dateEnd = new Date();
        liclimit_maxViewNum = 10;
        liclimit_maxVideoNum = 5;
        liclimit_maxMeetingNum = 1;
    }
    private void license_parseLimit(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        //初始化license限制数据
        license_initLimit();

        String dataStart = licenseInfo.getDateStart();
        String dateEnd = licenseInfo.getDateEnd();
        String maxViewNum = licenseInfo.getMaxViewNum();
        String maxVideoNum = licenseInfo.getMaxVideoNum();
        String maxMeetingNum = licenseInfo.getMaxMeetingNum();
        liclimit_machineCode = licenseInfo.getMachineCode();

        try {
            if (StringUtils.isNotEmpty(dataStart)){
                liclimit_dateStart = df.parse(dataStart);
            }
        } catch (Exception e) {
            log.warn("license_parseLimit parse dateStart:{} error",dataStart);
//            e.printStackTrace();
        }
        try {
            if (StringUtils.isNotEmpty(dateEnd)){
                liclimit_dateEnd = df.parse(dateEnd);
            }
        } catch (Exception e) {
            log.warn("license_parseLimit parse dateEnd:{} error",dateEnd);
//            e.printStackTrace();
        }
        try {
            liclimit_maxViewNum = Integer.parseInt(maxViewNum);
            if (liclimit_maxViewNum < 0 || liclimit_maxViewNum > 20000){
                log.info("license_parseLimit parse maxViewNum error! set liclimit_maxViewNum=20000");
                liclimit_maxViewNum = 20000;
            }
        } catch (Exception e) {
            log.warn("parse maxViewNum:{} error",maxViewNum);
        }
        try {
            liclimit_maxVideoNum = Integer.parseInt(maxVideoNum);
            if (liclimit_maxVideoNum < 0 || liclimit_maxVideoNum > 20000) {
                log.info("license_parseLimit parse maxVideoNum error! set liclimit_maxVideoNum=20000");
                liclimit_maxVideoNum = 20000;
            }
        } catch (Exception e) {
                log.warn("parse maxVideoNum:{} error",maxVideoNum);
        }
        try {
            liclimit_maxMeetingNum = Integer.parseInt(maxMeetingNum);
            if (liclimit_maxMeetingNum < 0 || liclimit_maxMeetingNum > 2000){
                log.info("license_parseLimit parse maxMeetingNum error! set liclimit_maxMeetingNum=20000");
                liclimit_maxMeetingNum = 2000;
            }
        } catch (Exception e) {
            log.warn("parse maxMeetingNum:{} error",maxMeetingNum);
        }


        log.info("liclimit_dateStart:{}",liclimit_dateStart);
        log.info("liclimit_dateEnd:{}",liclimit_dateEnd);
        log.info("liclimit_maxViewNum:{}",liclimit_maxViewNum);
        log.info("liclimit_maxVideoNum:{}",liclimit_maxVideoNum);
        log.info("liclimit_maxMeetingNum:{}",liclimit_maxMeetingNum);
        log.info("liclimit_machineCode:{}",liclimit_machineCode);

    }
    @PostConstruct
    void init(){
        log.info("License Manage init");
        //初始化license限制数据
        license_initLimit();

        license_LoadLocal();
    }
    public RespCode licenseVerfify(int countParticipant, int countMeeting){
        RespCode respCode = RespCode.SUCCESS;

        if (!licenseLoaded){
            license_LoadLocal();
        }
        if (licenseOk){
            //检查参会总人数 countParticipant == 0 时则不限制
            if (countParticipant != 0 && countParticipant > liclimit_maxViewNum){
                log.info("licenseVerfify countParticipant:{} > liclimit_maxViewNum:{}",countParticipant,liclimit_maxViewNum);
                respCode = RespCode.IME_LIC_MAX_JOIN_NUMS;
            }
            //检查会议总数 countMeeting == 0 时则不限制
            if (countMeeting != 0 && countMeeting > liclimit_maxMeetingNum){
                log.info("licenseVerfify countMeeting:{} > liclimit_maxMeetingNum:{}",countMeeting,liclimit_maxMeetingNum);
                respCode = RespCode.IME_LIC_MAX_MEETING_NUMS;
            }
            //检查有效期
            Date curDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
            if (liclimit_dateStart == null || liclimit_dateEnd == null || curDate.before(liclimit_dateStart) || curDate.after(liclimit_dateEnd)){
                log.info("licenseVerfify license EXPIRED！ cur :{} ",dateFormat.format(curDate));
                if (liclimit_dateStart != null) log.debug("liclimit_dateStart:{}",dateFormat.format(liclimit_dateStart));
                if (liclimit_dateEnd != null) log.debug("liclimit_dateEnd:{}",dateFormat.format(liclimit_dateEnd));

                respCode = RespCode.IME_LIC_LICENSE_EXPIRED;
            }
        } else {
            log.info("LICENSE_NOT_FOUND");
            respCode = RespCode.IME_LIC_LICENSE_NOT_FOUND;
        }

        return respCode;
    }
    public synchronized boolean license_LoadLocal(){
        boolean bReturn = false;
        licenseLoaded = true;
        InputStream licenseFileStream = null;
        try {
            licenseFileStream = new FileInputStream(getLicenseFilePath());
            if (licenseFileStream != null){
                log.info("license_LoadLocal");
                bReturn = licenseFile_verify(licenseFileStream);
            } else {
                log.warn("load license file fail!");
            }
        } catch (FileNotFoundException e) {
            log.warn("load license file Exception!");
            e.printStackTrace();

        }

        return bReturn;
    }

    public synchronized boolean licenseFile_verify(InputStream inputStream){
        boolean bVerifyOk = false;
        try {
            HashMap<String, String> propsLicenseFile = null;
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            propsLicenseFile = genDataFromArrayByte(bytes);

//            String pubKey = propsLicenseFile.get("pubkey");
            String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEiHSsNLizuyKHLTMHgrDnWL/eplgI6wdG8FXr/hprhwEINznGigf6irDH9TakSgU+Y9gnjc/Zz4uzXCKGYZOTGuCgqteb2Cv47dqMNULjTVnY4nJWFRyLwtQSLb1VH4ZxAYpjw6chcRjT9nZ/CHROuFosr9oeBrwInh/6dLMUbwIDAQAB";
            String licenseinfo = propsLicenseFile.get("licenseinfo");
            log.debug("pubKey:{}",pubKey);
            log.debug("licenseinfo:{}",licenseinfo);
            //            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(keyBytes);
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            //使用公钥解密
            String decriptliccontent = Encrpt.DecriptWithRSA_Pub(licenseinfo,publicKey);
            String machinecode = null;
            String licensesign = null;
            String alllicenseInfoString = null;
            HashMap<String, String> props = null;
            if (StringUtils.isNotEmpty(decriptliccontent)){

                props = genDataFromArrayByte(decriptliccontent.getBytes());
                if (props.size() > 0){
                    machinecode = props.get("machineCode");
                    licensesign = props.get("licenseSign");
                    int pos = decriptliccontent.indexOf("licenseSign=");
                    alllicenseInfoString = decriptliccontent.substring(0,pos);
                    log.info("license machinecode:{}",machinecode);
                    log.debug("licensesign:{}",licensesign);
                }

            }

//            log.debug("decriptliccontent:"+decriptliccontent);
//            log.debug("licenseinfo:"+licenseinfo);
//            log.debug("alllicenseInfoString:"+alllicenseInfoString);
            if (StringUtils.isNotEmpty(licensesign) &&
                    StringUtils.isNotEmpty(alllicenseInfoString) &&
                    licensesign.equals(Encrpt.GetMD5Code(alllicenseInfoString))) {
                //校验内容
                log.debug("license_sign:ok");
                String machinecode_this = getMachineCode();
                if (StringUtils.isNotEmpty(machinecode) && machinecode_this.equals(machinecode))
                {
                    log.info("licenseFile_verify:ok");
                    bVerifyOk = true;
                    if (props != null)
                    {
                        licenseInfo = new LicenseVo();
                        licenseInfo.setDateStart(props.get("dateStart"));
                        licenseInfo.setDateEnd(props.get("dateEnd"));
                        licenseInfo.setMaxViewNum(props.get("maxViewNum"));
                        licenseInfo.setMaxVideoNum(props.get("maxVideoNum"));
                        licenseInfo.setMaxMeetingNum(props.get("maxMeetingNum"));
                        licenseInfo.setMachineCode(props.get("machineCode"));

                        license_parseLimit();
                    }
                } else {
                    log.warn("machinecode:false");
                }
            } else {
                log.warn("lincense_sign:false");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        licenseOk = bVerifyOk;
        return bVerifyOk;
    }

    public LicenseVo getLicenseInfo() {
        return licenseInfo;
    }

}
