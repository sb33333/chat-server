"use strict";
import {getUsername, getProfileImageId} from "./userInfo.js";

const WS_URL = 'ws://'+window.location.host+'/chat'; // 운영 시 wss://로 변경
let socket = null;
let listeners = [];
let reconnectTimeout = 1000; // 재접속 대기(ms)
let heartbeatInterval = null;

// ========== 이벤트 리스너 관리 ==========
export function addChatListener(fn) {
  listeners.push(fn);
}
export function removeChatListener(fn) {
  listeners = listeners.filter(f => f !== fn);
}

// ========== 메시지 전송 ==========
export function sendMessage(text) {
  if (!socket || socket.readyState !== WebSocket.OPEN) return;
  // socket.send(JSON.stringify({ type: 'message', text }));
  socket.send(JSON.stringify({ type: "MESSAGE", text, sender: getUsername(), imgId: getProfileImageId() }));
}

// ========== 연결 관리 ==========
export async function connectChat(nickname, avatar) {
  if (socket && socket.readyState === WebSocket.OPEN) return;

  var isOpened =  false;
  isOpened = (await fetch("/chat").then(res => res.text()).then(txt => txt.trim())) === "true";

  if (!isOpened) throw new Error("session is not opened.");
  
  // if (!profile?.username) throw new Error("username is required.");
  socket = new WebSocket(WS_URL + "?user="+profile.username);

  socket.addEventListener('open', () => {
    console.log('[ChatSocket] connected');
    reconnectTimeout = 1000;
    socket.send(JSON.stringify({ type: 'JOIN', nickname, avatar }));

    heartbeatInterval = setInterval(() => {
      if (socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ type: 'PING' }));
      }
    }, 20000);
  });

  socket.addEventListener('message', (ev) => {
    let data;
    try {
      data = JSON.parse(ev.data);
    } catch {
      console.warn('invalid message:', ev.data);
      return;
    }
    listeners.forEach(fn => fn(data));
  });

  socket.addEventListener('close', () => {
    console.log('[ChatSocket] closed — reconnecting in', reconnectTimeout, 'ms');
    clearInterval(heartbeatInterval);
    socket = null;
    setTimeout(() => {
      reconnectTimeout = Math.min(30000, reconnectTimeout * 1.5);
      connectChat(nickname, avatar);
    }, reconnectTimeout);
  });

  socket.addEventListener('error', (err) => {
    console.error('[ChatSocket] error', err);
    socket.close();
  });
}

export function disconnectChat() {
  if (socket) {
    console.log('[ChatSocket] disconnecting');
    clearInterval(heartbeatInterval);
    socket.close();
    socket = null;
  }
}