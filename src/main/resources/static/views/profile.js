/**
 * 1) 아이디 입력 및 setUsername으로 저장
 * 2) 프로필 사진 업로드하고 setProfileImageId로 파일 id를 저장해두기 
 */

import { upload } from "../services/files.js";
import { setUsername, setProfileImageId } from "../services/userInfo.js";

// views/profile.js

export function renderProfile(container, navigateTo) {
  const div = document.createElement('div');
  div.className = 'container';

  div.innerHTML = `
    <div class="profile-setup">
      <h2>프로필 설정</h2>
      <div class="avatar-preview">
        <div id="avatarCircle" class="avatar-circle">U</div>
      </div>
      <input type="file" id="avatarInput" accept="image/*" />
      <label for="avatarInput">아바타 업로드</label>
      <input type="text" id="nickname" placeholder="닉네임을 입력하세요" />
      <button class="enter-chat-btn" id="enterChat">채팅방 입장</button>
    </div>
  `;

  container.appendChild(div);

  const nicknameInput = div.querySelector('#nickname');
  const avatarInput = div.querySelector('#avatarInput');
  const enterBtn = div.querySelector('#enterChat');
  const avatarCircle = div.querySelector('#avatarCircle');

  // ========== 1️⃣ nickname 입력 시 아바타에 반영 ==========
  nicknameInput.addEventListener('input', () => {
    const name = nicknameInput.value.trim();
    if (avatarCircle.dataset.hasImage !== 'true') {
      avatarCircle.textContent = name ? name[0].toUpperCase() : 'U';
      avatarCircle.style.background = getRandomGradient();
    }
  });

  // ========== 2️⃣ 이미지 선택 시 미리보기 반영 ==========
  avatarInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) {
      avatarCircle.dataset.hasImage = 'false';
      avatarCircle.textContent = nicknameInput.value.trim()[0] || 'U';
      avatarCircle.style.background = getRandomGradient();
      avatarCircle.style.backgroundImage = 'none';
      return;
    }

    const reader = new FileReader();
    reader.onload = function (event) {
      avatarCircle.style.backgroundImage = `url(${event.target.result})`;
      avatarCircle.style.backgroundSize = 'cover';
      avatarCircle.style.backgroundPosition = 'center';
      avatarCircle.textContent = '';
      avatarCircle.dataset.hasImage = 'true';
    };
    reader.readAsDataURL(file);
  });

  // ========== 3️⃣ 채팅방 입장 버튼 ==========
  enterBtn.addEventListener('click', () => {
    const nickname = nicknameInput.value.trim();
    if (!nickname) {
      alert('닉네임을 입력해주세요.');
      return;
    }

    navigateTo('chat', { nickname });
  });
}

// ========== 🎨 랜덤 그라데이션 함수 ==========
function getRandomGradient() {
  const colors = [
    ['#ff9a9e', '#fad0c4'],
    ['#a1c4fd', '#c2e9fb'],
    ['#fbc2eb', '#a6c1ee'],
    ['#84fab0', '#8fd3f4'],
    ['#fccb90', '#d57eeb']
  ];
  const [start, end] = colors[Math.floor(Math.random() * colors.length)];
  return `linear-gradient(135deg, ${start}, ${end})`;
}
