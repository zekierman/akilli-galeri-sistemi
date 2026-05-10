/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx}',
  ],
  theme: {
    extend: {
      // PDR §1.2.7'deki AOGS Renk Paleti — Tailwind utility class'larına
      // dönüştürülmüş hali. Eski JavaFX'te String sabiti olarak tanımlıydı,
      // şimdi class-based ve responsive.
      colors: {
        aogs: {
          birincil: '#FF6B35',  // Ana turuncu
          ikincil:  '#F7931E',  // Açık turuncu
          arkaplan: '#FFFBF7',  // Kirli beyaz
          kart:     '#FFFFFF',  // Beyaz kart
          kenar:    '#FFE5D0',  // Soft turuncu kenar
          metin:    '#2C3E50',  // Koyu metin
          'metin-hafif': '#7F8C8D', // Gri metin
          basari:   '#27AE60',  // Yeşil
          uyari:    '#E74C3C',  // Kırmızı (hata)
          uyariSari:'#F39C12',  // Sarı (uyarı)
          sidebar:  '#2C3E50',  // Koyu sidebar
        },
      },
      boxShadow: {
        'aogs-kart': '0 2px 8px 0 rgba(255, 107, 53, 0.08)',
      },
    },
  },
  plugins: [],
};
