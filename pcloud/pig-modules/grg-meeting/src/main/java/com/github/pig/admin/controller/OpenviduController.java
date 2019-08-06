package com.github.pig.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.github.pig.admin.common.meeting.CacheFunctions;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.RespEntity;
import com.github.pig.admin.service.IvxOpenviduService;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/sessions")
public class OpenviduController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OpenviduController.class);

    @Autowired
    private IvxOpenviduService openviduService;

    @Autowired
    private CacheFunctions cacheFunctions;

    public OpenviduController() {
    }

    @RequestMapping(value = "/UpdateSessions", method = RequestMethod.POST)
    public RespEntity UpdateSessions(@RequestBody Map<String, Object> data){
        log.info("UpdateSessions" + data);
        String publicUrl = (String) data.get("publicUrl");
        List<String> sessions = (ArrayList<String>) data.get("sessions");
        //先取出缓存中保存的队列。用于匹配是否有增减
        List<String> listSessionidCache = cacheFunctions.CacheGetSessionOneServer(publicUrl,sessions);
        List<String> listSessionid = new ArrayList<>();
        for (String sessionUrl : sessions) {
            log.info("get:" + sessionUrl);
            String sessionid = openviduService.GetSessionIdWithUrl(sessionUrl);
            log.info("get sessionid:" + sessionid);
            listSessionid.add(sessionid);
            cacheFunctions.CacheSetSession(sessionid,publicUrl);
            log.info("get CacheGetSession:" + cacheFunctions.CacheGetSession(sessionid));
        }
        //把当前的session信息保存到缓存中。
        cacheFunctions.CacheSetSessionOneServer(publicUrl,listSessionid);

//        openviduService.CacheSessionDeleteNotExist(listSessioRemoveOneSessionsnid);

        for (String sKey : listSessionidCache) {
            log.info("listSessionidCache sKey={}",sKey);
            if (!listSessionid.contains(sKey)){
                RemoveOneSessions(sKey);
            }
        }

        return new RespEntity(RespCode.SUCCESS);
    }

    public void RemoveOneSessions(String sessionId){
        log.info("RemoveOneSessions sessionId={}",sessionId);
        String meetingId = cacheFunctions.GetMeetingidWithSessionid(sessionId,null);
        if (meetingId.length() > 0){
            cacheFunctions.RemoveMeetingSession(meetingId);
        }
        cacheFunctions.CacheRemoveSession(sessionId);
        cacheFunctions.RemoveMeetingidWithSessionid(sessionId);
    }

//    @RequestMapping(value = "/remove-user", method = RequestMethod.POST)
//    public ResponseEntity<JSONObject> removeUser(@RequestBody String sessionNameToken, HttpSession httpSession)
//            throws Exception {
//
//        try {
//            checkUserLogged(httpSession);
//        } catch (Exception e) {
//            return getErrorResponse(e);
//        }
//        System.out.println("Removing user | {sessionName, token}=" + sessionNameToken);
//
//        // Retrieve the params from BODY
//        JSONObject sessionNameTokenJSON = (JSONObject) new JSONParser().parse(sessionNameToken);
//        String sessionName = (String) sessionNameTokenJSON.get("sessionName");
//        String token = (String) sessionNameTokenJSON.get("token");
//
//        // If the session exists ("TUTORIAL" in this case)
//        if (this.mapSessions.get(sessionName) != null) {
//            String sessionId = this.mapSessions.get(sessionName).getSessionId();
//
//            if (this.mapSessionIdsTokens.containsKey(sessionId)) {
//                // If the token exists
//                if (this.mapSessionIdsTokens.get(sessionId).remove(token) != null) {
//                    // User left the session
//                    if (this.mapSessionIdsTokens.get(sessionId).isEmpty()) {
//                        // Last user left: session must be removed
//                        this.mapSessions.remove(sessionName);
//                    }
//                    return new ResponseEntity<>(HttpStatus.OK);
//                } else {
//                    // The TOKEN wasn't valid
//                    System.out.println("Problems in the app server: the TOKEN wasn't valid");
//                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//            } else {
//                // The SESSIONID wasn't valid
//                System.out.println("Problems in the app server: the SESSIONID wasn't valid");
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            // The SESSION does not exist
//            System.out.println("Problems in the app server: the SESSION does not exist");
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void checkUserLogged(HttpSession httpSession) throws Exception {
//        if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
//            throw new Exception("User not logged");
//        }
    }

}