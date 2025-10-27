// views/chat.js
import { connectChat, disconnectChat, addChatListener, removeChatListener, sendMessage } from '../services/chatSocket.js';
import { getProfile } from '../services/profile.js';
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
  const nickname = getProfile().username || '익명';
  const avatar = localStorage.getItem('avatar') || '';

  // ===== 메시지 렌더링 =====
  function appendMessage(obj) {
    const el = document.createElement('div');
    if (obj.type === 'SYSTEM') {
      el.className = 'message other';
      el.textContent = `[시스템] ${obj.text}`;
      messagesDiv.appendChild(el);
      return;
    }
    if (obj.type === 'MESSAGE') {
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

  // ===== WebSocket 메시지 수신 리스너 등록 =====
  const listener = (data) => appendMessage(data);
  removeChatListener(listener);
  addChatListener(listener);
  // connectChat(nickname, avatar);

  // ===== 전송 버튼 및 Enter 처리 =====
  function handleSend() {
    const text = input.value.trim();
    if (text) sendMessage(text);
    input.value = '';
  }
  sendBtn.addEventListener('click', handleSend);
  input.addEventListener('keypress', (e) => e.key === 'Enter' && handleSend());

  // ===== 헤더 클릭 시 프로필로 이동 =====
  const header = container.querySelector('.header');
  if (header) {
    header.style.cursor = 'pointer';
    header.addEventListener('click', () => {
      disconnectChat();
      removeChatListener(listener);
      navigateTo('profile');
    });
  }
}