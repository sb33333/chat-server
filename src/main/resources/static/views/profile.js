import { upload } from "../services/files.js";
import { setProfile, getProfile } from "../services/profile.js";

import * as ChatSocket from "../services/chatSocket.js";

export function renderProfile(container, navigateTo) {
  const profileDiv = document.createElement('div');
  profileDiv.classList.add('profile-setup');

  profileDiv.innerHTML = `
    <h2>프로필 설정</h2>
    <div class="avatar-preview" id="avatarPreview">
      <img src="https://via.placeholder.com/90?text=Avatar" alt="avatar">
    </div>
    <input type="file" id="avatarInput" accept="image/*">
    <label for="avatarInput">아바타 선택</label>
    <input type="text" id="nickname" placeholder="닉네임을 입력하세요" />
    <button class="enter-chat-btn" id="enterChat">채팅방 입장</button>
    
    <button type="button" id="fileTest">파일 테스트</button>
  `;

  // 아바타 미리보기
  const avatarInput = profileDiv.querySelector('#avatarInput');
  const avatarPreview = profileDiv.querySelector('#avatarPreview img');
  const nicknameInput = profileDiv.querySelector("#nickname");

  nicknameInput.addEventListener("input", e => {
    setProfile(e.target.value);
  });
  avatarInput.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = ev => (avatarPreview.src = ev.target.result);
      reader.readAsDataURL(file);
    }
  });

  // 입장 버튼
  profileDiv.querySelector('#enterChat').addEventListener('click', () => {
    const nickname = profileDiv.querySelector('#nickname').value.trim();
    if (!nickname) {
      alert('닉네임을 입력해주세요.');
      return;
    }
    // localStorage.setItem('nickname', nickname);
    sessionStorage.setItem('nickname', nickname);
    const avatarSrc = avatarPreview.src;
    localStorage.setItem('avatar', avatarSrc);

    ChatSocket.addChatListener(data => {
        var {type} = data;
        switch (type) {
        case "JOIN":
            navigateTo('chat');
            if (avatarInput.files[0]) {
              upload(avatarInput).then(jsonResponse => setProfile(getProfile.username, jsonResponse.id));
            }
            break;
        default: break;
        }
    });
    ChatSocket.connectChat();
    // navigateTo('chat');
  });

  profileDiv.querySelector("#fileTest").addEventListener("click", () => {
    upload(avatarInput)
    .then(fileId => console.log(fileId));
  });

  container.appendChild(profileDiv);
}
