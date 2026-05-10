import axios from 'axios';

/**
 * AOGS REST API istemcisi.
 *
 * Vite proxy'si /api isteklerini http://localhost:8080'e yönlendirir
 * (vite.config.js). Bu sayede CORS gerekmez (geliştirmede),
 * üretimde frontend ve backend aynı domain altında servis edilir.
 */
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

// Hata yakalama interceptor'ı — backend'in standart hata formatını
// kullanıcı dostu mesaja dönüştürür.
api.interceptors.response.use(
  (resp) => resp,
  (err) => {
    const mesaj = err?.response?.data?.mesaj
              ?? err?.message
              ?? 'Bilinmeyen sunucu hatası';
    return Promise.reject(new Error(mesaj));
  }
);

// ---------------- Endpoint kümeleri ----------------

export const aracApi = {
  liste: (durum) => api.get('/araclar', { params: durum ? { durum } : {} }).then(r => r.data),
  bul:   (id)    => api.get(`/araclar/${id}`).then(r => r.data),
  ara:   (q)     => api.get('/araclar/ara', { params: { q } }).then(r => r.data),
  ekle:  (arac)  => api.post('/araclar', arac).then(r => r.data),
  guncelle: (id, arac) => api.put(`/araclar/${id}`, arac).then(r => r.data),
  sil:   (id)    => api.delete(`/araclar/${id}`),
};

export const musteriApi = {
  liste:    ()    => api.get('/musteriler').then(r => r.data),
  tcAra:    (tc)  => api.get(`/musteriler/tc/${tc}`).then(r => r.data),
  ada:      (q)   => api.get('/musteriler/ara', { params: { q } }).then(r => r.data),
  ekle:     (m)   => api.post('/musteriler', m).then(r => r.data),
  guncelle: (id, m) => api.put(`/musteriler/${id}`, m).then(r => r.data),
  sil:      (id)  => api.delete(`/musteriler/${id}`),
};

export const satisApi = {
  liste:     ()         => api.get('/satislar').then(r => r.data),
  bul:       (id)       => api.get(`/satislar/${id}`).then(r => r.data),
  yap:       (istek)    => api.post('/satislar', istek).then(r => r.data),
  iptal:     (id)       => api.post(`/satislar/${id}/iptal`),
  fatura:    (id)       => api.get(`/satislar/${id}/fatura`).then(r => r.data),
  musteriSatislari: (tc) => api.get(`/satislar/musteri/${tc}`).then(r => r.data),
};

export const ekspertizApi = {
  liste:     ()         => api.get('/ekspertizler').then(r => r.data),
  son:       ()         => api.get('/ekspertizler/son').then(r => r.data),
  aracaGore: (aracId)   => api.get(`/ekspertizler/arac/${aracId}`).then(r => r.data),
  ekle:      (e)        => api.post('/ekspertizler', e).then(r => r.data),
  guncelle:  (id, e)    => api.put(`/ekspertizler/${id}`, e).then(r => r.data),
  sil:       (id)       => api.delete(`/ekspertizler/${id}`),
};

export const bildirimApi = {
  dashboard:    ()      => api.get('/bildirimler/dashboard').then(r => r.data),
  yaklasanSig:  (gun=30)=> api.get('/bildirimler/sigorta/yaklasan', { params: { gun } }).then(r => r.data),
  yaklasanMua:  (gun=30)=> api.get('/bildirimler/muayene/yaklasan', { params: { gun } }).then(r => r.data),
  sigortaEkle:  (s)     => api.post('/bildirimler/sigorta', s).then(r => r.data),
  sigortaGuncelle:(id, s) => api.put(`/bildirimler/sigorta/${id}`, s).then(r => r.data),
  sigortaSil:   (id)    => api.delete(`/bildirimler/sigorta/${id}`),
  muayeneEkle:  (m)     => api.post('/bildirimler/muayene', m).then(r => r.data),
  muayeneGuncelle:(id, m) => api.put(`/bildirimler/muayene/${id}`, m).then(r => r.data),
  muayeneSil:   (id)    => api.delete(`/bildirimler/muayene/${id}`),
};

export const raporApi = {
  ozet:           ()           => api.get('/raporlar/ozet').then(r => r.data),
  gelirGider:     (b, bt)      => api.get('/raporlar/gelir-gider', { params: { baslangic: b, bitis: bt } }).then(r => r.data),
  aylikSatis:     (yil)        => api.get('/raporlar/aylik-satis', { params: { yil } }).then(r => r.data),
  markaSatis:     ()           => api.get('/raporlar/marka-satis').then(r => r.data),
  odemeSekli:     ()           => api.get('/raporlar/odeme-sekli').then(r => r.data),
  calisanPerf:    ()           => api.get('/raporlar/calisan-performans').then(r => r.data),
  stokDeger:      ()           => api.get('/raporlar/stok-deger').then(r => r.data),
};

export const calisanApi = {
  liste:    ()    => api.get('/calisanlar').then(r => r.data),
  bul:      (id)  => api.get(`/calisanlar/${id}`).then(r => r.data),
  ekle:     (c)   => api.post('/calisanlar', c).then(r => r.data),
  guncelle: (id, c) => api.put(`/calisanlar/${id}`, c).then(r => r.data),
  sil:      (id)  => api.delete(`/calisanlar/${id}`),
};

export default api;
