"use strict";

import * as ChatSocket from "../util/chatSocket.js";
import * as UserInfo from "./userInfo.js";

const CHAR_RESOURCE = "/chat";
const WS_URL = 'ws://'+window.location.host+CHAR_RESOURCE;
export const CHAT_MESSAGE_TYPE = Object.freeze({
  MESSAGE: "MESSAGE",
  SYSTEM: "SYSTEM"
});

const onopen = () => {
  ChatSocket.sendMessage({
    type: CHAT_MESSAGE_TYPE.SYSTEM,
    sender: UserInfo.getUsername(),
    message: `${UserInfo.getUsername()}님이 입장하셨습니다.`,
  });
};

const chatMessageFormat = (() => {
  var background = UserInfo.getProfileImageId();
  if (!background) background = UserInfo.getProfileGradient();
  
  return (message) => {
    return {
      type: CHAT_MESSAGE_TYPE.MESSAGE,
      sender: UserInfo.getUsername(),
      text: message,
      profileImg: background
    }
  }
})();

export async function connectChatSocket(...onMessageHandlers) {
  const username = UserInfo.getUsername();
  if (!username) throw new Error("username is required.");
  const connectionQueryString = `username=${encodeURIComponent(username)}`;

  var isOpened =  false;
  isOpened = (await fetch(CHAR_RESOURCE).then(res => res.text()).then(txt => txt.trim())) === "true";
  if (!isOpened) throw new Error("session is not opened.");

  ChatSocket.addOpenListener(onopen);
  onMessageHandlers.forEach(fn => ChatSocket.addChatListener(fn));
  return ChatSocket.connect(WS_URL, connectionQueryString);
}

export function sendChatMessage(message) {
  ChatSocket.sendMessage(chatMessageFormat(message));
}

export function disconnectChatSocket() {
  ChatSocket.disconnect();
}