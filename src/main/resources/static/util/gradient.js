// ========== 🎨 랜덤 그라데이션 함수 ==========
export default function generateRandomGradient() {
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