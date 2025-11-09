/**
 * socket 연결하기
 * 채팅
 * 
 * join 메시지 보내면
 * 서버에서는 system으로 전체 메시지 보내기
 * 
 * 채팅 메시지 보내면
 * 서버에서는 broadcast
 * 
 * 새로고침 하면?
 * ws 연결 끊김. -> 서버에서느,ㄴ system으로 전체 메시지 보내기.
 * 로드되면 getUsername, getProfileImgId 사용해서 다시 ws 연결 시도
 */

// views/chat.js
import { connect, disconnect, addChatListener, removeChatListener, sendMessage } from '../util/chatSocket.js';
import { getUsername, getProfileGradient, getProfileImageId } from '../services/userInfo.js';
import * as SimpleChat from '../services/simple-chat.js';

export function renderChat(container, navigateTo) {
  container.innerHTML = `
    <div class="chat-window">
      <div class="chat-messages" id="chatMessages"></div>
      <div class="chat-input">
        <input type="text" id="messageInput" placeholder="메시지를 입력하세요..." autocomplete="off" />
        <button id="sendBtn">➤</button>
      </div>
    </div>
  `;

  const messagesDiv = container.querySelector('#chatMessages');
  const input = container.querySelector('#messageInput');
  const sendBtn = container.querySelector('#sendBtn');

  // const nickname = localStorage.getItem('nickname') || '익명';
  const nickname = getUsername() || '익명';

  // ===== 메시지 렌더링 =====
  function appendMessage(obj) {
    const el = document.createElement('div');
    if (obj.type === SimpleChat.CHAT_MESSAGE_TYPE.SYSTEM) {
      el.className = 'message other';
      el.textContent = `[시스템] ${obj.text}`;
      messagesDiv.appendChild(el);
      return;
    }
    if (obj.type === SimpleChat.CHAT_MESSAGE_TYPE.MESSAGE) {
      const wrapper = document.createElement('div');
      const isSelf = obj.sender === nickname;
      wrapper.className = isSelf ? 'message self' : 'message other';
      wrapper.innerHTML = `
        <strong>${obj.sender}</strong><br/>
        ${escapeHtml(obj.text)}<br/>
        <small>${new Date(obj.timestamp).toLocaleTimeString()}</small>
      `;
      messagesDiv.appendChild(wrapper);
      messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
  }

  function escapeHtml(unsafe) {
    return unsafe
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#039;');
  }

  // ===== 전송 버튼 및 Enter 처리 =====
  function handleSend() {
    const text = input.value.trim();
    if (text) SimpleChat.sendChatMessage(text);
    input.value = '';
  }
  sendBtn.addEventListener('click', handleSend);
  input.addEventListener('keypress', (e) => e.key === 'Enter' && handleSend());

  // ===== 헤더 클릭 시 프로필로 이동 =====
  const header = container.querySelector('.header');
  if (header) {
    header.style.cursor = 'pointer';
    header.addEventListener('click', () => {
      SimpleChat.disconnectChatSocket();
      navigateTo('profile');
    });
  }

  // ===== WebSocket 메시지 수신 리스너 등록 =====
  const listener = (data) => appendMessage(data);
  removeChatListener(listener);
  SimpleChat.connectChatSocket(listener).catch(err => {
    console.error('채팅 서버 연결 실패:', err);
    alert('채팅 서버에 연결할 수 없습니다. 프로필 설정으로 이동합니다.');
    navigateTo('profile');
  });
}