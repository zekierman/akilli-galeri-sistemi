/**
 * AOGS Tema Token'ları.
 * --------------------
 * Sorumlu: Emre Kuzal (Raporlama modülü) + Eray Gök (UI altyapısı).
 *
 * Bu dosya, Tailwind utility class'larıyla ifade edilemeyen JavaScript
 * tarafı tema sabitlerini barındırır. Tailwind sınıfları HTML/JSX
 * içinde yeterlidir; ancak Recharts gibi kütüphaneler stil olarak hex
 * değer ister — o değerler buradan tek bir kaynaktan okunur.
 *
 * Hex değerleri `tailwind.config.js` ile **birebir** aynı tutulmalıdır.
 * Yeni bir renk eklenirken her iki dosya da aynı PR içinde güncellenir.
 *
 * Kullanım örneği:
 *   import { GRAFIK_RENKLERI, formatTl } from '@/theme/tokens';
 *   <Bar dataKey="adet" fill={GRAFIK_RENKLERI.birincil} />
 */

// =====================================================================
// Renk Paleti — tailwind.config.js ile senkron
// =====================================================================

/** Ham marka renkleri. Tailwind `aogs-*` namespace'i ile aynı değerler. */
export const RENKLER = Object.freeze({
  birincil:    '#FF6B35',  // Ana turuncu, vurgular
  ikincil:     '#F7931E',  // Açık turuncu, hover
  arkaplan:    '#FFFBF7',  // Kirli beyaz, genel arka plan
  kart:        '#FFFFFF',  // Beyaz kart
  kenar:       '#FFE5D0',  // Soft turuncu border
  metin:       '#2C3E50',  // Koyu metin
  metinHafif:  '#7F8C8D',  // Gri yardımcı metin
  basari:      '#27AE60',  // Yeşil — kâr, başarılı işlem
  uyari:       '#E74C3C',  // Kırmızı — hata, kritik
  uyariSari:   '#F39C12',  // Sarı — dikkat
  sidebar:     '#2C3E50',  // Sidebar arka plan
});

// =====================================================================
// Recharts için tematik paletler
// =====================================================================

/**
 * Pasta/donut grafikleri için 5'li turuncu degrade.
 * Ödeme şekli, marka dağılımı vb. kategorik dağılımlarda kullanılır.
 */
export const TURUNCU_PALET = Object.freeze([
  '#FF6B35',  // birincil
  '#F7931E',  // ikincil
  '#FFA66B',  // orta açık turuncu
  '#FFC59E',  // açık turuncu
  '#FFE5D0',  // kenar
]);

/**
 * Çok seriyli grafikler için anlamlı renk eşlemesi.
 * Tek bir grafikte birden fazla metriği ayırt etmek için kullanılır.
 */
export const GRAFIK_RENKLERI = Object.freeze({
  birincil:  RENKLER.birincil,    // Ana seri (genelde adet/satış)
  ikincil:   RENKLER.ikincil,     // İkinci seri (genelde tutar)
  basari:    RENKLER.basari,      // Pozitif değer (kâr, ciro)
  uyari:     RENKLER.uyari,       // Negatif değer (zarar, gider)
  uyariSari: RENKLER.uyariSari,   // Nötr/dikkat değer (stok)
  izgara:    RENKLER.kenar,       // CartesianGrid çizgi rengi
  eksen:     RENKLER.metinHafif,  // XAxis/YAxis tick rengi
});

// =====================================================================
// Format yardımcıları
// =====================================================================

/** Türk Lirası formatlayıcı (önbelleklenmiş — her çağrıda yeniden oluşturulmaz). */
const tlFormatter = new Intl.NumberFormat('tr-TR', {
  style: 'currency',
  currency: 'TRY',
  maximumFractionDigits: 0,
});

/** Sayısal değer formatlayıcı (binlik ayraçlı). */
const sayiFormatter = new Intl.NumberFormat('tr-TR');

/**
 * Bir sayıyı "₺125.000" formatında TL string'ine çevirir.
 * @param {number|null|undefined} deger
 * @returns {string} formatlanmış TL veya "—"
 */
export function formatTl(deger) {
  if (deger === null || deger === undefined || Number.isNaN(deger)) return '—';
  return tlFormatter.format(deger);
}

/**
 * Bir sayıyı binlik ayraçlı string'e çevirir (1234 → "1.234").
 * @param {number|null|undefined} deger
 * @returns {string}
 */
export function formatSayi(deger) {
  if (deger === null || deger === undefined || Number.isNaN(deger)) return '—';
  return sayiFormatter.format(deger);
}

/**
 * Bir oranı "%12,5" formatında string'e çevirir.
 * @param {number|null|undefined} deger 0-100 aralığında ondalıklı sayı
 * @returns {string}
 */
export function formatYuzde(deger) {
  if (deger === null || deger === undefined || Number.isNaN(deger)) return '—';
  return `%${deger.toFixed(1).replace('.', ',')}`;
}

/** Ay numarasını (1-12) Türkçe ay adına çevirir. */
const AY_ADLARI = [
  'Oca', 'Şub', 'Mar', 'Nis', 'May', 'Haz',
  'Tem', 'Ağu', 'Eyl', 'Eki', 'Kas', 'Ara',
];

/**
 * 1-12 arası ay numarasını "Oca", "Şub" gibi 3 harfli kısaltmaya çevirir.
 * Aylık satış grafiğinin XAxis tick formatter'ı olarak kullanılır.
 */
export function ayKisaltma(ay) {
  return AY_ADLARI[ay - 1] ?? String(ay);
}

/**
 * ISO 8601 tarih string'ini "06.05.2026 14:30" formatına çevirir.
 * @param {string|Date|null|undefined} tarih
 * @returns {string}
 */
export function formatTarih(tarih) {
  if (!tarih) return '—';
  const d = tarih instanceof Date ? tarih : new Date(tarih);
  if (Number.isNaN(d.getTime())) return '—';
  return d.toLocaleString('tr-TR', {
    day:    '2-digit',
    month:  '2-digit',
    year:   'numeric',
    hour:   '2-digit',
    minute: '2-digit',
  });
}
