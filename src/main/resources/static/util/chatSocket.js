"use strict";
import {getUsername, getProfileImageId} from "../services/userInfo.js";

let socket = null;
let listeners = [];
let openListeners = [];
let reconnectTimeout = 1000; // 재접속 대기(ms)
let heartbeatInterval = null;

// ========== 이벤트 리스너 관리 ==========
export function addChatListener(fn) {
  listeners.push(fn);
  return listeners.filter(f => f !== fn);
}
export function removeChatListener(fn) {
  listeners = listeners.filter(f => f !== fn);
}

function clearListeners() {
  listeners = [];
  openListeners = [];
}

export function addOpenListener(fn) {
  openListeners.push(fn);
  return openListeners.filter(f => f !== fn);
}

export function removeOpenListener(fn) {
  openListeners.filter(f => f !== fn);
}


// ========== 메시지 전송 ==========
export function sendMessage(message) {
  if (!socket || socket.readyState !== WebSocket.OPEN) return;
  switch (typeof message) {
    case 'string':
      socket.send(message);
      break;
    case 'object':
      socket.send(JSON.stringify(message));
      break;
    default:
      console.warn('unsupported message type:', typeof message);
  }
  // socket.send(JSON.stringify({ type: 'message', text }));
  // socket.send(JSON.stringify({ type: "MESSAGE", text, sender: getUsername(), imgId: getProfileImageId() }));
}

// ========== 연결 관리 ==========
export async function connect(wsUrl, connectionQueryString) {
  if (socket && socket.readyState === WebSocket.OPEN) return;

  var _url = wsUrl + (connectionQueryString ? `?${connectionQueryString}` : '');
  socket = new WebSocket(_url);

  socket.addEventListener('open', () => {
    console.log('[ChatSocket] connected');
    reconnectTimeout = 1000;
    openListeners.forEach(fn => fn());

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
      connect(wsUrl, connectionQueryString);
    }, reconnectTimeout);
  });

  socket.addEventListener('error', (err) => {
    console.error('[ChatSocket] error', err);
    socket.close();
  });
}

export function disconnect() {
  if (socket) {
    console.log('[ChatSocket] disconnecting');
    clearInterval(heartbeatInterval);
    clearListeners();
    socket.close();
    socket = null;
  }
}