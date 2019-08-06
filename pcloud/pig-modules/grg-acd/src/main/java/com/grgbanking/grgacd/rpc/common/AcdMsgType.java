/*
 * (C) Copyright 2017-2019 OpenVidu (https://openvidu.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grgbanking.grgacd.rpc.common;

/**
 * @author wjqiu
 * @date 2019-05-29
 * @description
 */
public class AcdMsgType {

	// ---------------------------- CLIENT REQUESTS -----------------------
	public static final String LOGIN_METHOD = "login";
	public static final String LOGOUT_METHOD = "logout";

	// ---------------------------- SERVER RESPONSES & EVENTS -----------------
	public static final String RINGING_EVENT = "RingingEvent";
	public static final String ACCEPTCALL_EVENT = "AcceptCallEvent";
	public static final String JOINCALL_EVENT = "JoinCallEvent";
	public static final String REJECTCALL_EVENT = "RejectCallEvent";
	public static final String HANGUP_EVENT = "HangupEvent";
	public static final String TIMEOUT_EVENT = "TimeoutEvent";
	public static final String AGENT_STATUS_CHANGE_EVENT = "AgentStatusChange";
}
