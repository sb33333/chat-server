import { renderProfile } from './views/profile.js';
import { renderChat } from './views/chat.js';

const app = document.getElementById('app');

function renderPage(page) {
  app.innerHTML = '';

  const container = document.createElement('div');
  container.classList.add('container'); // 공통 스타일

  const header = document.createElement('div');
  header.classList.add('header');
  header.textContent = page === 'chat' ? '채팅방' : '프로필 설정';

  container.appendChild(header);

  if (page === 'chat') {
    renderChat(container, navigateTo);
  } else {
    renderProfile(container, navigateTo);
  }

  app.appendChild(container);
}

function navigateTo(page) {
  history.pushState({ page }, '', `#${page}`);
  renderPage(page);
}

window.addEventListener('popstate', () => {
  const page = location.hash.replace('#', '') || 'profile';
  renderPage(page);
});

document.addEventListener('DOMContentLoaded', () => {
  const page = location.hash.replace('#', '') || 'profile';
  renderPage(page);
});
